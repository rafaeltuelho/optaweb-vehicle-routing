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

import { ThunkCommandFactory } from '../types';
import * as actions from './actions';
import {
  AddLocationAction,
  AddDepotAction,
  AddDepotVehicleAction,
  AddVehicleAction,
  ClearRouteAction,
  DeleteLocationAction,
  DeleteVehicleAction,
  DeleteDepotVehicleAction,
  Depot,
  LatLngWithTypeDescription,
  Vehicle,
  VehicleCapacity,
  DeleteDepotAction,
} from './types';

export const { updateRoute } = actions;

export const addLocation: ThunkCommandFactory<LatLngWithTypeDescription, AddLocationAction> = (
  (location) => (dispatch, getState, client) => {
    client.addLocation(location);
    dispatch(actions.addLocation(location));
  });

export const deleteLocation: ThunkCommandFactory<number, DeleteLocationAction> = (
  (locationId) => (dispatch, getState, client) => {
    client.deleteLocation(locationId);
    dispatch(actions.deleteLocation(locationId));
  });

export const addDepot: ThunkCommandFactory<LatLngWithTypeDescription, AddDepotAction> = (
  (depot) => (dispatch, getState, client) => {
    client.addDepot(depot);
    dispatch(actions.addDepot(depot));
  });
  
export const deleteDepot: ThunkCommandFactory<number, DeleteDepotAction> = (
  (id) => (dispatch, getState, client) => {
    client.deleteDepot(id);
    dispatch(actions.deleteDepot(id));
  });

export const addDepotVehicle: ThunkCommandFactory<Vehicle, AddDepotVehicleAction> = (
  (vehicle) => (dispatch, getState, client) => {
    dispatch(actions.addDepotVehicle(vehicle));
  });

export const deleteDepotVehicle: ThunkCommandFactory<number, DeleteDepotVehicleAction> = (
  (vehicleId) => (dispatch, getState, client) => {
    dispatch(actions.deleteDepotVehicle(vehicleId));
  });

export const addVehicle: ThunkCommandFactory<Vehicle, AddVehicleAction> = (
  (vehicle) => (dispatch, getState, client) => {
    client.addVehicle(vehicle);
    dispatch(actions.addVehicle(vehicle));
  });
  
export const deleteVehicle: ThunkCommandFactory<number, DeleteVehicleAction> = (
  (vehicleId) => (dispatch, getState, client) => {
    client.deleteVehicle(vehicleId);
    dispatch(actions.deleteVehicle(vehicleId));
  });

export const deleteAnyVehicle: ThunkCommandFactory<void, never> = (
  () => (dispatch, getState, client) => {
    client.deleteAnyVehicle();
  });

export const changeVehicleCapacity: ThunkCommandFactory<VehicleCapacity, never> = (
  ({ vehicleId, capacity }: VehicleCapacity) => (dispatch, getState, client) => {
    client.changeVehicleCapacity(vehicleId, capacity);
  });

export const clearRoute: ThunkCommandFactory<void, ClearRouteAction> = (
  () => (dispatch, getState, client) => {
    client.clear();
    dispatch(actions.clearRoute());
  });
