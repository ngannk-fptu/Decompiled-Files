/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics;

public enum ProbeLevel {
    MANDATORY(2),
    INFO(1),
    DEBUG(0);

    private final int precedence;

    private ProbeLevel(int precedence) {
        this.precedence = precedence;
    }

    public boolean isEnabled(ProbeLevel minimumLevel) {
        return this.precedence >= minimumLevel.precedence;
    }
}

