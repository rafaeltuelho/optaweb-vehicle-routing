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

import {
  Form,
  FormSelect,
  FormSelectOption,
  GutterSize,
  Split,
  SplitItem,
  Text,
  TextContent,
  TextVariants,
} from '@patternfly/react-core';
import * as React from 'react';
import { connect } from 'react-redux';
import { clientOperations } from 'store/client';
import { UserViewport } from 'store/client/types';
import { routeOperations } from 'store/route';
import { Depot, LatLng, Location, RouteWithTrack, Vehicle } from 'store/route/types';
import { AppState } from 'store/types';
import LocationList from 'ui/components/LocationList';
import RouteMap from 'ui/components/RouteMap';
import { sideBarStyle } from 'ui/pages/common';

export interface StateProps {
  vehicleCount: number;
  depots: Depot[];
  visits: Location[];
  routes: RouteWithTrack[];
  boundingBox: [LatLng, LatLng] | null;
  userViewport: UserViewport;
}

export interface DispatchProps {
  addVisitHandler: typeof routeOperations.addLocation;
  removeVisitHandler: typeof routeOperations.deleteLocation;
  removeDepotHandler: typeof routeOperations.deleteDepot;
  addVehicleHandler: typeof routeOperations.addVehicle,
  addDepotVehicleHandler: typeof routeOperations.addDepotVehicle,
  removeDepotVehicleHandler: typeof routeOperations.deleteDepotVehicle,
  removeVehicleHandler: typeof routeOperations.deleteVehicle,
  updateViewport: typeof clientOperations.updateViewport;
}

const mapStateToProps = ({ plan, serverInfo, userViewport }: AppState): StateProps => ({
  vehicleCount: plan.vehicles.length,
  depots: plan.depots,
  visits: plan.visits,
  routes: plan.routes,
  boundingBox: serverInfo.boundingBox,
  userViewport,
});

const mapDispatchToProps: DispatchProps = {
  addVisitHandler: routeOperations.addLocation,
  removeVisitHandler: routeOperations.deleteLocation,
  removeDepotHandler: routeOperations.deleteDepot,
  addVehicleHandler: routeOperations.addVehicle,
  removeVehicleHandler: routeOperations.deleteVehicle,
  addDepotVehicleHandler: routeOperations.addDepotVehicle,
  removeDepotVehicleHandler: routeOperations.deleteDepotVehicle,
  updateViewport: clientOperations.updateViewport,
};

export type RouteProps = DispatchProps & StateProps;

export interface RouteState {
  selectedId: number;
  selectedRouteId: number;
}

export class Route extends React.Component<RouteProps, RouteState> {
  constructor(props: RouteProps) {
    super(props);

    this.state = {
      selectedId: NaN,
      selectedRouteId: 0,
    };
    this.onSelectLocation = this.onSelectLocation.bind(this);
    this.handleMapClick = this.handleMapClick.bind(this);
  }

  handleMapClick(e: any) {
    this.props.addVisitHandler({ ...e.latlng, description: '' });
  }

  onSelectLocation(id: number) {
    this.setState({ selectedId: id });
  }

  render() {
    const { selectedId, selectedRouteId } = this.state;
    const {
      vehicleCount,
      boundingBox,
      userViewport,
      depots,
      visits,
      routes,
      removeVisitHandler,
      removeDepotHandler,
      addVehicleHandler,
      removeVehicleHandler,
      addDepotVehicleHandler,
      removeDepotVehicleHandler,
      updateViewport,
    } = this.props;

    // FIXME quick hack to preserve route color by keeping its index
    const filteredRoutes = (
      routes.map((value, index) => (index === selectedRouteId ? value : { visits: [], track: [] }))
    );
    const filteredVisits: Location[] = routes.length > 0 ? routes[selectedRouteId].visits : [];
    return (
      <>
        <TextContent>
          <Text component={TextVariants.h1}>Route</Text>
        </TextContent>
        <Split gutter={GutterSize.md}>
          <SplitItem
            isFilled={false}
            style={sideBarStyle}
          >
            <Form>
              <FormSelect
                style={{ backgroundColor: 'white', marginBottom: 10 }}
                isDisabled={routes.length === 0}
                value={selectedRouteId}
                onChange={(e) => {
                  this.setState({ selectedRouteId: parseInt(e as unknown as string, 10) });
                }}
                aria-label="FormSelect Input"
              >
                {routes.map(
                  (route, index) => (
                    <FormSelectOption
                      isDisabled={false}
                      // eslint-disable-next-line react/no-array-index-key
                      key={index}
                      value={index}
                      label={route.vehicle.name}
                    />
                  ),
                )}
              </FormSelect>
            </Form>
            <LocationList
              depots={depots}
              visits={filteredVisits}
              removeHandler={removeVisitHandler}
              selectHandler={this.onSelectLocation}
            />
          </SplitItem>
          <SplitItem isFilled>
            <RouteMap
              boundingBox={boundingBox}
              userViewport={userViewport}
              updateViewport={updateViewport}
              selectedId={selectedId}
              clickHandler={this.handleMapClick}
              removeVisitHandler={removeVisitHandler}
              removeDepotHandler={removeDepotHandler}
              addVehicleHandler={addVehicleHandler}
              removeVehicleHandler={removeVehicleHandler}
              addDepotVehicleHandler={addDepotVehicleHandler}
              removeDepotVehicleHandler={removeDepotVehicleHandler}
              // vehicleCount={vehicleCount}
              depots={depots}
              visits={visits}
              routes={filteredRoutes}
            />
          </SplitItem>
        </Split>
      </>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(Route);
