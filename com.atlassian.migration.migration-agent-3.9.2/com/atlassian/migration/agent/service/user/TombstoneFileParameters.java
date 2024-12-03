/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import javax.annotation.Nullable;
import lombok.Generated;

public class TombstoneFileParameters {
    private static final TombstoneFileParameters WITHOUT_FILE = new TombstoneFileParameters(null);
    @Nullable
    private final String planId;

    public static TombstoneFileParameters withFile(String planId) {
        return new TombstoneFileParameters(planId);
    }

    public static TombstoneFileParameters withoutFile() {
        return WITHOUT_FILE;
    }

    @Generated
    private TombstoneFileParameters(@Nullable String planId) {
        this.planId = planId;
    }

    @Nullable
    @Generated
    public String getPlanId() {
        return this.planId;
    }
}

