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

package org.optaweb.vehiclerouting.plugin.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.LocationFactory;
import org.optaweb.vehiclerouting.domain.LocationType;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.domain.VehicleData;
import org.optaweb.vehiclerouting.domain.VehicleFactory;

@ExtendWith(MockitoExtension.class)
class VehicleRepositoryImplTest {

    @Mock
    private VehicleCrudRepository vehicleCrudRepository;
    @Mock
    private LocationCrudRepository locationCrudRepository;
    @InjectMocks
    private LocationRepositoryImpl locationRepository;
    @InjectMocks
    private VehicleRepositoryImpl vehicleRepository;
    @Captor
    private ArgumentCaptor<VehicleEntity> vehicleEntityCaptor;
    @Captor
    private ArgumentCaptor<LocationEntity> locationEntityCaptor;

    private final Location testVehicleLocation = LocationFactory.testLocation(0, LocationType.VEHICLE);
    private final Vehicle testVehicle = VehicleFactory.createVehicle(19, "vehicle name", 1100, testVehicleLocation);

    private static VehicleEntity vehicleEntity(Vehicle vehicle) {
        LocationEntity locationEntity = new LocationEntity(
                vehicle.location().id(), vehicle.location().type(),
                vehicle.location().coordinates().latitude(), vehicle.location().coordinates().longitude(),
                vehicle.location().description());
        return new VehicleEntity(vehicle.id(), vehicle.name(), vehicle.capacity(), locationEntity);
    }

    private static LocationEntity locationEntity(Location location) {
        return new LocationEntity(
                location.id(), location.type(),
                location.coordinates().latitude(), location.coordinates().longitude(),
                location.description());
    }

    @Test
    void should_create_vehicle_and_generate_id_name_and_location() {
        // arrange
        // LocationEntity vehicleLocationEntity = locationEntity(testVehicleLocation);
        // when(locationCrudRepository.save(locationEntityCaptor.capture())).thenReturn(vehicleLocationEntity);
        VehicleEntity newEntity = vehicleEntity(testVehicle);
        when(vehicleCrudRepository.save(vehicleEntityCaptor.capture())).thenReturn(newEntity);

        // act
        VehicleData savedVehicleData =
                VehicleFactory.vehicleData("Vehicle 1", 1, testVehicleLocation);
        Vehicle newVehicle = vehicleRepository.createVehicleWithLocation(savedVehicleData);

        // assert
        // -- the correct values were used to save the entity
        // List<VehicleEntity> savedVehicles = vehicleEntityCaptor.getAllValues();
        VehicleEntity savedVehicle = vehicleEntityCaptor.getValue();
        assertThat(savedVehicle).isNotNull();

        // assertThat(savedVehicles.get(0).getName()).isNull();
        // assertThat(savedVehicles.get(0).getCapacity()).isEqualTo(savedVehicleData.capacity());
        // assertThat(savedVehicles.get(0).getLocation()).isEqualTo(vehicleLocationEntity);
        // assertThat(savedVehicle.getName()).isEqualTo("Vehicle " + newEntity.getId());
        // assertThat(savedVehicle.getCapacity()).isEqualTo(savedVehicleData.capacity());
        // assertThat(savedVehicle.getLocation()).isEqualTo(vehicleLocationEntity);

        // -- created domain vehicle is equal to the entity returned by repository.save()
        // This may be confusing but that's the contract of Spring Repository API.
        // The entity instance that is being saved is meant to be discarded. The returned instance should be used
        // for further operations as the save() operation may update it (for example generate the ID).
        assertThat(newVehicle.id()).isEqualTo(newEntity.getId());
        assertThat(newVehicle.name()).isEqualTo(newEntity.getName());
        assertThat(newVehicle.capacity()).isEqualTo(newEntity.getCapacity());
        assertThat(newVehicle.location().id()).isEqualTo(testVehicleLocation.id());
        assertThat(newVehicle.location().description()).isEqualTo(testVehicleLocation.description());
        assertThat(newVehicle.location().coordinates().latitude()).isEqualTo(testVehicleLocation.coordinates().latitude());
        assertThat(newVehicle.location().coordinates().longitude()).isEqualTo(testVehicleLocation.coordinates().longitude());

    }

    @Test
    void create_vehicle_from_given_data() {
        // arrange
        // LocationEntity vehicleLocationEntity = locationEntity(testVehicleLocation);
        // when(locationCrudRepository.save(locationEntityCaptor.capture())).thenReturn(vehicleLocationEntity);
        VehicleEntity newEntity = vehicleEntity(testVehicle);
        when(vehicleCrudRepository.save(vehicleEntityCaptor.capture())).thenReturn(newEntity);

        VehicleData vehicleData = VehicleFactory.vehicleData("x", 1, testVehicleLocation);

        // act
        Vehicle newVehicle = vehicleRepository.createVehicleWithLocation(vehicleData);

        // assert
        // -- the correct values were used to save the entity
        VehicleEntity savedVehicle = vehicleEntityCaptor.getValue();


        assertThat(savedVehicle.getName()).isEqualTo(vehicleData.name());
        assertThat(savedVehicle.getCapacity()).isEqualTo(vehicleData.capacity());
        // assertThat(savedVehicle.getLocation()).isEqualTo(vehicleLocationEntity);
        assertThat(savedVehicle.getLocation().getId()).isEqualTo(testVehicleLocation.id());
        assertThat(savedVehicle.getLocation().getDescription()).isEqualTo(testVehicleLocation.description());
        assertThat(savedVehicle.getLocation().getLatitude()).isEqualTo(testVehicleLocation.coordinates().latitude());
        assertThat(savedVehicle.getLocation().getLongitude()).isEqualTo(testVehicleLocation.coordinates().longitude());

        // -- created domain vehicle is equal to the entity returned by repository.save()
        assertThat(newVehicle.id()).isEqualTo(newEntity.getId());
        assertThat(newVehicle.name()).isEqualTo(newEntity.getName());
        assertThat(newVehicle.capacity()).isEqualTo(newEntity.getCapacity());
        assertThat(newVehicle.location().id()).isEqualTo(testVehicleLocation.id());
        assertThat(newVehicle.location().description()).isEqualTo(testVehicleLocation.description());
        assertThat(newVehicle.location().coordinates().latitude()).isEqualTo(testVehicleLocation.coordinates().latitude());
        assertThat(newVehicle.location().coordinates().longitude()).isEqualTo(testVehicleLocation.coordinates().longitude());
    }

    @Test
    void remove_created_vehicle_by_id() {
        VehicleEntity vehicleEntity = vehicleEntity(testVehicle);
        final long id = testVehicle.id();
        when(vehicleCrudRepository.findById(id)).thenReturn(Optional.of(vehicleEntity));

        //FIX ME: must also remove the associated Location

        Vehicle removed = vehicleRepository.removeVehicle(id);
        assertThat(removed).isEqualTo(testVehicle);
        verify(vehicleCrudRepository).deleteById(id);
    }

    @Test
    void removing_nonexistent_vehicle_should_fail() {
        when(vehicleCrudRepository.findById(anyLong())).thenReturn(Optional.empty());

        // removing nonexistent vehicle should fail and its ID should appear in the exception message
        int uniqueNonexistentId = 7173;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> vehicleRepository.removeVehicle(uniqueNonexistentId))
                .withMessageContaining(String.valueOf(uniqueNonexistentId));
    }

    @Test
    void remove_all_vehicles() {
        vehicleRepository.removeAll();
        verify(vehicleCrudRepository).deleteAll();
    }

    @Test
    void get_all_vehicles() {
        VehicleEntity vehicleEntity = vehicleEntity(testVehicle);
        when(vehicleCrudRepository.findAll()).thenReturn(Collections.singletonList(vehicleEntity));
        assertThat(vehicleRepository.vehicles()).containsExactly(testVehicle);
    }

    @Test
    void find_by_id() {
        VehicleEntity vehicleEntity = vehicleEntity(testVehicle);
        when(vehicleCrudRepository.findById(testVehicle.id())).thenReturn(Optional.of(vehicleEntity));
        assertThat(vehicleRepository.find(testVehicle.id())).contains(testVehicle);
    }

    @Test
    void update() {
        vehicleRepository.update(testVehicle);

        verify(vehicleCrudRepository).save(vehicleEntityCaptor.capture());

        VehicleEntity savedVehicle = vehicleEntityCaptor.getValue();
        assertThat(savedVehicle.getId()).isEqualTo(testVehicle.id());
        assertThat(savedVehicle.getName()).isEqualTo(testVehicle.name());
        assertThat(savedVehicle.getCapacity()).isEqualTo(testVehicle.capacity());
        assertThat(savedVehicle.getLocation().getId()).isEqualTo(testVehicleLocation.id());
        assertThat(savedVehicle.getLocation().getDescription()).isEqualTo(testVehicleLocation.description());
        assertThat(savedVehicle.getLocation().getLatitude()).isEqualTo(testVehicleLocation.coordinates().latitude());
        assertThat(savedVehicle.getLocation().getLongitude()).isEqualTo(testVehicleLocation.coordinates().longitude());
    }
}
