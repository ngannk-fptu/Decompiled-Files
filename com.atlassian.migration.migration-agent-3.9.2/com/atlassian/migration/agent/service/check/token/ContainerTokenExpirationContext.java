/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.token;

import com.atlassian.cmpt.check.base.CheckContext;

public class ContainerTokenExpirationContext
implements CheckContext {
    public final String cloudId;

    public ContainerTokenExpirationContext(String cloudId) {
        this.cloudId = cloudId;
    }
}

