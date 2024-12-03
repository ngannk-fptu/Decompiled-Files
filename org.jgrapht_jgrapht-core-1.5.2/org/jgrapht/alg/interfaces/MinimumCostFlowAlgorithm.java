/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Map;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.FlowAlgorithm;

public interface MinimumCostFlowAlgorithm<V, E>
extends FlowAlgorithm<V, E> {
    public MinimumCostFlow<E> getMinimumCostFlow(MinimumCostFlowProblem<V, E> var1);

    default public double getFlowCost(MinimumCostFlowProblem<V, E> minimumCostFlowProblem) {
        return this.getMinimumCostFlow(minimumCostFlowProblem).getCost();
    }

    public static interface MinimumCostFlow<E>
    extends FlowAlgorithm.Flow<E> {
        public double getCost();
    }

    public static class MinimumCostFlowImpl<E>
    extends FlowAlgorithm.FlowImpl<E>
    implements MinimumCostFlow<E> {
        double cost;

        public MinimumCostFlowImpl(double cost, Map<E, Double> flowMap) {
            super(flowMap);
            this.cost = cost;
        }

        @Override
        public double getCost() {
            return this.cost;
        }
    }
}

