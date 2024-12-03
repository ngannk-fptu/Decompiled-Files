/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.ReIndexOption
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.rest.service;

import com.atlassian.confluence.search.ReIndexOption;
import java.util.EnumSet;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ReIndexService {
    public static final String REINDEX_CLUSTER_LOCK_NAME = "confluence_reindex_cluster_lock";
    public static final long REINDEX_CLUSTER_LOCK_ACQUIRE_TIMEOUT_MS = Long.getLong("confluence.reindex.cluster.lock.acquire.timeout.ms", 10000L);

    public boolean isReIndexing();

    public boolean reindex(@NonNull List<String> var1, @NonNull EnumSet<ReIndexOption> var2) throws InterruptedException;

    public void resetJobStatus();
}

