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

import * as L from 'leaflet';
import * as React from 'react';
import { Map, Polyline, Rectangle, TileLayer, ZoomControl } from 'react-leaflet';
import { UserViewport } from 'store/client/types';
import { Depot, LatLng, Location, LocationType, RouteWithTrack, Vehicle } from 'store/route/types';
import LocationMarker from './LocationMarker';
import DepotMarker from './DepotMarker';

type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;

export interface Props {
  selectedId: number;
  // vehicleCount: number;
  clickHandler: (e: React.SyntheticEvent<HTMLElement>) => void;
  removeVisitHandler: (id: number) => void;
  removeDepotHandler: (id: number) => void;
  addVehicleHandler: (vehicle: Vehicle) => void;
  removeVehicleHandler: (id: number) => void;
  addDepotVehicleHandler: (vehicle: Vehicle) => void;
  removeDepotVehicleHandler: (id: number) => void;
  depots: Depot[];
  visits: Location[];
  routes: Omit<RouteWithTrack, 'vehicle'>[];
  boundingBox: [LatLng, LatLng] | null;
  userViewport: UserViewport;
  updateViewport: (viewport: UserViewport) => void;
}

// TODO unlimited unique (random) colors
const colors = ['deepskyblue', 'crimson', 'seagreen', 'slateblue', 'gold', 'darkorange'];

function color(index: number) {
  return colors[index % colors.length];
}

const RouteMap: React.FC<Props> = ({
  boundingBox,
  userViewport,
  selectedId,
  // vehicleCount,
  depots,
  visits,
  routes,
  clickHandler,
  removeVisitHandler,
  removeDepotHandler,
  addVehicleHandler,
  removeVehicleHandler,  
  addDepotVehicleHandler,
  removeDepotVehicleHandler,  
  updateViewport,
}) => {
  const bounds = boundingBox ? new L.LatLngBounds(boundingBox[0], boundingBox[1]) : undefined;
  // do not use bounds if user's viewport is dirty
  const mapBounds = userViewport.isDirty ? undefined : bounds;
  // TODO make TileLayer URL configurable
  // eslint-disable-next-line @typescript-eslint/ban-ts-ignore
  // @ts-ignore
  const tileLayerUrl = window.Cypress ? 'test-mode-empty-url' : 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
  return (
    <Map
      bounds={mapBounds}
      viewport={userViewport}
      onViewportChanged={updateViewport}
      onClick={clickHandler}
      // FIXME use height: 100%
      style={{ width: '100%', height: 'calc(100vh - 176px)' }}
      zoomControl={false} // hide the default zoom control which is on top left
    >
      <TileLayer
        attribution="&copy; <a href='http://osm.org/copyright'>OpenStreetMap</a> contributors"
        url={tileLayerUrl}
      />
      <ZoomControl position="topright" />
      {depots.map((depot) => (
          <DepotMarker
            key={depot.id}
            depot={depot}
            isSelected={depot.id === selectedId}
            vehicleCount={0}
            removeHandler={removeDepotHandler}
            addVehicleHandler={addVehicleHandler}
            removeVehicleHandler={removeVehicleHandler}
            addDepotVehicleHandler={addDepotVehicleHandler}
            removeDepotVehicleHandler={removeDepotVehicleHandler}
          />

      ))}
      {visits.map((location) => (
        <LocationMarker
          key={location.id}
          location={location}
          isDepot={false}
          isSelected={location.id === selectedId}
          removeHandler={removeVisitHandler}
      />
      ))}
      {routes.map((route, index) => (
        <Polyline
          // eslint-disable-next-line react/no-array-index-key
          key={index} // FIXME use unique id (not iteration index)
          positions={route.track}
          fill={false}
          color={color(index)}
          smoothFactor={3}
          weight={9}
          opacity={0.6666}
        />
      ))}
      {bounds && (
        <Rectangle
          bounds={bounds}
          color="seagreen"
          fill={false}
          dashArray="10,5"
          weight={1}
        />
      )}
    </Map>
  );
};

export default RouteMap;
