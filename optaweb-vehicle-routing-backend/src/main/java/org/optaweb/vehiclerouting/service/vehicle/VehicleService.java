/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.service.vehicle;

import static java.util.Comparator.comparingLong;

import java.util.Objects;
import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleData;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.DistanceMapImpl;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.optaweb.vehiclerouting.service.location.RouteOptimizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {

    static final int DEFAULT_VEHICLE_CAPACITY = 10;

    private final RouteOptimizer optimizer;
    private final VehicleRepository vehicleRepository;
    private final DistanceMatrix distanceMatrix;
    private final LocationRepository locationRepository;

    @Autowired
    public VehicleService(RouteOptimizer optimizer, VehicleRepository vehicleRepository,
            LocationRepository locationRepository,
            DistanceMatrix distanceMatrix) {
        this.optimizer = optimizer;
        this.vehicleRepository = vehicleRepository;
        this.locationRepository = locationRepository;
        this.distanceMatrix = distanceMatrix;
    }

    // Each Vehicle must have a location from now on
    // public void createVehicle() {
    //     Vehicle vehicle = vehicleRepository.createVehicle(DEFAULT_VEHICLE_CAPACITY);
    //     addVehicle(vehicle);
    // }

    public void createVehicleWithLocation(VehicleData vehicleData) {
        Vehicle vehicle = null;
        // Optional<Location> location = locationRepository.find(vehicleData.locationData().id());
        // if (!location.isPresent()) {
        // Location vehicleLocation = locationRepository.createLocation(
        //     vehicleData.location().type(), vehicleData.location().coordinates(), vehicleData.location().description());
        vehicle = vehicleRepository.createVehicleWithLocation(vehicleData);
        // vehicleRepository.update(vehicle);
        // }
        // else {
        //     vehicle = vehicleRepository.createVehicle(vehicleData);
        // }

        addVehicle(vehicle);
    }

    public void addVehicle(Vehicle vehicle) {
        // Optional<Location> depotLocation = locationRepository.find(vehicle.depotId());
        // if (depotLocation.isEmpty())
        //     throw new IllegalArgumentException("Depot [" + vehicle.depotId() + "] not found! This should not happen.");

        // DistanceMatrixRow distanceMatrixRow = distanceMatrix.getDistanceMatrix(depotLocation.get());
        // PlanningDepot planningDepot = new PlanningDepot(PlanningLocationFactory.fromDomain(depotLocation.get(), new DistanceMapImpl(distanceMatrixRow)));

        optimizer.addVehicle(Objects.requireNonNull(vehicle));
    }

    public void removeVehicle(long vehicleId) {
        Vehicle vehicle = vehicleRepository.removeVehicle(vehicleId);
        optimizer.removeVehicle(vehicle);
    }

    public synchronized void removeAnyVehicle() {
        Optional<Vehicle> first = vehicleRepository.vehicles().stream().min(comparingLong(Vehicle::id));
        first.ifPresent(vehicle -> {
            Vehicle removed = vehicleRepository.removeVehicle(vehicle.id());
            optimizer.removeVehicle(removed);
        });
    }

    public void removeAll() {
        optimizer.removeAllVehicles();
        vehicleRepository.removeAll();
    }

    public void changeCapacity(long vehicleId, int capacity) {
        Vehicle vehicle = vehicleRepository.find(vehicleId).orElseThrow(
                () -> new IllegalArgumentException("Can't remove Vehicle{id=" + vehicleId + "} because it doesn't exist"));
        Vehicle updatedVehicle = VehicleFactory.createVehicle(vehicle.id(), vehicle.name(), capacity, vehicle.location(), vehicle.depotId());
        vehicleRepository.update(updatedVehicle);
        optimizer.changeCapacity(updatedVehicle);
    }
}
