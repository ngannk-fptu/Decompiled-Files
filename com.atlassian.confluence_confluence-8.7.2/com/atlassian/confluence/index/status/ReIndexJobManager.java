/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.index.status;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.index.status.ReIndexJob;
import java.util.List;
import java.util.Optional;

@Internal
public interface ReIndexJobManager {
    public Optional<ReIndexJob> getRunningOrMostRecentReIndex();

    public Optional<ReIndexJob> createNewJob(List<String> var1);

    public boolean acknowledgeRunningJob() throws InterruptedException;

    public void clear();
}

