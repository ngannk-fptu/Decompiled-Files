/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.retention;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import java.util.Optional;

@ExperimentalApi
@Deprecated
public interface SpaceRetentionPolicyService {
    public void deletePolicy(String var1);

    public void savePolicy(String var1, SpaceRetentionPolicy var2);

    public Optional<SpaceRetentionPolicy> getPolicy(String var1);

    public Optional<SpaceRetentionPolicy> getPolicy(long var1);
}

