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

import { produce } from 'immer';
import { ActionType, RouteAction, RoutingPlan } from './types';

export const initialRouteState: RoutingPlan = {
  distance: 'no data',
  vehicles: [],
  depots: [],
  visits: [],
  routes: [],
};

export const routeReducer = (state = initialRouteState, action: RouteAction): RoutingPlan => {
  switch (action.type) {
    case ActionType.UPDATE_ROUTING_PLAN: {
      return action.plan;
    }
    case ActionType.ADD_VEHICLE: {
      console.debug('Vehicle added!');
      console.debug(action);
      console.debug('Vehicle Id (action value) ', action.value);
      // state.depots[0].vehicles;
      return state;
    }
    case ActionType.DELETE_VEHICLE: {
      console.debug('Vehicle deleted!');
      console.debug(action);
      console.debug('Vehicle Id (action value) ', action.value);
      return state;
    }
    case ActionType.ADD_DEPOT_VEHICLE: {
      let vehicle = action.value;
      console.debug('Depot\'s vehicle list updated');
      console.debug(action);
      console.debug('Updating Depot\'s vehicle list ');
      console.debug('Vehicle Id (action value) ', vehicle);

      // let newState = produce(state, draftState => {
      //   draftState.depots.find(d => d.id == vehicle.depotId)?.vehicles.push(vehicle.id);
      // })
      
      return state;
    }
    case ActionType.DELETE_DEPOT_VEHICLE: {
      console.debug('Depot\'s vehicle list updated');
      console.debug(action);
      console.debug('Updating Depot\'s vehicle list ');
      console.debug('Vehicle Id (action value) ', action.value);
      return state;
    }
    default: {
      console.debug('routeReducer() default reducer ');
      console.debug('Action Type: ', action.type);
      return state;
    }
  }
};
