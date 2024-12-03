/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy
 */
package com.atlassian.confluence.impl.retention.manager;

import com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy;

public interface GlobalRetentionPolicyManager {
    public void savePolicy(GlobalRetentionPolicy var1);

    public GlobalRetentionPolicy getPolicy();
}

