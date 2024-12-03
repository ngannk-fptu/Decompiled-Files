/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

public enum QueryConstants {
    KEY_ATTRIBUTE_NAME("__key"),
    THIS_ATTRIBUTE_NAME("this");

    private final String value;

    private QueryConstants(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}

