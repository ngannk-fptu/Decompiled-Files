/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.search.SearcherLifetimeManager
 *  org.apache.lucene.search.SearcherManager
 *  org.apache.lucene.store.Directory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.SearcherLifetimeManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;

public interface LuceneAccessor {
    public Analyzer getAnalyzer();

    public void execute(Consumer<IndexWriter> var1);

    public SearcherManager getSearcherManager();

    public SearcherLifetimeManager getSearcherLifetimeManager();

    public void snapshot(Directory var1) throws IOException;

    public void reset(Runnable var1) throws LuceneException;

    public void close();

    @Nullable
    public Path getPath();
}

