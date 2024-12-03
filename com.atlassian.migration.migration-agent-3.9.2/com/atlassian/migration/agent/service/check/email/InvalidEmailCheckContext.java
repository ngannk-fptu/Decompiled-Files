/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.email;

import com.atlassian.cmpt.check.base.CheckContext;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2FilePayload;
import java.util.Set;
import lombok.Generated;

public class InvalidEmailCheckContext
implements CheckContext {
    private String cloudId;
    private String executionId;
    private Set<String> spaceKeys;
    private UsersMigrationV2FilePayload payload;

    @Generated
    public InvalidEmailCheckContext(String cloudId, String executionId, Set<String> spaceKeys, UsersMigrationV2FilePayload payload) {
        this.cloudId = cloudId;
        this.executionId = executionId;
        this.spaceKeys = spaceKeys;
        this.payload = payload;
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public String getExecutionId() {
        return this.executionId;
    }

    @Generated
    public Set<String> getSpaceKeys() {
        return this.spaceKeys;
    }

    @Generated
    public UsersMigrationV2FilePayload getPayload() {
        return this.payload;
    }
}

