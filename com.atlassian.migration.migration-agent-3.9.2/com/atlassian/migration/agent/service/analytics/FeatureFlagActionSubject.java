/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.analytics;

import lombok.Generated;

public enum FeatureFlagActionSubject {
    PLAN("plan"),
    PREFLIGHT("preflight");

    private final String value;

    @Generated
    public String getValue() {
        return this.value;
    }

    @Generated
    private FeatureFlagActionSubject(String value) {
        this.value = value;
    }
}

