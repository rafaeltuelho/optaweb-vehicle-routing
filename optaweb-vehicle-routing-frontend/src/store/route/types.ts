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

import { Action } from 'redux';

export interface LatLng {
  readonly lat: number;
  readonly lng: number;
}

export enum LocationType {
  Visit = 'VISIT',
  Depot = 'DEPOT',
  Vehicle = 'VEHICLE',
  Other = 'OTHER',
}

// TODO: remove this type and work with the Location Type
export interface LatLngWithTypeDescription extends LatLng {
  type: LocationType;
  description: string;
}

export interface Location extends LatLng {
  readonly id: number;
  readonly type: string;
  // TODO decide between optional, nullable and more complex structure (displayName, fullDescription, address, ...)
  readonly description?: string;
}

export interface Depot extends Location {
  vehicles: number[];
}

export interface Vehicle {
  readonly id: number;
  readonly name: string;
  readonly capacity: number;
  readonly location: Location;
  depotId?: number;
}

export interface Route {
  readonly vehicle: Vehicle; // TODO change to vehicleId
  readonly visits: Location[];
}

export type LatLngTuple = [number, number];

export interface RouteWithTrack extends Route {
  readonly track: LatLngTuple[];
}

export interface RoutingPlan {
  readonly distance: string;
  readonly vehicles: Vehicle[];
  depots: Depot[];
  readonly visits: Location[];
  readonly routes: RouteWithTrack[];
}

export enum ActionType {
  UPDATE_ROUTING_PLAN = 'UPDATE_ROUTING_PLAN',
  DELETE_LOCATION = 'DELETE_LOCATION',
  ADD_LOCATION = 'ADD_LOCATION',
  ADD_DEPOT = 'ADD_DEPOT',
  DELETE_DEPOT = 'DELETE_DEPOT',
  ADD_VEHICLE = 'ADD_VEHICLE',
  ADD_DEPOT_VEHICLE = 'ADD_DEPOT_VEHICLES',
  DELETE_DEPOT_VEHICLE = 'DELETE_DEPOT_VEHICLES',
  DELETE_VEHICLE = 'DELETE_VEHICLE',
  CLEAR_SOLUTION = 'CLEAR_SOLUTION',
}

export interface AddLocationAction extends Action<ActionType.ADD_LOCATION> {
  readonly value: LatLngWithTypeDescription;
}

export interface AddDepotAction extends Action<ActionType.ADD_DEPOT> {
  readonly value: LatLngWithTypeDescription;
}

export interface AddVehicleAction extends Action<ActionType.ADD_VEHICLE> {
  readonly value: Vehicle;
}

export interface AddDepotVehicleAction extends Action<ActionType.ADD_DEPOT_VEHICLE> {
  readonly value: Vehicle;
}

export interface DeleteDepotVehicleAction extends Action<ActionType.DELETE_DEPOT_VEHICLE> {
  readonly value: number;
}

export interface ClearRouteAction extends Action<ActionType.CLEAR_SOLUTION> {
}

export interface DeleteLocationAction extends Action<ActionType.DELETE_LOCATION> {
  readonly value: number;
}

export interface DeleteDepotAction extends Action<ActionType.DELETE_DEPOT> {
  readonly value: number;
}

export interface DeleteVehicleAction extends Action<ActionType.DELETE_VEHICLE> {
  readonly value: number;
}

export interface VehicleCapacity {
  vehicleId: number;
  capacity: number;
}

export interface UpdateRouteAction extends Action<ActionType.UPDATE_ROUTING_PLAN> {
  readonly plan: RoutingPlan;
}

export type RouteAction =
  | AddLocationAction
  | AddDepotAction
  | AddVehicleAction
  | DeleteLocationAction
  | DeleteDepotAction
  | DeleteVehicleAction
  | AddDepotVehicleAction
  | DeleteDepotVehicleAction
  | UpdateRouteAction
  | ClearRouteAction;
