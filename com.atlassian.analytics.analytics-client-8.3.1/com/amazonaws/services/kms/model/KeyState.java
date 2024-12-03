/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum KeyState {
    Creating("Creating"),
    Enabled("Enabled"),
    Disabled("Disabled"),
    PendingDeletion("PendingDeletion"),
    PendingImport("PendingImport"),
    PendingReplicaDeletion("PendingReplicaDeletion"),
    Unavailable("Unavailable"),
    Updating("Updating");

    private String value;

    private KeyState(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static KeyState fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (KeyState enumEntry : KeyState.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

