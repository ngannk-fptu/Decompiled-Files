/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.prc.model;

import lombok.Generated;

public enum CommandName {
    CHECK("check"),
    MIGRATE("migrate");

    private final String name;

    private CommandName(String name) {
        this.name = name;
    }

    @Generated
    public String getName() {
        return this.name;
    }
}

