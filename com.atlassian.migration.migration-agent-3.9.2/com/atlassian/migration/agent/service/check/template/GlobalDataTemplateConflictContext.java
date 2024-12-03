/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.template;

import com.atlassian.cmpt.check.base.CheckContext;
import lombok.Generated;

public class GlobalDataTemplateConflictContext
implements CheckContext {
    public final String cloudId;
    private final String templateType;

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public String getTemplateType() {
        return this.templateType;
    }

    @Generated
    public GlobalDataTemplateConflictContext(String cloudId, String templateType) {
        this.cloudId = cloudId;
        this.templateType = templateType;
    }
}

