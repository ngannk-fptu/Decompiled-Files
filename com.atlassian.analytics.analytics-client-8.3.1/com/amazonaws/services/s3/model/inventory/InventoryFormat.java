/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.inventory;

public enum InventoryFormat {
    CSV("CSV"),
    ORC("ORC"),
    Parquet("Parquet");

    private final String format;

    private InventoryFormat(String format) {
        this.format = format;
    }

    public String toString() {
        return this.format;
    }
}

