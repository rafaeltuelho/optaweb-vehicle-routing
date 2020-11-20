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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class VehicleTest {

    @Test
    void constructor_params_must_not_be_null() {
        assertThatNullPointerException().isThrownBy(() -> new Vehicle(0, null, 0, null));
    }

    @Test
    void vehicles_are_identified_based_on_id() {
        final long id = 0;
        final String description = "test description";
        final int capacity = 1;
        final Location vehicleLocation = LocationFactory.testLocation(1, LocationType.VEHICLE);
        final Vehicle vehicle = new Vehicle(id, description, capacity, vehicleLocation);

        assertThat(vehicle)
                // different ID
                .isNotEqualTo(new Vehicle(id + 1, description, capacity, vehicleLocation))
                // different Location
                .isNotEqualTo(new Vehicle(id + 1, description, capacity, LocationFactory.testLocation(2, LocationType.VEHICLE)))
                // null
                .isNotEqualTo(null)
                // different class
                .isNotEqualTo(id)
                // same object -> OK
                .isEqualTo(vehicle)
                // same properties -> OK
                .isEqualTo(new Vehicle(id, description, capacity, vehicleLocation))
                // same ID, different description -> OK
                .isEqualTo(new Vehicle(id, description + "x", capacity, vehicleLocation))
                // same ID, different capacity -> OK
                .isEqualTo(new Vehicle(id, description, capacity + 1, vehicleLocation));
    }

    @Test
    void equal_vehicles_must_have_same_hashcode() {
        long id = 1;
        assertThat(new Vehicle(id, "description 1", 1, LocationFactory.testLocation(1, LocationType.VEHICLE)))
                .hasSameHashCodeAs(new Vehicle(id, "description 2", 2, LocationFactory.testLocation(1, LocationType.VEHICLE)));
    }
}
