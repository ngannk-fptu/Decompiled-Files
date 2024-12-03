/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.job.scope;

import com.atlassian.migration.agent.mapi.job.scope.ScopeMode;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class UsersGroupsScope {
    @JsonProperty
    @Nullable
    private ScopeMode mode = ScopeMode.REFERENCED;

    @Generated
    public UsersGroupsScope(@Nullable ScopeMode mode) {
        this.mode = mode;
    }

    @Generated
    public UsersGroupsScope() {
    }

    @Nullable
    @Generated
    public ScopeMode getMode() {
        return this.mode;
    }
}

