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
 * Creates {@link Location} instances.
 */
public class LocationFactory {

    private LocationFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create location data.
     *
     * @param type location's type
     * @param coordinates location's coordinates
     * @param description location's description
     * @return location data
     */
    public static LocationData locationData(LocationType type, Coordinates coordinates, String description) {
        return new LocationData(type, coordinates, description);
    }

    /**
     * Create location data.
     *
     * @param type location's types
     * @param latitude location's coordinates
     * @param longitude location's coordinates
     * @param description location's description
     * @return location data
     */
    public static LocationData locationData(LocationType type, double latitude, double longitude, String description) {
        return new LocationData(type, Coordinates.valueOf(latitude, longitude), description);
    }

    /**
     * Create a new location with the given ID, name and capacity.
     *
     * @param id location's ID
     * @param type location's type
     * @param coordinates location's coordinates
     * @param description location's description
     * @return new location
     */
    public static Location createLocation(long id, LocationType type, Coordinates coordinates, String description) {
        return new Location(id, type, coordinates, description);
    }

    /**
     * Create a location with given ID, type VISIT and fake coordinates. 
     * The location will have a non-empty description.
     *
     * @param id location's ID
     * @return new testing location instance
     */
    public static Location testLocation(long id, LocationType type) {
        return new Location(0L, type, Coordinates.valueOf(.1, .1), "Location " + id);
    }
}
