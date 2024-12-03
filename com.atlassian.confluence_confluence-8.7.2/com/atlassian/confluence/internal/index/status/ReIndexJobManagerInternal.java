/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.status;

import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexJobManager;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public interface ReIndexJobManagerInternal
extends ReIndexJobManager {
    public static final String REINDEX_IMPROVEMENT_DARKFEATURE_KEY = "confluence.reindex.improvements";
    public static final String JOB_PERSISTER_LOCK_NAME = "confluence.rendex.job.persister.lock";

    public void updateReIndexJobIfPresent(Consumer<ReIndexJob> var1) throws InterruptedException, TimeoutException;

    public void updateReIndexJob(ReIndexJob var1) throws InterruptedException, TimeoutException;
}

