/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 */
package com.atlassian.confluence.impl.retention.manager;

import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import java.util.Optional;

public interface SpaceRetentionPolicyManager {
    public void deletePolicy(String var1);

    public void savePolicy(String var1, SpaceRetentionPolicy var2);

    public Optional<SpaceRetentionPolicy> getPolicy(String var1);

    public Optional<SpaceRetentionPolicy> getPolicy(long var1);
}

