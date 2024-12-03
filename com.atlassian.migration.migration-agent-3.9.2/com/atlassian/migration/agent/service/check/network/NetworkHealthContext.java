/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.network;

import com.atlassian.cmpt.check.base.CheckContext;

public class NetworkHealthContext
implements CheckContext {
    public final String cloudId;

    public NetworkHealthContext(String cloudId) {
        this.cloudId = cloudId;
    }
}

