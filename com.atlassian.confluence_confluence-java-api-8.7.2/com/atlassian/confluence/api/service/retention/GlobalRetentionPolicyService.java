/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.retention;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy;

@ExperimentalApi
@Deprecated
public interface GlobalRetentionPolicyService {
    public void savePolicy(GlobalRetentionPolicy var1);

    public GlobalRetentionPolicy getPolicy();
}

