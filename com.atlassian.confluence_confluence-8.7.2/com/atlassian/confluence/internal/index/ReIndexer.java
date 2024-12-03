/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.index;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.confluence.index.ReIndexSpec;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.util.Progress;
import java.util.EnumSet;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

@Internal
public interface ReIndexer {
    default public void reIndex(Progress progress) {
        this.reIndex(ReIndexOption.fullReindex(), progress);
    }

    public void reIndex(EnumSet<ReIndexOption> var1, Progress var2);

    public void reIndex(EnumSet<ReIndexOption> var1, @NonNull SearchQuery var2, Progress var3);

    public void reIndex(EnumSet<ReIndexOption> var1, List<String> var2, Progress var3);

    @ExperimentalApi
    public void reIndex(ReIndexSpec var1, Progress var2);
}

