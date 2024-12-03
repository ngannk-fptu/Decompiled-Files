/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.store.Directory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.DefaultConfiguration;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import com.atlassian.confluence.internal.search.v2.lucene.SearchTokenExpiredException;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

public interface ILuceneConnection {
    public static final Configuration DEFAULT_CONFIGURATION = new DefaultConfiguration();

    public void withSearch(SearcherAction var1) throws LuceneException;

    public <T> T withSearcher(SearcherWithTokenAction<T> var1);

    public <T> T withSearcher(long var1, SearcherWithTokenAction<T> var3) throws SearchTokenExpiredException;

    public Object withReader(ReaderAction var1) throws LuceneException;

    public void withWriter(WriterAction var1) throws LuceneException;

    public void withBatchUpdate(BatchUpdateAction var1);

    public void optimize() throws LuceneException;

    public void close() throws LuceneException;

    @Deprecated
    public void closeWriter() throws LuceneException;

    public int getNumDocs();

    public void truncateIndex() throws LuceneException;

    public void snapshot(Directory var1) throws IOException;

    public void reset(Runnable var1) throws LuceneException;

    public static interface Configuration {
        public int getInteractiveMergeFactor();

        public int getInteractiveMaxMergeDocs();

        public int getInteractiveMaxBufferedDocs();

        public int getBatchMergeFactor();

        public int getBatchMaxMergeDocs();

        public int getBatchMaxBufferedDocs();

        public int getMaxFieldLength();

        public boolean isCompoundIndexFileFormat();

        public long getIndexSearcherMaxAge();

        public long getIndexSearcherPruneDelay();
    }

    public static interface BatchUpdateAction {
        public void perform() throws Exception;
    }

    public static interface WriterAction {
        public void perform(IndexWriter var1) throws IOException;
    }

    public static interface ReaderAction {
        public Object perform(IndexReader var1) throws IOException;
    }

    public static interface SearcherWithTokenAction<T> {
        public T perform(IndexSearcher var1, long var2) throws IOException;
    }

    public static interface SearcherAction {
        public void perform(IndexSearcher var1) throws IOException;
    }
}

