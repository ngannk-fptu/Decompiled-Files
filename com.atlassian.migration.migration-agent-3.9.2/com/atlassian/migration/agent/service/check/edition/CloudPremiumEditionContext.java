/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.edition;

import com.atlassian.cmpt.check.base.CheckContext;
import lombok.Generated;

public class CloudPremiumEditionContext
implements CheckContext {
    private String cloudId;

    @Generated
    public CloudPremiumEditionContext(String cloudId) {
        this.cloudId = cloudId;
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }
}

