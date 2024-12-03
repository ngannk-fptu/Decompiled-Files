/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy
 */
package com.atlassian.confluence.retention;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy;

@Internal
public interface GlobalRetentionPolicyService {
    public void savePolicy(GlobalRetentionPolicy var1);

    public GlobalRetentionPolicy getPolicy();
}

