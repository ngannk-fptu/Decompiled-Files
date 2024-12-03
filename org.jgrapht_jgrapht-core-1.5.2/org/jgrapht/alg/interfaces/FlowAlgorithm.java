/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Collections;
import java.util.Map;

public interface FlowAlgorithm<V, E> {
    default public Flow<E> getFlow() {
        return new FlowImpl<E>(this.getFlowMap());
    }

    public Map<E, Double> getFlowMap();

    public V getFlowDirection(E var1);

    public static class FlowImpl<E>
    implements Flow<E> {
        private Map<E, Double> flowMap;

        public FlowImpl(Map<E, Double> flowMap) {
            this.flowMap = Collections.unmodifiableMap(flowMap);
        }

        @Override
        public Map<E, Double> getFlowMap() {
            return this.flowMap;
        }
    }

    public static interface Flow<E> {
        default public double getFlow(E edge) {
            return this.getFlowMap().get(edge);
        }

        public Map<E, Double> getFlowMap();
    }
}

