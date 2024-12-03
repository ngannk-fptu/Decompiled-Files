/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.internal.index;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.util.Progress;
import java.util.EnumSet;
import java.util.List;

@Internal
public interface ReindexProgress
extends Progress {
    public void reindexStarted(EnumSet<ReIndexOption> var1, List<String> var2);

    public void reindexFinished(List<String> var1);

    public void reindexStageStarted(ReIndexOption var1);

    public void reindexStageFinished(ReIndexOption var1);

    public void reindexBatchStarted();

    public void reindexBatchFinished();

    public void reIndexSkipped();
}

