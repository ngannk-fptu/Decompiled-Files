/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.index.IndexRecoverer
 *  com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.index;

import com.atlassian.confluence.api.model.index.IndexRecoverer;
import com.atlassian.confluence.internal.index.lucene.FullReindexManager;
import com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil;
import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import java.io.File;
import java.io.IOException;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChangeIndexRecoverer
implements IndexRecoverer {
    private ILuceneConnection changeLuceneConnection;
    private FullReindexManager fullReindexManager;

    public void snapshot(@NonNull File destDir) throws IOException {
        this.changeLuceneConnection.snapshot(DirectoryUtil.getDirectory((File)destDir));
    }

    public void reset(@NonNull Runnable replaceIndex) {
        this.changeLuceneConnection.reset(replaceIndex);
    }

    public void reindex() {
        this.fullReindexManager.reIndex();
    }

    public void setChangeLuceneConnection(@NonNull ILuceneConnection changeLuceneConnection) {
        this.changeLuceneConnection = changeLuceneConnection;
    }

    public void setFullReindexManager(@NonNull FullReindexManager fullReindexManager) {
        this.fullReindexManager = fullReindexManager;
    }
}

