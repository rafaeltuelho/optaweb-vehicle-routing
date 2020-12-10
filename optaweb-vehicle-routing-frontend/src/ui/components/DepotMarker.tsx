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

import * as L from 'leaflet';
import * as React from 'react';
import {  
  Button,
  ButtonVariant,
  Flex,
  FlexItem,
  FlexModifiers,
  GutterSize,
  InputGroup,
  InputGroupText,
  Split,
  SplitItem,
  Text,
  TextContent,
  TextVariants
} from  '@patternfly/react-core';
import ReactTooltip from "react-tooltip";
import { MinusIcon, PlusIcon, TrashIcon } from '@patternfly/react-icons';
import { VehiclesInfo, InfoBlock} from 'ui/pages/InfoBlock';
import { routeOperations } from 'store/route';
import { AppState } from 'store/types';
import { Marker, Tooltip as LToolTip, Popup } from 'react-leaflet';
import { Depot, Location, LocationType, Vehicle } from 'store/route/types';

const homeIcon = L.icon({
  iconAnchor: [12, 12],
  iconSize: [24, 24],
  iconUrl: 'if_big_house-home_2222740.png',
  popupAnchor: [0, -10],
  shadowAnchor: [16, 2],
  shadowSize: [50, 16],
  shadowUrl: 'if_big_house-home_2222740_shadow.png',
});

export interface Props {
  depot: Depot;
  isSelected: boolean;
  removeHandler: (id: number) => void;
  vehicleCount: Number;
  addVehicleHandler: (vehicle: Vehicle) => void;
  removeVehicleHandler:(id: number) => void;  
  addDepotVehicleHandler: (vehicle: Vehicle) => void;
  removeDepotVehicleHandler:(id: number) => void;  
}

const DepotMarker: React.FC<Props> = ({
  depot,
  isSelected,
  vehicleCount,
  removeHandler,
  addVehicleHandler,
  removeVehicleHandler,  
  addDepotVehicleHandler,
  removeDepotVehicleHandler  
}) => {
  const icon = homeIcon;
  let depotVehicleIds: number[] = [...depot.vehicles];

  function handleAddVehicleClick(depot: Depot) {
    const vehicleLocation: Location = {
      id: 0,
      lat: depot.lat,
      lng: depot.lng,
      type: LocationType.Vehicle,
      description: depot.description
    };
    const newVehicle: Vehicle = {
      id: 0,
      name: 'Vehicle at ' + (vehicleLocation.description ? vehicleLocation.description.split(',')[0] : 'undefined'),
      capacity: 10,
      location: vehicleLocation,
      depotId: depot.id
    };
    console.log('adding vehicle ', newVehicle);
    addVehicleHandler(newVehicle);
    // addDepotVehicleHandler(newVehicle);
  };

  function handleRemoveVehicleClick(id: number | undefined) {
    console.log('removing vehicle with id: ', id);
    if (id)
      removeVehicleHandler(id);
    //removeDepotVehicleHandler(id);
  };

  return (
    <Marker
      key={depot.id}
      position={depot}
      icon={icon}
    >
      <LToolTip
        // `permanent` is a static property (this is a React-Leaflet-specific
        // approach: https://react-leaflet.js.org/docs/en/components). Changing `permanent` prop
        // doesn't result in calling `setPermanent()` on the Leaflet element after the Tooltip component is mounted.
        // We're using `key` to force re-rendering of Tooltip when `isSelected` changes. A similar use case for
        // the `key` property is described here:
        // https://reactjs.org/blog/2018/06/07/you-probably-dont-need-derived-state.html
        // #recommendation-fully-uncontrolled-component-with-a-key
        key={isSelected ? 'selected' : ''}
        permanent={isSelected}
      >
        {`Location: ${depot.description?.split(",")[0]} (${depot.id})`}
        <br />
        {`[Lat=${depot.lat}, Lng=${depot.lng}]`}
      </LToolTip>
      <Popup zoomAnimation maxWidth={400}>
        <Split gutter={GutterSize.md} style={{ overflowY: 'auto' }}>
          <SplitItem
            isFilled
            style={{ display: 'flex', flexDirection: 'column' }}
          >
            <InputGroup data-tip data-for="removeDepot">
              <Button
                variant={ButtonVariant.danger}
                isDisabled={depot.vehicles.length == 0}
                onClick={() => removeHandler(depot.id)}
              >
                <TrashIcon />
              </Button>
              <ReactTooltip id="removeDepot" place="bottom" effect="solid">
                Remove Depot
              </ReactTooltip>              
            </InputGroup>
          </SplitItem>
          <SplitItem
            isFilled
            style={{ display: 'flex', flexDirection: 'column' }}
          >
            <Flex breakpointMods={[{ modifier: FlexModifiers['justify-content-space-between'] }]}>
              <FlexItem>
                <Flex>
                  <FlexItem>
                    <InputGroup>
                      <Button 
                        data-tip data-for="removeVehicle"
                        variant={ButtonVariant.primary}
                        isDisabled={depot.vehicles.length === 0}
                        onClick={() => handleRemoveVehicleClick(depotVehicleIds.pop())}
                      >
                        <MinusIcon />
                      </Button>
                      <ReactTooltip id="removeVehicle" place="bottom" effect="solid">
                        Remove Vehicle
                      </ReactTooltip>              

                      <InputGroupText readOnly>
                        {depot.vehicles.length}
                      </InputGroupText>

                      <Button
                        data-tip data-for="addVehicle"
                        variant={ButtonVariant.primary}
                        onClick={() => handleAddVehicleClick(depot)}
                        data-cy="demo-add-vehicle"
                      >
                        <PlusIcon />
                      </Button>
                      <ReactTooltip id="addVehicle" place="bottom" effect="solid">
                        Add Vehicle
                      </ReactTooltip>
                    </InputGroup>
                  </FlexItem>
                </Flex>
              </FlexItem>
          </Flex>      
          </SplitItem>
          <SplitItem
            isFilled
            style={{ display: 'flex', flexDirection: 'column' }}
          >
            <VehiclesInfo />
          </SplitItem>
        </Split>  
      </Popup>
    </Marker>
  );
};

export default DepotMarker;
