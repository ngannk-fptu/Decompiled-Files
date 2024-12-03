/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.index.IndexRecoverer
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.api.model.index.IndexRecoverer;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexBuilder;
import com.atlassian.confluence.plugins.edgeindex.EdgeSearchIndexAccessor;
import java.io.File;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;

public class EdgeIndexRecoverer
implements IndexRecoverer {
    private final EdgeIndexBuilder edgeIndexBuilder;
    private final EdgeSearchIndexAccessor edgeSearchIndexAccessor;

    public EdgeIndexRecoverer(EdgeSearchIndexAccessor edgeSearchIndexAccessor, @Qualifier(value="edgeIndexBuilder") EdgeIndexBuilder edgeIndexBuilder) {
        this.edgeSearchIndexAccessor = edgeSearchIndexAccessor;
        this.edgeIndexBuilder = edgeIndexBuilder;
    }

    public void snapshot(@NonNull File destDir) {
        this.edgeSearchIndexAccessor.snapshot(destDir);
    }

    public void reset(@NonNull Runnable replaceIndex) {
        this.edgeSearchIndexAccessor.reset(replaceIndex);
    }

    public void reindex() {
        this.edgeIndexBuilder.rebuild(EdgeIndexBuilder.EDGE_INDEX_REBUILD_DEFAULT_START_PERIOD, EdgeIndexBuilder.RebuildCondition.FORCE);
    }
}

