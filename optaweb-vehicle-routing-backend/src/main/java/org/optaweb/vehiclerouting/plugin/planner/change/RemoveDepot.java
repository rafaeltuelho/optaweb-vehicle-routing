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

public class RemoveDepot implements ProblemFactChange<VehicleRoutingSolution> {

    private final PlanningDepot planningDepot;

    public RemoveDepot(PlanningDepot planningDepot) {
        this.planningDepot = Objects.requireNonNull(planningDepot);
    }

    @Override
    public void doChange(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution workingSolution = scoreDirector.getWorkingSolution();

        // Look up a working copy of the visit
        PlanningDepot workingDepot = scoreDirector.lookUpWorkingObject(planningDepot);
        if (workingDepot == null) {
            throw new IllegalStateException("Can't look up a working copy of " + planningDepot);
        }

        // No need to clone the visitList because it is a problem fact collection, so it is already planning-cloned.
        // To learn more about problem fact changes, see:
        // https://docs.jboss.org/optaplanner/release/latest/optaplanner-docs/html_single/#problemFactChangeExample

        // Remove the depot fact
        scoreDirector.beforeEntityRemoved(workingDepot);
        if (!workingSolution.getDepotList().remove(workingDepot)) {
            throw new IllegalStateException(
                    "Working solution's depotList "
                            + workingSolution.getDepotList()
                            + " doesn't contain the workingVisit ("
                            + workingDepot
                            + "). This is a bug!");
        }
        scoreDirector.afterEntityRemoved(workingDepot);

        scoreDirector.triggerVariableListeners();
    }
}