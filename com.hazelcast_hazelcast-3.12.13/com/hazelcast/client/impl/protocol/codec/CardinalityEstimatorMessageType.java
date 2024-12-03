/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum CardinalityEstimatorMessageType {
    CARDINALITYESTIMATOR_ADD(7169),
    CARDINALITYESTIMATOR_ESTIMATE(7170);

    private final int id;

    private CardinalityEstimatorMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

