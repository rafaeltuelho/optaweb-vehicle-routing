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

package org.optaweb.vehiclerouting.plugin.planner.change;

import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

public class AddDepot implements ProblemFactChange<VehicleRoutingSolution> {

    private final PlanningDepot depot;

    public AddDepot(PlanningDepot depot) {
        this.depot = Objects.requireNonNull(depot);
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        scoreDirector.beforeProblemFactAdded(depot);
        scoreDirector.getWorkingSolution().getDepotList().add(depot);
        scoreDirector.afterProblemFactAdded(depot);

        scoreDirector.triggerVariableListeners();
    }
}
