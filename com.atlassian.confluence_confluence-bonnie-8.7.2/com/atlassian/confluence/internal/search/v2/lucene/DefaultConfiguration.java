/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import java.util.concurrent.TimeUnit;

public class DefaultConfiguration
implements ILuceneConnection.Configuration {
    private int batchMaxBufferedDocs = 300;
    private int batchMaxMergeDocs = Integer.MAX_VALUE;
    private int batchMergeFactor = 50;
    private int interactiveMaxBufferedDocs = 300;
    private int interactiveMaxMergeDocs = 5000;
    private int interactiveMergeFactor = 4;
    private long indexSearcherMaxAge = TimeUnit.MINUTES.toSeconds(2L);
    private long indexSearcherPruneDelay = TimeUnit.MINUTES.toSeconds(1L);
    private int maxFieldLength = 1000000;
    private boolean compoundIndexFileFormat = true;

    @Override
    public int getBatchMaxBufferedDocs() {
        return this.batchMaxBufferedDocs;
    }

    public void setBatchMaxBufferedDocs(int batchMaxBufferedDocs) {
        this.batchMaxBufferedDocs = batchMaxBufferedDocs;
    }

    @Override
    public int getBatchMaxMergeDocs() {
        return this.batchMaxMergeDocs;
    }

    public void setBatchMaxMergeDocs(int batchMaxMergeDocs) {
        this.batchMaxMergeDocs = batchMaxMergeDocs;
    }

    @Override
    public int getBatchMergeFactor() {
        return this.batchMergeFactor;
    }

    public void setBatchMergeFactor(int batchMergeFactor) {
        this.batchMergeFactor = batchMergeFactor;
    }

    @Override
    public int getInteractiveMaxBufferedDocs() {
        return this.interactiveMaxBufferedDocs;
    }

    public void setInteractiveMaxBufferedDocs(int interactiveMaxBufferedDocs) {
        this.interactiveMaxBufferedDocs = interactiveMaxBufferedDocs;
    }

    @Override
    public int getInteractiveMaxMergeDocs() {
        return this.interactiveMaxMergeDocs;
    }

    public void setInteractiveMaxMergeDocs(int interactiveMaxMergeDocs) {
        this.interactiveMaxMergeDocs = interactiveMaxMergeDocs;
    }

    @Override
    public int getInteractiveMergeFactor() {
        return this.interactiveMergeFactor;
    }

    public void setInteractiveMergeFactor(int interactiveMergeFactor) {
        this.interactiveMergeFactor = interactiveMergeFactor;
    }

    @Override
    public int getMaxFieldLength() {
        return this.maxFieldLength;
    }

    public void setMaxFieldLength(int maxFieldLength) {
        this.maxFieldLength = maxFieldLength;
    }

    @Override
    public boolean isCompoundIndexFileFormat() {
        return this.compoundIndexFileFormat;
    }

    public void setCompoundIndexFileFormat(boolean compoundIndexFileFormat) {
        this.compoundIndexFileFormat = compoundIndexFileFormat;
    }

    @Override
    public long getIndexSearcherMaxAge() {
        return this.indexSearcherMaxAge;
    }

    public void setIndexSearcherMaxAge(long indexSearcherMaxAge) {
        this.indexSearcherMaxAge = indexSearcherMaxAge;
    }

    @Override
    public long getIndexSearcherPruneDelay() {
        return this.indexSearcherPruneDelay;
    }

    public void setIndexSearcherPruneDelay(long indexSearcherPruneDelay) {
        this.indexSearcherPruneDelay = indexSearcherPruneDelay;
    }
}

