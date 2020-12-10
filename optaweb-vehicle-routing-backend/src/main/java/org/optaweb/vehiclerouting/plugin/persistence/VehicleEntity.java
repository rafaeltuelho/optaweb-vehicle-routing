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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Persistable vehicle.
 */
@Entity
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private int capacity;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "LOCATION_ID", referencedColumnName = "id")
    private LocationEntity location;
    //FIX ME: Depot must be an Entity!!!
    @Column(name = "DEPOT_ID")
    private long depot;

    protected VehicleEntity() {
        // for JPA
    }

    //Vehicle must have a Location from now on
    public VehicleEntity(long id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public VehicleEntity(long id, String name, int capacity, LocationEntity location, long depot) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.depot = depot;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public long getDepot(){
        return depot;
    }

    @Override
    public String toString() {
        return "VehicleEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", location=" + location +
                ", depotId=" + depot +
                '}';
    }

}
