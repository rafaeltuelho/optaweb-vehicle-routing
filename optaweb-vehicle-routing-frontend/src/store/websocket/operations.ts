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

import { demoOperations } from '../demo';
import { FinishLoadingAction } from '../demo/types';
import { messageActions } from '../message';
import { MessageAction } from '../message/types';
import { routeOperations } from '../route';
import { UpdateRouteAction, Vehicle } from '../route/types';
import { serverOperations } from '../server';
import { ServerInfoAction } from '../server/types';
import { ThunkCommandFactory } from '../types';
import * as actions from './actions';
import { WebSocketAction } from './types';
import { produce } from 'immer';

type ConnectClientThunkAction =
  | WebSocketAction
  | MessageAction
  | UpdateRouteAction
  | FinishLoadingAction
  | ServerInfoAction;

/**
 * Connect the client to WebSocket.
 */
export const connectClient: ThunkCommandFactory<void, ConnectClientThunkAction> = (
  () => (dispatch, getState, client) => {
    // dispatch WS connection initializing
    dispatch(actions.initWsConnection());
    client.connect(
      // on connection, subscribe to the route topic
      () => {
        dispatch(actions.wsConnectionSuccess());
        client.subscribeToServerInfo((serverInfo) => {
          dispatch(serverOperations.serverInfo(serverInfo));
        });
        client.subscribeToErrorTopic((errorMessage) => {
          dispatch(messageActions.receiveMessage(errorMessage));
        });
        client.subscribeToRoute((plan) => {
          // agregate vehicles per depot and update Depot's vehicle list.
          // this is done here in the front-end becuse we don't have Depot<->Vehicle association on the back-end by now
          let clonnedPlan = produce(plan, draftPlan => {
            let originalDepots = draftPlan.depots;
            let updatedDepots = originalDepots.map((depot) => {
              let depotVehicles: number[] = [];
              draftPlan.vehicles.filter(v => v.depotId == depot.id).forEach(v => {
                depotVehicles.push(v.id);
              });
              
              console.debug('Depot [' + depot.id + '] vehicles ', depotVehicles);
              return {...depot, vehicles: depotVehicles};
            });

            draftPlan.depots = updatedDepots;
          });
          // clonnedPlan.depots.map()      
          
          dispatch(routeOperations.updateRoute(clonnedPlan));
          if (getState().demo.isLoading) {
            // TODO handle the case when serverInfo doesn't contain demo with the given name
            //      (that could only be possible due to a bug in the code)
            const demo = getState().serverInfo.demos.filter((value) => value.name === getState().demo.demoName)[0];
            if (plan.visits.length === demo.visits) {
              dispatch(demoOperations.finishLoading());
            }
          }
        });
      },
      // on error, schedule a reconnection attempt
      (err) => {
        // TODO try to pass the original err object or test it here and
        //      dispatch different actions based on its properties (Frame vs. CloseEvent, reason etc.)
        dispatch(actions.wsConnectionFailure(JSON.stringify(err)));
        setTimeout(() => dispatch(connectClient()), 1000);
      },
    );
  });
