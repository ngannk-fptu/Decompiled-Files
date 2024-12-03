/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.FieldInfosWriter;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.codecs.StoredFieldsWriter;
import org.apache.lucene.codecs.TermVectorsWriter;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.ReaderSlice;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.InfoStream;

final class SegmentMerger {
    private final Directory directory;
    private final int termIndexInterval;
    private final Codec codec;
    private final IOContext context;
    private final MergeState mergeState;
    private final FieldInfos.Builder fieldInfosBuilder;

    SegmentMerger(List<AtomicReader> readers, SegmentInfo segmentInfo, InfoStream infoStream, Directory dir, int termIndexInterval, MergeState.CheckAbort checkAbort, FieldInfos.FieldNumbers fieldNumbers, IOContext context) {
        this.mergeState = new MergeState(readers, segmentInfo, infoStream, checkAbort);
        this.directory = dir;
        this.termIndexInterval = termIndexInterval;
        this.codec = segmentInfo.getCodec();
        this.context = context;
        this.fieldInfosBuilder = new FieldInfos.Builder(fieldNumbers);
    }

    MergeState merge() throws IOException {
        long t1;
        this.mergeState.segmentInfo.setDocCount(this.setDocMaps());
        this.mergeFieldInfos();
        this.setMatchingSegmentReaders();
        long t0 = 0L;
        if (this.mergeState.infoStream.isEnabled("SM")) {
            t0 = System.nanoTime();
        }
        int numMerged = this.mergeFields();
        if (this.mergeState.infoStream.isEnabled("SM")) {
            long t12 = System.nanoTime();
            this.mergeState.infoStream.message("SM", (t12 - t0) / 1000000L + " msec to merge stored fields [" + numMerged + " docs]");
        }
        assert (numMerged == this.mergeState.segmentInfo.getDocCount());
        SegmentWriteState segmentWriteState = new SegmentWriteState(this.mergeState.infoStream, this.directory, this.mergeState.segmentInfo, this.mergeState.fieldInfos, this.termIndexInterval, null, this.context);
        if (this.mergeState.infoStream.isEnabled("SM")) {
            t0 = System.nanoTime();
        }
        this.mergeTerms(segmentWriteState);
        if (this.mergeState.infoStream.isEnabled("SM")) {
            t1 = System.nanoTime();
            this.mergeState.infoStream.message("SM", (t1 - t0) / 1000000L + " msec to merge postings [" + numMerged + " docs]");
        }
        if (this.mergeState.infoStream.isEnabled("SM")) {
            t0 = System.nanoTime();
        }
        if (this.mergeState.fieldInfos.hasDocValues()) {
            this.mergeDocValues(segmentWriteState);
        }
        if (this.mergeState.infoStream.isEnabled("SM")) {
            t1 = System.nanoTime();
            this.mergeState.infoStream.message("SM", (t1 - t0) / 1000000L + " msec to merge doc values [" + numMerged + " docs]");
        }
        if (this.mergeState.fieldInfos.hasNorms()) {
            if (this.mergeState.infoStream.isEnabled("SM")) {
                t0 = System.nanoTime();
            }
            this.mergeNorms(segmentWriteState);
            if (this.mergeState.infoStream.isEnabled("SM")) {
                t1 = System.nanoTime();
                this.mergeState.infoStream.message("SM", (t1 - t0) / 1000000L + " msec to merge norms [" + numMerged + " docs]");
            }
        }
        if (this.mergeState.fieldInfos.hasVectors()) {
            if (this.mergeState.infoStream.isEnabled("SM")) {
                t0 = System.nanoTime();
            }
            numMerged = this.mergeVectors();
            if (this.mergeState.infoStream.isEnabled("SM")) {
                t1 = System.nanoTime();
                this.mergeState.infoStream.message("SM", (t1 - t0) / 1000000L + " msec to merge vectors [" + numMerged + " docs]");
            }
            assert (numMerged == this.mergeState.segmentInfo.getDocCount());
        }
        FieldInfosWriter fieldInfosWriter = this.codec.fieldInfosFormat().getFieldInfosWriter();
        fieldInfosWriter.write(this.directory, this.mergeState.segmentInfo.name, this.mergeState.fieldInfos, this.context);
        return this.mergeState;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void mergeDocValues(SegmentWriteState segmentWriteState) throws IOException {
        block18: {
            DocValuesConsumer consumer;
            block17: {
                consumer = this.codec.docValuesFormat().fieldsConsumer(segmentWriteState);
                boolean success = false;
                try {
                    for (FieldInfo field : this.mergeState.fieldInfos) {
                        Object values;
                        ArrayList<NumericDocValues> toMerge;
                        FieldInfo.DocValuesType type = field.getDocValuesType();
                        if (type == null) continue;
                        if (type == FieldInfo.DocValuesType.NUMERIC) {
                            toMerge = new ArrayList<NumericDocValues>();
                            for (AtomicReader reader : this.mergeState.readers) {
                                values = reader.getNumericDocValues(field.name);
                                if (values == null) {
                                    values = NumericDocValues.EMPTY;
                                }
                                toMerge.add((NumericDocValues)values);
                            }
                            consumer.mergeNumericField(field, this.mergeState, toMerge);
                            continue;
                        }
                        if (type == FieldInfo.DocValuesType.BINARY) {
                            toMerge = new ArrayList();
                            for (AtomicReader reader : this.mergeState.readers) {
                                values = reader.getBinaryDocValues(field.name);
                                if (values == null) {
                                    values = BinaryDocValues.EMPTY;
                                }
                                toMerge.add((NumericDocValues)values);
                            }
                            consumer.mergeBinaryField(field, this.mergeState, toMerge);
                            continue;
                        }
                        if (type == FieldInfo.DocValuesType.SORTED) {
                            toMerge = new ArrayList();
                            for (AtomicReader reader : this.mergeState.readers) {
                                values = reader.getSortedDocValues(field.name);
                                if (values == null) {
                                    values = SortedDocValues.EMPTY;
                                }
                                toMerge.add((NumericDocValues)values);
                            }
                            consumer.mergeSortedField(field, this.mergeState, toMerge);
                            continue;
                        }
                        if (type == FieldInfo.DocValuesType.SORTED_SET) {
                            toMerge = new ArrayList();
                            for (AtomicReader reader : this.mergeState.readers) {
                                values = reader.getSortedSetDocValues(field.name);
                                if (values == null) {
                                    values = SortedSetDocValues.EMPTY;
                                }
                                toMerge.add((NumericDocValues)values);
                            }
                            consumer.mergeSortedSetField(field, this.mergeState, toMerge);
                            continue;
                        }
                        throw new AssertionError((Object)("type=" + (Object)((Object)type)));
                    }
                    success = true;
                    if (!success) break block17;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(consumer);
                    } else {
                        IOUtils.closeWhileHandlingException(consumer);
                    }
                    throw throwable;
                }
                IOUtils.close(consumer);
                break block18;
            }
            IOUtils.closeWhileHandlingException(consumer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void mergeNorms(SegmentWriteState segmentWriteState) throws IOException {
        block8: {
            DocValuesConsumer consumer;
            block7: {
                consumer = this.codec.normsFormat().normsConsumer(segmentWriteState);
                boolean success = false;
                try {
                    for (FieldInfo field : this.mergeState.fieldInfos) {
                        if (!field.hasNorms()) continue;
                        ArrayList<NumericDocValues> toMerge = new ArrayList<NumericDocValues>();
                        for (AtomicReader reader : this.mergeState.readers) {
                            NumericDocValues norms = reader.getNormValues(field.name);
                            if (norms == null) {
                                norms = NumericDocValues.EMPTY;
                            }
                            toMerge.add(norms);
                        }
                        consumer.mergeNumericField(field, this.mergeState, toMerge);
                    }
                    success = true;
                    if (!success) break block7;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(consumer);
                    } else {
                        IOUtils.closeWhileHandlingException(consumer);
                    }
                    throw throwable;
                }
                IOUtils.close(consumer);
                break block8;
            }
            IOUtils.closeWhileHandlingException(consumer);
        }
    }

    private void setMatchingSegmentReaders() {
        int numReaders = this.mergeState.readers.size();
        this.mergeState.matchingSegmentReaders = new SegmentReader[numReaders];
        for (int i = 0; i < numReaders; ++i) {
            AtomicReader reader = this.mergeState.readers.get(i);
            if (!(reader instanceof SegmentReader)) continue;
            SegmentReader segmentReader = (SegmentReader)reader;
            boolean same = true;
            FieldInfos segmentFieldInfos = segmentReader.getFieldInfos();
            for (FieldInfo fi : segmentFieldInfos) {
                FieldInfo other = this.mergeState.fieldInfos.fieldInfo(fi.number);
                if (other != null && other.name.equals(fi.name)) continue;
                same = false;
                break;
            }
            if (!same) continue;
            this.mergeState.matchingSegmentReaders[i] = segmentReader;
            ++this.mergeState.matchedCount;
        }
        if (this.mergeState.infoStream.isEnabled("SM")) {
            this.mergeState.infoStream.message("SM", "merge store matchedCount=" + this.mergeState.matchedCount + " vs " + this.mergeState.readers.size());
            if (this.mergeState.matchedCount != this.mergeState.readers.size()) {
                this.mergeState.infoStream.message("SM", "" + (this.mergeState.readers.size() - this.mergeState.matchedCount) + " non-bulk merges");
            }
        }
    }

    public void mergeFieldInfos() throws IOException {
        for (AtomicReader reader : this.mergeState.readers) {
            FieldInfos readerFieldInfos = reader.getFieldInfos();
            for (FieldInfo fi : readerFieldInfos) {
                this.fieldInfosBuilder.add(fi);
            }
        }
        this.mergeState.fieldInfos = this.fieldInfosBuilder.finish();
    }

    private int mergeFields() throws IOException {
        try (StoredFieldsWriter fieldsWriter = this.codec.storedFieldsFormat().fieldsWriter(this.directory, this.mergeState.segmentInfo, this.context);){
            int n = fieldsWriter.merge(this.mergeState);
            return n;
        }
    }

    private int mergeVectors() throws IOException {
        try (TermVectorsWriter termVectorsWriter = this.codec.termVectorsFormat().vectorsWriter(this.directory, this.mergeState.segmentInfo, this.context);){
            int n = termVectorsWriter.merge(this.mergeState);
            return n;
        }
    }

    private int setDocMaps() throws IOException {
        int numReaders = this.mergeState.readers.size();
        this.mergeState.docMaps = new MergeState.DocMap[numReaders];
        this.mergeState.docBase = new int[numReaders];
        int docBase = 0;
        for (int i = 0; i < this.mergeState.readers.size(); ++i) {
            MergeState.DocMap docMap;
            AtomicReader reader = this.mergeState.readers.get(i);
            this.mergeState.docBase[i] = docBase;
            this.mergeState.docMaps[i] = docMap = MergeState.DocMap.build(reader);
            docBase += docMap.numDocs();
        }
        return docBase;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void mergeTerms(SegmentWriteState segmentWriteState) throws IOException {
        block7: {
            FieldsConsumer consumer;
            block6: {
                ArrayList<Fields> fields = new ArrayList<Fields>();
                ArrayList<ReaderSlice> slices = new ArrayList<ReaderSlice>();
                int docBase = 0;
                for (int readerIndex = 0; readerIndex < this.mergeState.readers.size(); ++readerIndex) {
                    AtomicReader reader = this.mergeState.readers.get(readerIndex);
                    Fields f = reader.fields();
                    int maxDoc = reader.maxDoc();
                    if (f != null) {
                        slices.add(new ReaderSlice(docBase, maxDoc, readerIndex));
                        fields.add(f);
                    }
                    docBase += maxDoc;
                }
                consumer = this.codec.postingsFormat().fieldsConsumer(segmentWriteState);
                boolean success = false;
                try {
                    consumer.merge(this.mergeState, new MultiFields(fields.toArray(Fields.EMPTY_ARRAY), slices.toArray(ReaderSlice.EMPTY_ARRAY)));
                    success = true;
                    if (!success) break block6;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(consumer);
                    } else {
                        IOUtils.closeWhileHandlingException(consumer);
                    }
                    throw throwable;
                }
                IOUtils.close(consumer);
                break block7;
            }
            IOUtils.closeWhileHandlingException(consumer);
        }
    }
}

