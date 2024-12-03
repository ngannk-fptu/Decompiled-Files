/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.dto;

import lombok.Generated;

public class AppDto {
    public final String key;
    public final String name;

    @Generated
    public AppDto(String key, String name) {
        this.key = key;
        this.name = name;
    }

    @Generated
    public String getKey() {
        return this.key;
    }

    @Generated
    public String getName() {
        return this.name;
    }
}

