/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.Closeable;
import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.codecs.lucene3x.SegmentTermEnum;
import org.apache.lucene.codecs.lucene3x.TermInfo;
import org.apache.lucene.codecs.lucene3x.TermInfosReaderIndex;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CloseableThreadLocal;
import org.apache.lucene.util.DoubleBarrelLRUCache;
import org.apache.lucene.util.IOUtils;

@Deprecated
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
    private static final Comparator<BytesRef> legacyComparator = BytesRef.getUTF8SortedAsUTF16Comparator();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    TermInfosReader(Directory dir, String seg, FieldInfos fis, IOContext context, int indexDivisor) throws CorruptIndexException, IOException {
        boolean success = false;
        if (indexDivisor < 1 && indexDivisor != -1) {
            throw new IllegalArgumentException("indexDivisor must be -1 (don't load terms index) or greater than 0: got " + indexDivisor);
        }
        try {
            this.directory = dir;
            this.segment = seg;
            this.fieldInfos = fis;
            this.origEnum = new SegmentTermEnum(this.directory.openInput(IndexFileNames.segmentFileName(this.segment, "", "tis"), context), this.fieldInfos, false);
            this.size = this.origEnum.size;
            if (indexDivisor != -1) {
                this.totalIndexInterval = this.origEnum.indexInterval * indexDivisor;
                String indexFileName = IndexFileNames.segmentFileName(this.segment, "", "tii");
                try (SegmentTermEnum indexEnum = new SegmentTermEnum(this.directory.openInput(indexFileName, context), this.fieldInfos, true);){
                    this.index = new TermInfosReaderIndex(indexEnum, indexDivisor, dir.fileLength(indexFileName), this.totalIndexInterval);
                    this.indexLength = this.index.length();
                }
            } else {
                this.totalIndexInterval = -1;
                this.index = null;
                this.indexLength = -1;
            }
            success = true;
        }
        finally {
            if (!success) {
                this.close();
            }
        }
    }

    public int getSkipInterval() {
        return this.origEnum.skipInterval;
    }

    public int getMaxSkipLevels() {
        return this.origEnum.maxSkipLevels;
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(this.origEnum, this.threadResources);
    }

    long size() {
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

    private final int compareAsUTF16(Term term1, Term term2) {
        if (term1.field().equals(term2.field())) {
            return legacyComparator.compare(term1.bytes(), term2.bytes());
        }
        return term1.field().compareTo(term2.field());
    }

    TermInfo get(Term term) throws IOException {
        return this.get(term, false);
    }

    private TermInfo get(Term term, boolean mustSeekEnum) throws IOException {
        if (this.size == 0L) {
            return null;
        }
        this.ensureIndexIsRead();
        TermInfoAndOrd tiOrd = this.termsCache.get(new CloneableTerm(term));
        ThreadResources resources = this.getThreadResources();
        if (!mustSeekEnum && tiOrd != null) {
            return tiOrd;
        }
        return this.seekEnum(resources.termEnum, term, tiOrd, true);
    }

    public void cacheCurrentTerm(SegmentTermEnum enumerator) {
        this.termsCache.put(new CloneableTerm(enumerator.term()), new TermInfoAndOrd(enumerator.termInfo, enumerator.position));
    }

    static Term deepCopyOf(Term other) {
        return new Term(other.field(), BytesRef.deepCopyOf(other.bytes()));
    }

    TermInfo seekEnum(SegmentTermEnum enumerator, Term term, boolean useCache) throws IOException {
        if (useCache) {
            return this.seekEnum(enumerator, term, this.termsCache.get(new CloneableTerm(TermInfosReader.deepCopyOf(term))), useCache);
        }
        return this.seekEnum(enumerator, term, null, useCache);
    }

    TermInfo seekEnum(SegmentTermEnum enumerator, Term term, TermInfoAndOrd tiOrd, boolean useCache) throws IOException {
        TermInfo ti;
        int enumOffset;
        if (this.size == 0L) {
            return null;
        }
        if (enumerator.term() != null && (enumerator.prev() != null && this.compareAsUTF16(term, enumerator.prev()) > 0 || this.compareAsUTF16(term, enumerator.term()) >= 0) && (this.indexLength == (enumOffset = (int)(enumerator.position / (long)this.totalIndexInterval) + 1) || this.index.compareTo(term, enumOffset) < 0)) {
            TermInfo ti2;
            int numScans = enumerator.scanTo(term);
            if (enumerator.term() != null && this.compareAsUTF16(term, enumerator.term()) == 0) {
                ti2 = enumerator.termInfo;
                if (numScans > 1) {
                    if (tiOrd == null) {
                        if (useCache) {
                            this.termsCache.put(new CloneableTerm(TermInfosReader.deepCopyOf(term)), new TermInfoAndOrd(ti2, enumerator.position));
                        }
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
        int indexPos = tiOrd != null ? (int)(tiOrd.termOrd / (long)this.totalIndexInterval) : this.index.getIndexOffset(term);
        this.index.seekEnum(enumerator, indexPos);
        enumerator.scanTo(term);
        if (enumerator.term() != null && this.compareAsUTF16(term, enumerator.term()) == 0) {
            ti = enumerator.termInfo;
            if (tiOrd == null) {
                if (useCache) {
                    this.termsCache.put(new CloneableTerm(TermInfosReader.deepCopyOf(term)), new TermInfoAndOrd(ti, enumerator.position));
                }
            } else {
                assert (this.sameTermInfo(ti, tiOrd, enumerator));
                assert (enumerator.position == tiOrd.termOrd);
            }
        } else {
            ti = null;
        }
        return ti;
    }

    private boolean sameTermInfo(TermInfo ti1, TermInfo ti2, SegmentTermEnum enumerator) {
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

    long getPosition(Term term) throws IOException {
        if (this.size == 0L) {
            return -1L;
        }
        this.ensureIndexIsRead();
        int indexOffset = this.index.getIndexOffset(term);
        SegmentTermEnum enumerator = this.getThreadResources().termEnum;
        this.index.seekEnum(enumerator, indexOffset);
        while (this.compareAsUTF16(term, enumerator.term()) > 0 && enumerator.next()) {
        }
        if (this.compareAsUTF16(term, enumerator.term()) == 0) {
            return enumerator.position;
        }
        return -1L;
    }

    public SegmentTermEnum terms() {
        return this.origEnum.clone();
    }

    public SegmentTermEnum terms(Term term) throws IOException {
        this.get(term, true);
        return this.getThreadResources().termEnum.clone();
    }

    private static final class ThreadResources {
        SegmentTermEnum termEnum;

        private ThreadResources() {
        }
    }

    private static class CloneableTerm
    extends DoubleBarrelLRUCache.CloneableKey {
        Term term;

        public CloneableTerm(Term t) {
            this.term = t;
        }

        public boolean equals(Object other) {
            CloneableTerm t = (CloneableTerm)other;
            return this.term.equals(t.term);
        }

        public int hashCode() {
            return this.term.hashCode();
        }

        @Override
        public CloneableTerm clone() {
            return new CloneableTerm(this.term);
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

