/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.store.CompoundFileDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.CloseableThreadLocal;
import org.apache.lucene.util.IOUtils;

final class SegmentCoreReaders {
    private final AtomicInteger ref = new AtomicInteger(1);
    final FieldInfos fieldInfos;
    final FieldsProducer fields;
    final DocValuesProducer dvProducer;
    final DocValuesProducer normsProducer;
    final int termsIndexDivisor;
    private final SegmentReader owner;
    final StoredFieldsReader fieldsReaderOrig;
    final TermVectorsReader termVectorsReaderOrig;
    final CompoundFileDirectory cfsReader;
    final CloseableThreadLocal<StoredFieldsReader> fieldsReaderLocal = new CloseableThreadLocal<StoredFieldsReader>(){

        @Override
        protected StoredFieldsReader initialValue() {
            return SegmentCoreReaders.this.fieldsReaderOrig.clone();
        }
    };
    final CloseableThreadLocal<TermVectorsReader> termVectorsLocal = new CloseableThreadLocal<TermVectorsReader>(){

        @Override
        protected TermVectorsReader initialValue() {
            return SegmentCoreReaders.this.termVectorsReaderOrig == null ? null : SegmentCoreReaders.this.termVectorsReaderOrig.clone();
        }
    };
    final CloseableThreadLocal<Map<String, Object>> docValuesLocal = new CloseableThreadLocal<Map<String, Object>>(){

        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };
    final CloseableThreadLocal<Map<String, Object>> normsLocal = new CloseableThreadLocal<Map<String, Object>>(){

        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };
    private final Set<SegmentReader.CoreClosedListener> coreClosedListeners = Collections.synchronizedSet(new LinkedHashSet());

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    SegmentCoreReaders(SegmentReader owner, Directory dir, SegmentInfoPerCommit si, IOContext context, int termsIndexDivisor) throws IOException {
        if (termsIndexDivisor == 0) {
            throw new IllegalArgumentException("indexDivisor must be < 0 (don't load terms index) or greater than 0 (got 0)");
        }
        Codec codec = si.info.getCodec();
        boolean success = false;
        try {
            Directory cfsDir;
            if (si.info.getUseCompoundFile()) {
                this.cfsReader = new CompoundFileDirectory(dir, IndexFileNames.segmentFileName(si.info.name, "", "cfs"), context, false);
                cfsDir = this.cfsReader;
            } else {
                this.cfsReader = null;
                cfsDir = dir;
            }
            this.fieldInfos = codec.fieldInfosFormat().getFieldInfosReader().read(cfsDir, si.info.name, IOContext.READONCE);
            this.termsIndexDivisor = termsIndexDivisor;
            PostingsFormat format = codec.postingsFormat();
            SegmentReadState segmentReadState = new SegmentReadState(cfsDir, si.info, this.fieldInfos, context, termsIndexDivisor);
            this.fields = format.fieldsProducer(segmentReadState);
            assert (this.fields != null);
            if (this.fieldInfos.hasDocValues()) {
                this.dvProducer = codec.docValuesFormat().fieldsProducer(segmentReadState);
                assert (this.dvProducer != null);
            } else {
                this.dvProducer = null;
            }
            if (this.fieldInfos.hasNorms()) {
                this.normsProducer = codec.normsFormat().normsProducer(segmentReadState);
                assert (this.normsProducer != null);
            } else {
                this.normsProducer = null;
            }
            this.fieldsReaderOrig = si.info.getCodec().storedFieldsFormat().fieldsReader(cfsDir, si.info, this.fieldInfos, context);
            this.termVectorsReaderOrig = this.fieldInfos.hasVectors() ? si.info.getCodec().termVectorsFormat().vectorsReader(cfsDir, si.info, this.fieldInfos, context) : null;
            success = true;
        }
        finally {
            if (!success) {
                this.decRef();
            }
        }
        this.owner = owner;
    }

    void incRef() {
        this.ref.incrementAndGet();
    }

    NumericDocValues getNumericDocValues(String field) throws IOException {
        FieldInfo fi = this.fieldInfos.fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() == null) {
            return null;
        }
        if (fi.getDocValuesType() != FieldInfo.DocValuesType.NUMERIC) {
            return null;
        }
        assert (this.dvProducer != null);
        Map<String, Object> dvFields = this.docValuesLocal.get();
        NumericDocValues dvs = (NumericDocValues)dvFields.get(field);
        if (dvs == null) {
            dvs = this.dvProducer.getNumeric(fi);
            dvFields.put(field, dvs);
        }
        return dvs;
    }

    BinaryDocValues getBinaryDocValues(String field) throws IOException {
        FieldInfo fi = this.fieldInfos.fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() == null) {
            return null;
        }
        if (fi.getDocValuesType() != FieldInfo.DocValuesType.BINARY) {
            return null;
        }
        assert (this.dvProducer != null);
        Map<String, Object> dvFields = this.docValuesLocal.get();
        BinaryDocValues dvs = (BinaryDocValues)dvFields.get(field);
        if (dvs == null) {
            dvs = this.dvProducer.getBinary(fi);
            dvFields.put(field, dvs);
        }
        return dvs;
    }

    SortedDocValues getSortedDocValues(String field) throws IOException {
        FieldInfo fi = this.fieldInfos.fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() == null) {
            return null;
        }
        if (fi.getDocValuesType() != FieldInfo.DocValuesType.SORTED) {
            return null;
        }
        assert (this.dvProducer != null);
        Map<String, Object> dvFields = this.docValuesLocal.get();
        SortedDocValues dvs = (SortedDocValues)dvFields.get(field);
        if (dvs == null) {
            dvs = this.dvProducer.getSorted(fi);
            dvFields.put(field, dvs);
        }
        return dvs;
    }

    SortedSetDocValues getSortedSetDocValues(String field) throws IOException {
        FieldInfo fi = this.fieldInfos.fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (fi.getDocValuesType() == null) {
            return null;
        }
        if (fi.getDocValuesType() != FieldInfo.DocValuesType.SORTED_SET) {
            return null;
        }
        assert (this.dvProducer != null);
        Map<String, Object> dvFields = this.docValuesLocal.get();
        SortedSetDocValues dvs = (SortedSetDocValues)dvFields.get(field);
        if (dvs == null) {
            dvs = this.dvProducer.getSortedSet(fi);
            dvFields.put(field, dvs);
        }
        return dvs;
    }

    NumericDocValues getNormValues(String field) throws IOException {
        FieldInfo fi = this.fieldInfos.fieldInfo(field);
        if (fi == null) {
            return null;
        }
        if (!fi.hasNorms()) {
            return null;
        }
        assert (this.normsProducer != null);
        Map<String, Object> normFields = this.normsLocal.get();
        NumericDocValues norms = (NumericDocValues)normFields.get(field);
        if (norms == null) {
            norms = this.normsProducer.getNumeric(fi);
            normFields.put(field, norms);
        }
        return norms;
    }

    void decRef() throws IOException {
        if (this.ref.decrementAndGet() == 0) {
            IOUtils.close(this.termVectorsLocal, this.fieldsReaderLocal, this.docValuesLocal, this.normsLocal, this.fields, this.dvProducer, this.termVectorsReaderOrig, this.fieldsReaderOrig, this.cfsReader, this.normsProducer);
            this.notifyCoreClosedListeners();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyCoreClosedListeners() {
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

    public String toString() {
        return "SegmentCoreReader(owner=" + this.owner + ")";
    }
}

