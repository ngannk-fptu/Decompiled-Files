/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Map;
import org.jgrapht.alg.interfaces.FlowAlgorithm;

public interface MaximumFlowAlgorithm<V, E>
extends FlowAlgorithm<V, E> {
    public MaximumFlow<E> getMaximumFlow(V var1, V var2);

    default public double getMaximumFlowValue(V source, V sink) {
        return this.getMaximumFlow(source, sink).getValue();
    }

    public static interface MaximumFlow<E>
    extends FlowAlgorithm.Flow<E> {
        public Double getValue();
    }

    public static class MaximumFlowImpl<E>
    extends FlowAlgorithm.FlowImpl<E>
    implements MaximumFlow<E> {
        private Double value;

        public MaximumFlowImpl(Double value, Map<E, Double> flow) {
            super(flow);
            this.value = value;
        }

        @Override
        public Double getValue() {
            return this.value;
        }

        public String toString() {
            return "Flow Value: " + this.value + "\nFlow map:\n" + this.getFlowMap();
        }
    }
}

