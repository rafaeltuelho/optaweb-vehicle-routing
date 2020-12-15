/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.persistence;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleData;
import org.optaweb.vehiclerouting.domain.VehicleFactory;
import org.optaweb.vehiclerouting.service.vehicle.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VehicleRepositoryImpl implements VehicleRepository {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRepositoryImpl.class);
    private final VehicleCrudRepository repository;
    private final LocationCrudRepository locationRepository;

    public VehicleRepositoryImpl(VehicleCrudRepository repository, LocationCrudRepository locationRepository) {
        this.repository = repository;
        this.locationRepository = locationRepository;
    }

    // @Override
    // public Vehicle createVehicle(int capacity) {
    //     long id = repository.save(new VehicleEntity(0, null, capacity)).getId();
    //     VehicleEntity vehicleEntity = repository.save(new VehicleEntity(id, "Vehicle " + id, capacity));
    //     Vehicle vehicle = toDomain(vehicleEntity);
    //     logger.info("Created vehicle {}.", vehicle);
    //     return vehicle;
    // }

    @Override
    public Vehicle createVehicleWithLocation(VehicleData vehicleData) {
        // Optional<LocationEntity> locationEntity = locationRepository.findById(vehicleData.location().id());
        // if (!locationEntity.isPresent()) {
        //     //TODO: create the vehicle location or thow an IllegalArgumentException?
        //     logger.warn("Vehicle location {} does not exist yet.", vehicleData.location());
        Location domainVehicleLocation = vehicleData.location();
        LocationEntity vehiclelocationEntity = new LocationEntity(
                0L, domainVehicleLocation.type(),
                domainVehicleLocation.coordinates().latitude(),
                domainVehicleLocation.coordinates().longitude(),
                domainVehicleLocation.description());
        //     locationEntity = Optional.of(locationRepository.save(vehiclelocationEntity));
        //     logger.info("Created vehicle location {}.", vehiclelocationEntity);
        // }
        // VehicleEntity vehicleEntity = new VehicleEntity(0L, vehicleData.name(), vehicleData.capacity());
        // vehicleEntity.setLocation(vehiclelocationEntity);

        VehicleEntity vehicleEntity =
                repository.save(new VehicleEntity(0L, vehicleData.name(), vehicleData.capacity(), vehiclelocationEntity,
                        vehicleData.depotId()));
        Vehicle vehicle = toDomain(vehicleEntity);
        logger.info("Created vehicle {}.", vehicle);
        return vehicle;
    }

    @Override
    public List<Vehicle> vehicles() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(VehicleRepositoryImpl::toDomain)
                .collect(toList());
    }

    @Override
    public Vehicle removeVehicle(long id) {
        Optional<VehicleEntity> optionalVehicleEntity = repository.findById(id);
        VehicleEntity vehicleEntity = optionalVehicleEntity.orElseThrow(
                () -> new IllegalArgumentException("Vehicle{id=" + id + "} doesn't exist"));
        repository.deleteById(id);
        Vehicle vehicle = toDomain(vehicleEntity);
        logger.info("Deleted vehicle {}.", vehicle);
        return vehicle;
    }

    @Override
    public void removeAll() {
        repository.deleteAll();
    }

    @Override
    public Optional<Vehicle> find(long vehicleId) {
        return repository.findById(vehicleId).map(VehicleRepositoryImpl::toDomain);
    }

    @Override
    public void update(Vehicle vehicle) {
        //FIX ME: not sure if this is allowed by design?!
        // Optional<LocationEntity> locationEntity = locationRepository.findById(vehicle.location().id());
        Location domainVehicleLocation = vehicle.location();
        LocationEntity vehiclelocationEntity = new LocationEntity(
                domainVehicleLocation.id(), domainVehicleLocation.type(),
                domainVehicleLocation.coordinates().latitude(), domainVehicleLocation.coordinates().longitude(),
                domainVehicleLocation.description());

        repository.save(
                new VehicleEntity(vehicle.id(), vehicle.name(), vehicle.capacity(), vehiclelocationEntity, vehicle.depotId()));
    }

    private static Vehicle toDomain(VehicleEntity vehicleEntity) {
        Location location = new Location(
                vehicleEntity.getLocation().getId(),
                vehicleEntity.getLocation().getype(),
                new Coordinates(vehicleEntity.getLocation().getLatitude(), vehicleEntity.getLocation().getLongitude()),
                vehicleEntity.getLocation().getDescription());

        return VehicleFactory.createVehicle(
                vehicleEntity.getId(),
                vehicleEntity.getName(),
                vehicleEntity.getCapacity(),
                location,
                vehicleEntity.getDepot());
    }
}
