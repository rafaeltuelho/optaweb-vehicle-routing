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

package org.optaweb.vehiclerouting.domain;

/**
 * Creates {@link Vehicle} instances.
 */
public class VehicleFactory {

    private VehicleFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create vehicle data.
     *
     * @param name vehicle's name
     * @param capacity vehicle's capacity
     * @param depot vehicle's depotId
     * @return vehicle data
     */
    // public static VehicleData vehicleData(String name, int capacity, Location location) {
    //     return new VehicleData(name, capacity, location, 0);
    // }

    /**
     * Create vehicle data.
     *
     * @param name vehicle's name
     * @param capacity vehicle's capacity
     * @param depot vehicle's depotId
     * @return vehicle data
     */
    public static VehicleData vehicleData(String name, int capacity, Location location, long depotId) {
        return new VehicleData(name, capacity, location, depotId);
    }

    /**
     * Create a new vehicle with the given ID, name capacity and location.
     *
     * @param id vehicle's ID
     * @param name vehicle's name
     * @param capacity vehicle's capacity
     * @param location vehicle's location
     * @return new vehicle
     */
    // public static Vehicle createVehicle(long id, String name, int capacity, Location location) {
    //     return new Vehicle(id, name, capacity, location, 0);
    // }

    /**
     * Create a new vehicle with the given ID, name capacity, location and depotId.
     *
     * @param id vehicle's ID
     * @param name vehicle's name
     * @param capacity vehicle's capacity
     * @param location vehicle's location
     * @param depot vehicle's depotId
     * @return new vehicle
     */
    public static Vehicle createVehicle(long id, String name, int capacity, Location location, long depotId) {
        return new Vehicle(id, name, capacity, location, depotId);
    }

    /**
     * Create a vehicle with given ID and capacity of zero. The vehicle will have a non-empty name.
     *
     * @param id vehicle's ID
     * @return new testing vehicle instance
     */
    public static Vehicle testVehicle(long id) {
        return new Vehicle(id, "Vehicle " + id, 0, new Location(0L, LocationType.VEHICLE, Coordinates.valueOf(.1, .1)), 1);
    }

    /**
     * Create a vehicle with given ID, capacity of zero and a given initial Location. The vehicle will have a non-empty name.
     *
     * @param id vehicle's ID
     * @param id vehicle's initial Location
     * @return new testing vehicle instance
     */
    public static Vehicle testVehicle(long id, Location location) {
        return new Vehicle(id, "Vehicle " + id, 0, location, 1);
    }

}
