/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.dto;

import lombok.Generated;

public class ProductDto {
    String name;
    String version;

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getVersion() {
        return this.version;
    }

    @Generated
    public ProductDto(String name, String version) {
        this.name = name;
        this.version = version;
    }
}

