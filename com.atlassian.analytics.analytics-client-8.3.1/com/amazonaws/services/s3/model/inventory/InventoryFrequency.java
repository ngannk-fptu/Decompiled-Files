/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.inventory;

public enum InventoryFrequency {
    Daily("Daily"),
    Weekly("Weekly");

    private final String frequency;

    private InventoryFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String toString() {
        return this.frequency;
    }
}

