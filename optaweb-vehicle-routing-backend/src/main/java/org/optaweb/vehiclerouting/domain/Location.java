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

package org.optaweb.vehiclerouting.domain;

import java.util.Objects;

/**
 * A unique location significant to the user.
 */
public class Location extends LocationData {

    private final long id;
    private final LocationType type;

    public Location(long id, LocationType type, Coordinates coordinates) {
        // TODO remove this?
        this(id, type, coordinates, "");
    }

    public Location(long id, LocationType type, Coordinates coordinates, String description) {
        super(type, coordinates, description);
        this.id = id;
        this.type = type;
    }

    /**
     * Location's ID.
     *
     * @return unique ID
     */
    public long id() {
        return id;
    }

    /**
     * Full description of the location including its ID, description and coordinates.
     *
     * @return full description
     */
    public String fullDescription() {
        return "[" + id + "]: " + super.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Location other = (Location) obj;
        if (id != other.id)
            return false;
        if (type != other.type)
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "Location [id=" + id +
                (description().isEmpty() ? "" : ", description=" + description()) +
                "]";
    }

}
