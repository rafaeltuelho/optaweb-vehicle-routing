/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaweb.vehiclerouting.plugin.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.LocationType;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.RouteOptimizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Accumulates vehicles, depots and visits until there's enough data to start the optimization.
 * Solutions are published even if solving hasn't started yet due to missing facts (e.g. no vehicles or no visits).
 * Stops solver when vehicles or visits are reduced to zero.
 */
@Component
class RouteOptimizerImpl implements RouteOptimizer {
    private static final Logger logger = LoggerFactory.getLogger(RouteOptimizerImpl.class);

    private final SolverManager solverManager;
    private final RouteChangedEventPublisher routeChangedEventPublisher;

    private final LocationRepository locationRepository;
    private final DistanceMatrix distanceMatrix;

    private final List<PlanningVehicle> vehicles = new ArrayList<>();
    private final List<PlanningVisit> visits = new ArrayList<>();
    private final List<PlanningDepot> depots = new ArrayList<>();

    @Autowired
    RouteOptimizerImpl(SolverManager solverManager, RouteChangedEventPublisher routeChangedEventPublisher,
            LocationRepository locationRepository, DistanceMatrix distanceMatrix) {
        this.solverManager = solverManager;
        this.routeChangedEventPublisher = routeChangedEventPublisher;
        this.locationRepository = locationRepository;
        this.distanceMatrix = distanceMatrix;
    }

    @Override
    public void addLocation(Location domainLocation, DistanceMatrixRow distanceMatrixRow) {
        PlanningLocation location = PlanningLocationFactory.fromDomain(
                domainLocation,
                new DistanceMapImpl(distanceMatrixRow));
        if (domainLocation.type() == LocationType.DEPOT) {
            PlanningDepot depot = new PlanningDepot(location);
            depots.add(depot);
            if (vehicles.isEmpty()) {
                publishSolution(); // publish just to update the view on client-side (front-end)
            } else if (!visits.isEmpty()) {
                solverManager.addDepot(depot);
            }
        } else { // Does all the other types should be considered Visits?
            PlanningVisit visit = PlanningVisitFactory.fromLocation(location);
            visits.add(visit);
            if (vehicles.isEmpty() || depots.isEmpty()) {
                publishSolution(); // publish just to update the view on client-side (front-end)
            } else if (visits.size() == 1) {
                solverManager.startSolver(SolutionFactory.solutionFromVisits(vehicles, depots, visits));
            } else {
                solverManager.addVisit(visit);
            }
        }
    }

    @Override
    public void removeLocation(Location domainLocation) {
        if (domainLocation.type() == LocationType.VISIT) {
            if (visits.isEmpty()) {
                throw new IllegalArgumentException(
                        "Cannot remove " + domainLocation + " because there are no visit locations");
            } else {
                if (!visits.removeIf(item -> item.getId() == domainLocation.id())) {
                    throw new IllegalArgumentException("Cannot remove " + domainLocation + " because it doesn't exist");
                }

                if (vehicles.isEmpty()) { // solver is not running
                    publishSolution();
                } else if (visits.isEmpty()) { // solver is running
                    solverManager.stopSolver();
                    publishSolution();
                } else {
                    // TODO maybe allow removing location by ID (only require the necessary information)
                    solverManager.removeVisit(
                            PlanningVisitFactory.fromLocation(PlanningLocationFactory.fromDomain(domainLocation)));
                }
            }
        } else if (domainLocation.type() == LocationType.DEPOT) {
            if (depots.isEmpty()) {
                throw new IllegalArgumentException(
                        "Cannot remove " + domainLocation + " because there are no depot locations");
            } else {
                // check is there are any vehicle still associated to this depot
                // TODO: should we remove Depot's vehicles in cascade mode???
                vehicles.stream().filter(
                        (v) -> v.getDepot().getLocation().getId() == domainLocation.id()).findFirst().ifPresent(
                                (v) -> {
                                    throw new IllegalArgumentException(
                                            "Cannot remove " + domainLocation
                                                    + "as it has vehicles associated to it. Make sure you remove all vehicles from this Depot first.");
                                });

                // vehicles.stream().filter(
                //         (v) -> v.getDepot().getLocation().getId() == domainLocation.id()).forEach((v) -> {
                //             removeVehicle(toDomainVehicle(v));
                //         });                

                if (!depots.removeIf(item -> item.getId() == domainLocation.id())) {
                    throw new IllegalArgumentException("Cannot remove " + domainLocation + " because it doesn't exist");
                }

                if (vehicles.isEmpty()) { // solver is not running
                    publishSolution();
                } else if (depots.isEmpty()) { // solver is running
                    solverManager.stopSolver();
                    publishSolution();
                } else {
                    // TODO maybe allow removing location by ID (only require the necessary information)
                    solverManager.removeDepot(new PlanningDepot(PlanningLocationFactory.fromDomain(domainLocation)));
                }
            }
        }
    }

    @Override
    public void addVehicle(Vehicle domainVehicle) {
        PlanningVehicle vehicle = PlanningVehicleFactory.fromDomain(domainVehicle);
        // with multiple Depots scenario Vehicle needs to know 
        //  it's associated Depot (initial/start/home location) upfront!
        //  for now each Vehicle will use it's (initial) location as the PlanningDepot.
        //  in the future we'll consider another approach
        Optional<Location> depotLocation = locationRepository.find(domainVehicle.depotId());
        if (depotLocation.isEmpty())
            throw new IllegalArgumentException("Depot [" + domainVehicle.depotId() + "] not found! This should not happen.");

        DistanceMatrixRow distanceMatrixRow = distanceMatrix.getDistanceMatrix(depotLocation.get());
        PlanningDepot planningDepot = new PlanningDepot(
                PlanningLocationFactory.fromDomain(depotLocation.get(), new DistanceMapImpl(distanceMatrixRow)));

        vehicle.setDepot(planningDepot);

        vehicles.add(vehicle);
        if (visits.isEmpty()) {
            publishSolution();
        } else if (vehicles.size() == 1) {
            solverManager.startSolver(SolutionFactory.solutionFromVisits(vehicles, depots, visits));
        } else {
            solverManager.addVehicle(vehicle);
        }
    }

    @Override
    public void removeVehicle(Vehicle domainVehicle) {
        if (!vehicles.removeIf(vehicle -> vehicle.getId() == domainVehicle.id())) {
            throw new IllegalArgumentException("Cannot remove " + domainVehicle + " because it doesn't exist");
        }
        if (visits.isEmpty()) { // solver is not running
            publishSolution();
        } else if (vehicles.isEmpty()) { // solver is running
            solverManager.stopSolver();
            publishSolution();
        } else {
            solverManager.removeVehicle(PlanningVehicleFactory.fromDomain(domainVehicle));
        }
    }

    @Override
    public void changeCapacity(Vehicle domainVehicle) {
        PlanningVehicle vehicle = vehicles.stream()
                .filter(item -> item.getId() == domainVehicle.id())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot change capacity of " + domainVehicle + " because it doesn't exist"));
        vehicle.setCapacity(domainVehicle.capacity());
        if (!visits.isEmpty()) {
            solverManager.changeCapacity(vehicle);
        } else {
            publishSolution();
        }
    }

    @Override
    public void removeAllLocations() {
        solverManager.stopSolver();
        depots.clear();
        visits.clear();
        publishSolution();
    }

    @Override
    public void removeAllVehicles() {
        solverManager.stopSolver();
        vehicles.clear();
        publishSolution();
    }

    private void publishSolution() {
        routeChangedEventPublisher.publishSolution(SolutionFactory.solutionFromVisits(vehicles, depots, visits));
    }
}
