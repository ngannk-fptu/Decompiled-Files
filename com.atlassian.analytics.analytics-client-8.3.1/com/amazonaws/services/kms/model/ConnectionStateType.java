/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum ConnectionStateType {
    CONNECTED("CONNECTED"),
    CONNECTING("CONNECTING"),
    FAILED("FAILED"),
    DISCONNECTED("DISCONNECTED"),
    DISCONNECTING("DISCONNECTING");

    private String value;

    private ConnectionStateType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static ConnectionStateType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (ConnectionStateType enumEntry : ConnectionStateType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

