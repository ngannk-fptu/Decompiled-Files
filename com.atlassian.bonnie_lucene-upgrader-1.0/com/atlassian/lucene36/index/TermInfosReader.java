/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.SegmentTermEnum;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermInfo;
import com.atlassian.lucene36.index.TermInfosReaderIndex;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.util.BytesRef;
import com.atlassian.lucene36.util.CloseableThreadLocal;
import com.atlassian.lucene36.util.DoubleBarrelLRUCache;
import java.io.Closeable;
import java.io.IOException;

final class TermInfosReader
implements Closeable {
    private final Directory directory;
    private final String segment;
    private final FieldInfos fieldInfos;
    private final CloseableThreadLocal<ThreadResources> threadResources = new CloseableThreadLocal();
    private final SegmentTermEnum origEnum;
    private final long size;
    private final TermInfosReaderIndex index;
    private final int indexLength;
    private final int totalIndexInterval;
    private static final int DEFAULT_CACHE_SIZE = 1024;
    private final DoubleBarrelLRUCache<CloneableTerm, TermInfoAndOrd> termsCache = new DoubleBarrelLRUCache(1024);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    TermInfosReader(Directory dir, String seg, FieldInfos fis, int readBufferSize, int indexDivisor) throws CorruptIndexException, IOException {
        boolean success = false;
        if (indexDivisor < 1 && indexDivisor != -1) {
            throw new IllegalArgumentException("indexDivisor must be -1 (don't load terms index) or greater than 0: got " + indexDivisor);
        }
        try {
            block6: {
                this.directory = dir;
                this.segment = seg;
                this.fieldInfos = fis;
                this.origEnum = new SegmentTermEnum(this.directory.openInput(IndexFileNames.segmentFileName(this.segment, "tis"), readBufferSize), this.fieldInfos, false);
                this.size = this.origEnum.size;
                if (indexDivisor != -1) {
                    this.totalIndexInterval = this.origEnum.indexInterval * indexDivisor;
                    String indexFileName = IndexFileNames.segmentFileName(this.segment, "tii");
                    SegmentTermEnum indexEnum = new SegmentTermEnum(this.directory.openInput(indexFileName, readBufferSize), this.fieldInfos, true);
                    try {
                        this.index = new TermInfosReaderIndex(indexEnum, indexDivisor, dir.fileLength(indexFileName), this.totalIndexInterval);
                        this.indexLength = this.index.length();
                        Object var10_9 = null;
                    }
                    catch (Throwable throwable) {
                        Object var10_10 = null;
                        indexEnum.close();
                        throw throwable;
                    }
                    indexEnum.close();
                    {
                        break block6;
                    }
                }
                this.totalIndexInterval = -1;
                this.index = null;
                this.indexLength = -1;
            }
            success = true;
            Object var12_12 = null;
            if (success) return;
        }
        catch (Throwable throwable) {
            Object var12_13 = null;
            if (success) throw throwable;
            this.close();
            throw throwable;
        }
        this.close();
    }

    public int getSkipInterval() {
        return this.origEnum.skipInterval;
    }

    public int getMaxSkipLevels() {
        return this.origEnum.maxSkipLevels;
    }

    public final void close() throws IOException {
        if (this.origEnum != null) {
            this.origEnum.close();
        }
        this.threadResources.close();
    }

    final long size() {
        return this.size;
    }

    private ThreadResources getThreadResources() {
        ThreadResources resources = this.threadResources.get();
        if (resources == null) {
            resources = new ThreadResources();
            resources.termEnum = this.terms();
            this.threadResources.set(resources);
        }
        return resources;
    }

    TermInfo get(Term term) throws IOException {
        BytesRef termBytesRef = new BytesRef(term.text);
        return this.get(term, false, termBytesRef);
    }

    private TermInfo get(Term term, boolean mustSeekEnum, BytesRef termBytesRef) throws IOException {
        TermInfo ti;
        int enumOffset;
        if (this.size == 0L) {
            return null;
        }
        this.ensureIndexIsRead();
        CloneableTerm cacheKey = new CloneableTerm(term);
        TermInfoAndOrd tiOrd = this.termsCache.get(cacheKey);
        ThreadResources resources = this.getThreadResources();
        if (!mustSeekEnum && tiOrd != null) {
            return tiOrd;
        }
        SegmentTermEnum enumerator = resources.termEnum;
        if (enumerator.term() != null && (enumerator.prev() != null && term.compareTo(enumerator.prev()) > 0 || term.compareTo(enumerator.term()) >= 0) && (this.indexLength == (enumOffset = (int)(enumerator.position / (long)this.totalIndexInterval) + 1) || this.index.compareTo(term, termBytesRef, enumOffset) < 0)) {
            TermInfo ti2;
            int numScans = enumerator.scanTo(term);
            if (enumerator.term() != null && term.compareTo(enumerator.term()) == 0) {
                ti2 = enumerator.termInfo();
                if (numScans > 1) {
                    if (tiOrd == null) {
                        this.termsCache.put(cacheKey, new TermInfoAndOrd(ti2, enumerator.position));
                    } else {
                        assert (this.sameTermInfo(ti2, tiOrd, enumerator));
                        assert ((long)((int)enumerator.position) == tiOrd.termOrd);
                    }
                }
            } else {
                ti2 = null;
            }
            return ti2;
        }
        int indexPos = tiOrd != null ? (int)(tiOrd.termOrd / (long)this.totalIndexInterval) : this.index.getIndexOffset(term, termBytesRef);
        this.index.seekEnum(enumerator, indexPos);
        enumerator.scanTo(term);
        if (enumerator.term() != null && term.compareTo(enumerator.term()) == 0) {
            ti = enumerator.termInfo();
            if (tiOrd == null) {
                this.termsCache.put(cacheKey, new TermInfoAndOrd(ti, enumerator.position));
            } else {
                assert (this.sameTermInfo(ti, tiOrd, enumerator));
                assert (enumerator.position == tiOrd.termOrd);
            }
        } else {
            ti = null;
        }
        return ti;
    }

    private final boolean sameTermInfo(TermInfo ti1, TermInfo ti2, SegmentTermEnum enumerator) {
        if (ti1.docFreq != ti2.docFreq) {
            return false;
        }
        if (ti1.freqPointer != ti2.freqPointer) {
            return false;
        }
        if (ti1.proxPointer != ti2.proxPointer) {
            return false;
        }
        return ti1.docFreq < enumerator.skipInterval || ti1.skipOffset == ti2.skipOffset;
    }

    private void ensureIndexIsRead() {
        if (this.index == null) {
            throw new IllegalStateException("terms index was not loaded when this reader was created");
        }
    }

    final long getPosition(Term term) throws IOException {
        if (this.size == 0L) {
            return -1L;
        }
        this.ensureIndexIsRead();
        BytesRef termBytesRef = new BytesRef(term.text);
        int indexOffset = this.index.getIndexOffset(term, termBytesRef);
        SegmentTermEnum enumerator = this.getThreadResources().termEnum;
        this.index.seekEnum(enumerator, indexOffset);
        while (term.compareTo(enumerator.term()) > 0 && enumerator.next()) {
        }
        if (term.compareTo(enumerator.term()) == 0) {
            return enumerator.position;
        }
        return -1L;
    }

    public SegmentTermEnum terms() {
        return (SegmentTermEnum)this.origEnum.clone();
    }

    public SegmentTermEnum terms(Term term) throws IOException {
        BytesRef termBytesRef = new BytesRef(term.text);
        this.get(term, true, termBytesRef);
        return (SegmentTermEnum)this.getThreadResources().termEnum.clone();
    }

    private static final class ThreadResources {
        SegmentTermEnum termEnum;

        private ThreadResources() {
        }
    }

    private static class CloneableTerm
    extends DoubleBarrelLRUCache.CloneableKey {
        private final Term term;

        public CloneableTerm(Term t) {
            this.term = new Term(t.field(), t.text());
        }

        public Object clone() {
            return new CloneableTerm(this.term);
        }

        public boolean equals(Object _other) {
            CloneableTerm other = (CloneableTerm)_other;
            return this.term.equals(other.term);
        }

        public int hashCode() {
            return this.term.hashCode();
        }
    }

    private static final class TermInfoAndOrd
    extends TermInfo {
        final long termOrd;

        public TermInfoAndOrd(TermInfo ti, long termOrd) {
            super(ti);
            assert (termOrd >= 0L);
            this.termOrd = termOrd;
        }
    }
}

