/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.api.service.eviction;

import com.atlassian.confluence.api.model.SynchronyRowsCount;
import com.atlassian.confluence.api.model.content.id.ContentId;
import javax.annotation.Nullable;

public interface SynchronyDataService {
    public SynchronyRowsCount currentSynchronyDatasetSize(@Nullable Long var1);

    public void softRemoveHistoryOlderThan(int var1, int var2);

    public void hardRemoveHistoryOlderThan(int var1);

    public void removeHistoryFor(ContentId var1);

    @Deprecated
    public void removeApplicationCredentials(String var1);

    public void dataCleanUpAfterTurningOffCollabEditing(String var1);
}

