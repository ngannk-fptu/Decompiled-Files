/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.confluence.api.model.retention.RetentionPolicy;

public interface VersionRemovalService {
    public void softRemoveVersions(RetentionPolicy var1, int var2);

    public void hardRemoveVersions(RetentionPolicy var1);
}

