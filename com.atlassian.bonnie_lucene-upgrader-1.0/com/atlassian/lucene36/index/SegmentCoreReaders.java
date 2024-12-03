/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CompoundFileReader;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.FieldsReader;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.index.TermInfosReader;
import com.atlassian.lucene36.index.TermVectorsReader;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.util.IOUtils;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

final class SegmentCoreReaders {
    private final AtomicInteger ref = new AtomicInteger(1);
    final String segment;
    final FieldInfos fieldInfos;
    final IndexInput freqStream;
    final IndexInput proxStream;
    final TermInfosReader tisNoIndex;
    final Directory dir;
    final Directory cfsDir;
    final int readBufferSize;
    final int termsIndexDivisor;
    private final SegmentReader owner;
    volatile TermInfosReader tis;
    FieldsReader fieldsReaderOrig;
    TermVectorsReader termVectorsReaderOrig;
    CompoundFileReader cfsReader;
    CompoundFileReader storeCFSReader;
    private final Set<SegmentReader.CoreClosedListener> coreClosedListeners = Collections.synchronizedSet(new LinkedHashSet());

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    SegmentCoreReaders(SegmentReader owner, Directory dir, SegmentInfo si, int readBufferSize, int termsIndexDivisor) throws IOException {
        this.segment = si.name;
        this.readBufferSize = readBufferSize;
        this.dir = dir;
        boolean success = false;
        try {
            Directory dir0 = dir;
            if (si.getUseCompoundFile()) {
                this.cfsReader = new CompoundFileReader(dir, IndexFileNames.segmentFileName(this.segment, "cfs"), readBufferSize);
                dir0 = this.cfsReader;
            }
            this.cfsDir = dir0;
            this.fieldInfos = new FieldInfos(this.cfsDir, IndexFileNames.segmentFileName(this.segment, "fnm"));
            this.termsIndexDivisor = termsIndexDivisor;
            TermInfosReader reader = new TermInfosReader(this.cfsDir, this.segment, this.fieldInfos, readBufferSize, termsIndexDivisor);
            if (termsIndexDivisor == -1) {
                this.tisNoIndex = reader;
            } else {
                this.tis = reader;
                this.tisNoIndex = null;
            }
            this.freqStream = this.cfsDir.openInput(IndexFileNames.segmentFileName(this.segment, "frq"), readBufferSize);
            this.proxStream = this.fieldInfos.hasProx() ? this.cfsDir.openInput(IndexFileNames.segmentFileName(this.segment, "prx"), readBufferSize) : null;
            success = true;
        }
        finally {
            if (!success) {
                this.decRef();
            }
        }
        this.owner = owner;
    }

    synchronized TermVectorsReader getTermVectorsReaderOrig() {
        return this.termVectorsReaderOrig;
    }

    synchronized FieldsReader getFieldsReaderOrig() {
        return this.fieldsReaderOrig;
    }

    synchronized void incRef() {
        this.ref.incrementAndGet();
    }

    synchronized Directory getCFSReader() {
        return this.cfsReader;
    }

    TermInfosReader getTermsReader() {
        TermInfosReader tis = this.tis;
        if (tis != null) {
            return tis;
        }
        return this.tisNoIndex;
    }

    boolean termsIndexIsLoaded() {
        return this.tis != null;
    }

    synchronized void loadTermsIndex(SegmentInfo si, int termsIndexDivisor) throws IOException {
        if (this.tis == null) {
            Directory dir0;
            if (si.getUseCompoundFile()) {
                if (this.cfsReader == null) {
                    this.cfsReader = new CompoundFileReader(this.dir, IndexFileNames.segmentFileName(this.segment, "cfs"), this.readBufferSize);
                }
                dir0 = this.cfsReader;
            } else {
                dir0 = this.dir;
            }
            this.tis = new TermInfosReader(dir0, this.segment, this.fieldInfos, this.readBufferSize, termsIndexDivisor);
        }
    }

    synchronized void decRef() throws IOException {
        if (this.ref.decrementAndGet() == 0) {
            IOUtils.close(this.tis, this.tisNoIndex, this.freqStream, this.proxStream, this.termVectorsReaderOrig, this.fieldsReaderOrig, this.cfsReader, this.storeCFSReader);
            this.tis = null;
            this.notifyCoreClosedListeners();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void notifyCoreClosedListeners() {
        Set<SegmentReader.CoreClosedListener> set = this.coreClosedListeners;
        synchronized (set) {
            for (SegmentReader.CoreClosedListener listener : this.coreClosedListeners) {
                listener.onClose(this.owner);
            }
        }
    }

    void addCoreClosedListener(SegmentReader.CoreClosedListener listener) {
        this.coreClosedListeners.add(listener);
    }

    void removeCoreClosedListener(SegmentReader.CoreClosedListener listener) {
        this.coreClosedListeners.remove(listener);
    }

    synchronized void openDocStores(SegmentInfo si) throws IOException {
        assert (si.name.equals(this.segment));
        if (this.fieldsReaderOrig == null) {
            Directory storeDir;
            if (si.getDocStoreOffset() != -1) {
                if (si.getDocStoreIsCompoundFile()) {
                    assert (this.storeCFSReader == null);
                    this.storeCFSReader = new CompoundFileReader(this.dir, IndexFileNames.segmentFileName(si.getDocStoreSegment(), "cfx"), this.readBufferSize);
                    storeDir = this.storeCFSReader;
                    assert (storeDir != null);
                } else {
                    storeDir = this.dir;
                    assert (storeDir != null);
                }
            } else if (si.getUseCompoundFile()) {
                if (this.cfsReader == null) {
                    this.cfsReader = new CompoundFileReader(this.dir, IndexFileNames.segmentFileName(this.segment, "cfs"), this.readBufferSize);
                }
                storeDir = this.cfsReader;
                assert (storeDir != null);
            } else {
                storeDir = this.dir;
                assert (storeDir != null);
            }
            String storesSegment = si.getDocStoreOffset() != -1 ? si.getDocStoreSegment() : this.segment;
            this.fieldsReaderOrig = new FieldsReader(storeDir, storesSegment, this.fieldInfos, this.readBufferSize, si.getDocStoreOffset(), si.docCount);
            if (si.getDocStoreOffset() == -1 && this.fieldsReaderOrig.size() != si.docCount) {
                throw new CorruptIndexException("doc counts differ for segment " + this.segment + ": fieldsReader shows " + this.fieldsReaderOrig.size() + " but segmentInfo shows " + si.docCount);
            }
            if (si.getHasVectors()) {
                this.termVectorsReaderOrig = new TermVectorsReader(storeDir, storesSegment, this.fieldInfos, this.readBufferSize, si.getDocStoreOffset(), si.docCount);
            }
        }
    }

    public String toString() {
        return "SegmentCoreReader(owner=" + this.owner + ")";
    }
}

