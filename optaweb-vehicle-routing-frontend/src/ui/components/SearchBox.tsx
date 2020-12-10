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

import '@patternfly/patternfly/patternfly.css';
import { Button, Text, TextContent, TextInput, TextVariants, Split, SplitItem, GutterSize } from '@patternfly/react-core';
import { PlusSquareIcon, EnterpriseIcon, LocationArrowIcon, CarIcon } from '@patternfly/react-icons';
import { latLng } from 'leaflet';
import { OpenStreetMapProvider } from 'leaflet-geosearch';
import { type } from 'os';
import * as React from 'react';
import { LatLngWithTypeDescription, LatLng, LocationType } from 'store/route/types';

export interface Props {
  searchDelay: number;
  boundingBox: [LatLng, LatLng] | null;
  countryCodeSearchFilter: string[];
  addVisitHandler: (result: Result) => void;
  addDepotHandler: (result: Result)  => void;
  addVehicleHandler: (result: Result) => void;
}

export interface State {
  query: string;
  results: Result[];
  attributions: string[];
}

export interface Result {
  address: string;
  latLng: LatLngWithTypeDescription;
}

const searchParams = (props: Props) => ({
  countrycodes: props.countryCodeSearchFilter,
  viewbox: props.boundingBox
    ? [props.boundingBox[0].lng, props.boundingBox[0].lat, props.boundingBox[1].lng, props.boundingBox[1].lat]
    : undefined,
  bounded: !!props.boundingBox,
});

class SearchBox extends React.Component<Props, State> {
  // eslint-disable-next-line max-len
  // https://github.com/airbnb/javascript/blob/eslint-config-airbnb-v18.1.0/packages/eslint-config-airbnb/rules/react.js#L489
  // TODO remove this suppression once the TODO above is resolved:
  // eslint-disable-next-line react/static-property-placement
  static defaultProps: Pick<Props, 'searchDelay'> = {
    searchDelay: 500,
  };

  private searchProvider: OpenStreetMapProvider;

  private timeoutId: number | null;

  constructor(props: Props) {
    super(props);

    this.state = {
      query: '',
      results: [],
      attributions: [],
    };

    this.searchProvider = new OpenStreetMapProvider({ params: searchParams(props) });
    this.timeoutId = null;

    this.handleTextInputChange = this.handleTextInputChange.bind(this);
    this.handleVisitClick = this.handleVisitClick.bind(this);
    this.handleDepotClick = this.handleDepotClick.bind(this);
  }

  componentDidUpdate() {
    this.searchProvider = new OpenStreetMapProvider({ params: searchParams(this.props) });
  }

  componentWillUnmount() {
    if (this.timeoutId) {
      window.clearTimeout(this.timeoutId);
    }
  }

  handleTextInputChange(query: string): void {
    if (this.timeoutId) {
      window.clearTimeout(this.timeoutId);
    }
    if (query.trim() !== '') {
      this.timeoutId = window.setTimeout(
        async () => {
          const searchResults = await this.searchProvider.search({ query });
          if (this.state.query !== query) {
            return;
          }
          this.setState({
            results: searchResults
              .map((result) => ({
                address: result.label,
                latLng: { type: LocationType.Other, lat: result.y, lng: result.x, description: result.label},
              })),
            attributions: searchResults
              .map((result) => result.raw.licence)
              // filter out duplicate elements
              .filter((value, index, array) => array.indexOf(value) === index),
          });
        },
        this.props.searchDelay,
      );
      this.setState({ query });
    } else {
      this.setState({ query, results: [], attributions: [] });
    }
  }

  handleVisitClick(index: number) {
    let resultCopy = Object.assign({}, this.state.results[index]);
    resultCopy.latLng.type = LocationType.Visit;
    this.props.addVisitHandler(resultCopy);

    this.setState({
      query: '',
      results: [],
      attributions: [],
    });
    // TODO focus text input
  }

  handleDepotClick(index: number) {
    let resultCopy = Object.assign({}, this.state.results[index]);
    resultCopy.latLng.type = LocationType.Depot;
    this.props.addDepotHandler(resultCopy);

    this.setState({
      query: '',
      results: [],
      attributions: [],
    });
    // TODO focus text input
  }

  render() {
    const { attributions, query, results } = this.state;
    return (
      <>
        <TextInput
          style={{ marginBottom: 10 }}
          value={query}
          type="search"
          placeholder="Search to add a location..."
          aria-label="geosearch text input"
          onChange={this.handleTextInputChange}
          data-cy="geosearch-text-input"
        />
        {results.length > 0 && (
          <div className="pf-c-options-menu pf-m-expanded" style={{ zIndex: 1100 }}>
            <ul className="pf-c-options-menu__menu">
              {results.map((result, index) => (
                <li key={`result: ${result}`}>
                  <div className="pf-c-options-menu__menu-item">
                    <Split gutter={GutterSize.md} style={{ overflowY: 'auto', overflowX: 'auto' }}>
                      <SplitItem
                        isFilled={true}
                        style={{ display: 'grid', flexDirection: 'column' }}
                      >
                        {result.address}
                      </SplitItem>
                      <SplitItem
                        isFilled={true}
                        style={{ display: 'grid', flexDirection: 'column' }}
                      >
                        <Button
                          className="pf-c-options-menu__menu-item-icon"
                          variant="link"
                          type="button"
                          onClick={() => this.handleVisitClick(index)}
                          data-cy={`geosearch-location-item-button-${index}`}
                        >
                          <LocationArrowIcon />
                        </Button>
                      </SplitItem>
                      <SplitItem
                        isFilled={true}
                        style={{ display: 'grid', flexDirection: 'column' }}
                      >
                        <Button
                          className="pf-c-options-menu__menu-item-icon"
                          variant="link"
                          type="button"
                          onClick={() => this.handleDepotClick(index)}
                          data-cy={`geosearch-location-item-button-${index}`}
                        >
                          <EnterpriseIcon />
                        </Button>
                      </SplitItem>
                      <SplitItem
                        isFilled={true}
                        style={{ display: 'grid', flexDirection: 'column' }}
                      >
                        <Button
                          className="pf-c-options-menu__menu-item-icon"
                          variant="link"
                          type="button"
                          onClick={() => this.handleDepotClick(index)}
                          data-cy={`geosearch-location-item-button-${index}`}
                          isDisabled={true}
                        >
                          <CarIcon />
                        </Button>
                      </SplitItem>
                    </Split>
                  </div>
                </li>
              ))}

              <li className="pf-c-options-menu__separator" role="separator" />

              {attributions.map((attribution) => (
                <li
                  key={`attrib: ${attribution}`}
                  className="pf-c-options-menu__menu-item pf-m-disabled"
                >
                  <TextContent>
                    <Text
                      component={TextVariants.small}
                    >
                      {attribution}
                    </Text>
                  </TextContent>
                </li>
              ))}
            </ul>
          </div>
        )}
      </>
    );
  }
}

export default SearchBox;
