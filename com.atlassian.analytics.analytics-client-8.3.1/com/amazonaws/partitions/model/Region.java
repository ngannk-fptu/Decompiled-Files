/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.partitions.model;

import com.amazonaws.util.ValidationUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Region {
    private final String description;

    public Region(@JsonProperty(value="description") String description) {
        this.description = ValidationUtils.assertNotNull(description, "Region description");
    }

    public String getDescription() {
        return this.description;
    }
}

