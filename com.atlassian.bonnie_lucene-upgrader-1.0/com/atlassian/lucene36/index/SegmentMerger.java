/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.index.CompoundFileWriter;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.FieldsReader;
import com.atlassian.lucene36.index.FieldsWriter;
import com.atlassian.lucene36.index.FormatPostingsDocsConsumer;
import com.atlassian.lucene36.index.FormatPostingsFieldsConsumer;
import com.atlassian.lucene36.index.FormatPostingsFieldsWriter;
import com.atlassian.lucene36.index.FormatPostingsPositionsConsumer;
import com.atlassian.lucene36.index.FormatPostingsTermsConsumer;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.PayloadProcessorProvider;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentMergeInfo;
import com.atlassian.lucene36.index.SegmentMergeQueue;
import com.atlassian.lucene36.index.SegmentNorms;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermEnum;
import com.atlassian.lucene36.index.TermFreqVector;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.index.TermVectorsReader;
import com.atlassian.lucene36.index.TermVectorsWriter;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.ReaderUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class SegmentMerger {
    private Directory directory;
    private String segment;
    private int termIndexInterval = 128;
    private List<IndexReader> readers = new ArrayList<IndexReader>();
    private final FieldInfos fieldInfos;
    private int mergedDocs;
    private final CheckAbort checkAbort;
    private static final int MAX_RAW_MERGE_DOCS = 4192;
    private SegmentWriteState segmentWriteState;
    private final PayloadProcessorProvider payloadProcessorProvider;
    private SegmentReader[] matchingSegmentReaders;
    private int[] rawDocLengths;
    private int[] rawDocLengths2;
    private int matchedCount;
    private SegmentMergeQueue queue = null;
    FieldInfo.IndexOptions indexOptions;
    private byte[] payloadBuffer;
    private int[][] docMaps;

    SegmentMerger(Directory dir, int termIndexInterval, String name, MergePolicy.OneMerge merge, PayloadProcessorProvider payloadProcessorProvider, FieldInfos fieldInfos) {
        this.payloadProcessorProvider = payloadProcessorProvider;
        this.directory = dir;
        this.fieldInfos = fieldInfos;
        this.segment = name;
        this.checkAbort = merge != null ? new CheckAbort(merge, this.directory) : new CheckAbort(null, null){

            public void work(double units) throws MergePolicy.MergeAbortedException {
            }
        };
        this.termIndexInterval = termIndexInterval;
    }

    public FieldInfos fieldInfos() {
        return this.fieldInfos;
    }

    final void add(IndexReader reader) {
        ReaderUtil.gatherSubReaders(this.readers, reader);
    }

    final int merge() throws CorruptIndexException, IOException {
        this.mergedDocs = this.mergeFields();
        this.mergeTerms();
        this.mergeNorms();
        if (this.fieldInfos.hasVectors()) {
            this.mergeVectors();
        }
        return this.mergedDocs;
    }

    final Collection<String> createCompoundFile(String fileName, SegmentInfo info) throws IOException {
        List<String> files = info.files();
        CompoundFileWriter cfsWriter = new CompoundFileWriter(this.directory, fileName, this.checkAbort);
        for (String file : files) {
            assert (!IndexFileNames.matchesExtension(file, "del")) : ".del file is not allowed in .cfs: " + file;
            assert (!IndexFileNames.isSeparateNormsFile(file)) : "separate norms file (.s[0-9]+) is not allowed in .cfs: " + file;
            cfsWriter.addFile(file);
        }
        cfsWriter.close();
        return files;
    }

    public int getMatchedSubReaderCount() {
        return this.matchedCount;
    }

    private void setMatchingSegmentReaders() {
        int numReaders = this.readers.size();
        this.matchingSegmentReaders = new SegmentReader[numReaders];
        for (int i = 0; i < numReaders; ++i) {
            IndexReader reader = this.readers.get(i);
            if (!(reader instanceof SegmentReader)) continue;
            SegmentReader segmentReader = (SegmentReader)reader;
            boolean same = true;
            FieldInfos segmentFieldInfos = segmentReader.getFieldInfos();
            int numFieldInfos = segmentFieldInfos.size();
            for (int j = 0; j < numFieldInfos; ++j) {
                if (this.fieldInfos.fieldName(j).equals(segmentFieldInfos.fieldName(j))) continue;
                same = false;
                break;
            }
            if (!same) continue;
            this.matchingSegmentReaders[i] = segmentReader;
            ++this.matchedCount;
        }
        this.rawDocLengths = new int[4192];
        this.rawDocLengths2 = new int[4192];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int mergeFields() throws CorruptIndexException, IOException {
        for (IndexReader reader : this.readers) {
            this.fieldInfos.add(reader.getFieldInfos());
        }
        this.fieldInfos.write(this.directory, this.segment + ".fnm");
        int docCount = 0;
        this.setMatchingSegmentReaders();
        FieldsWriter fieldsWriter = new FieldsWriter(this.directory, this.segment, this.fieldInfos);
        try {
            int idx = 0;
            for (IndexReader reader : this.readers) {
                FieldsReader fieldsReader;
                SegmentReader matchingSegmentReader = this.matchingSegmentReaders[idx++];
                FieldsReader matchingFieldsReader = null;
                if (matchingSegmentReader != null && (fieldsReader = matchingSegmentReader.getFieldsReader()) != null && fieldsReader.canReadRawDocs()) {
                    matchingFieldsReader = fieldsReader;
                }
                if (reader.hasDeletions()) {
                    docCount += this.copyFieldsWithDeletions(fieldsWriter, reader, matchingFieldsReader);
                    continue;
                }
                docCount += this.copyFieldsNoDeletions(fieldsWriter, reader, matchingFieldsReader);
            }
            fieldsWriter.finish(docCount);
            Object var10_10 = null;
        }
        catch (Throwable throwable) {
            Object var10_11 = null;
            fieldsWriter.close();
            throw throwable;
        }
        fieldsWriter.close();
        this.segmentWriteState = new SegmentWriteState(null, this.directory, this.segment, this.fieldInfos, docCount, this.termIndexInterval, null);
        return docCount;
    }

    private int copyFieldsWithDeletions(FieldsWriter fieldsWriter, IndexReader reader, FieldsReader matchingFieldsReader) throws IOException, MergePolicy.MergeAbortedException, CorruptIndexException {
        int docCount = 0;
        int maxDoc = reader.maxDoc();
        if (matchingFieldsReader != null) {
            int j = 0;
            while (j < maxDoc) {
                if (reader.isDeleted(j)) {
                    ++j;
                    continue;
                }
                int start = j;
                int numDocs = 0;
                do {
                    ++numDocs;
                    if (++j >= maxDoc) break;
                    if (!reader.isDeleted(j)) continue;
                    ++j;
                    break;
                } while (numDocs < 4192);
                IndexInput stream = matchingFieldsReader.rawDocs(this.rawDocLengths, start, numDocs);
                fieldsWriter.addRawDocuments(stream, this.rawDocLengths, numDocs);
                docCount += numDocs;
                this.checkAbort.work(300 * numDocs);
            }
        } else {
            for (int j = 0; j < maxDoc; ++j) {
                if (reader.isDeleted(j)) continue;
                Document doc = reader.document(j);
                fieldsWriter.addDocument(doc);
                ++docCount;
                this.checkAbort.work(300.0);
            }
        }
        return docCount;
    }

    private int copyFieldsNoDeletions(FieldsWriter fieldsWriter, IndexReader reader, FieldsReader matchingFieldsReader) throws IOException, MergePolicy.MergeAbortedException, CorruptIndexException {
        int docCount;
        int maxDoc = reader.maxDoc();
        if (matchingFieldsReader != null) {
            int len;
            for (docCount = 0; docCount < maxDoc; docCount += len) {
                len = Math.min(4192, maxDoc - docCount);
                IndexInput stream = matchingFieldsReader.rawDocs(this.rawDocLengths, docCount, len);
                fieldsWriter.addRawDocuments(stream, this.rawDocLengths, len);
                this.checkAbort.work(300 * len);
            }
        } else {
            while (docCount < maxDoc) {
                Document doc = reader.document(docCount);
                fieldsWriter.addDocument(doc);
                this.checkAbort.work(300.0);
                ++docCount;
            }
        }
        return docCount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void mergeVectors() throws IOException {
        TermVectorsWriter termVectorsWriter = new TermVectorsWriter(this.directory, this.segment, this.fieldInfos);
        try {
            int idx = 0;
            for (IndexReader reader : this.readers) {
                TermVectorsReader vectorsReader;
                SegmentReader matchingSegmentReader = this.matchingSegmentReaders[idx++];
                TermVectorsReader matchingVectorsReader = null;
                if (matchingSegmentReader != null && (vectorsReader = matchingSegmentReader.getTermVectorsReader()) != null && vectorsReader.canReadRawDocs()) {
                    matchingVectorsReader = vectorsReader;
                }
                if (reader.hasDeletions()) {
                    this.copyVectorsWithDeletions(termVectorsWriter, matchingVectorsReader, reader);
                    continue;
                }
                this.copyVectorsNoDeletions(termVectorsWriter, matchingVectorsReader, reader);
            }
            termVectorsWriter.finish(this.mergedDocs);
            Object var9_8 = null;
        }
        catch (Throwable throwable) {
            Object var9_9 = null;
            termVectorsWriter.close();
            throw throwable;
        }
        termVectorsWriter.close();
    }

    private void copyVectorsWithDeletions(TermVectorsWriter termVectorsWriter, TermVectorsReader matchingVectorsReader, IndexReader reader) throws IOException, MergePolicy.MergeAbortedException {
        int maxDoc = reader.maxDoc();
        if (matchingVectorsReader != null) {
            int docNum = 0;
            while (docNum < maxDoc) {
                if (reader.isDeleted(docNum)) {
                    ++docNum;
                    continue;
                }
                int start = docNum;
                int numDocs = 0;
                do {
                    ++numDocs;
                    if (++docNum >= maxDoc) break;
                    if (!reader.isDeleted(docNum)) continue;
                    ++docNum;
                    break;
                } while (numDocs < 4192);
                matchingVectorsReader.rawDocs(this.rawDocLengths, this.rawDocLengths2, start, numDocs);
                termVectorsWriter.addRawDocuments(matchingVectorsReader, this.rawDocLengths, this.rawDocLengths2, numDocs);
                this.checkAbort.work(300 * numDocs);
            }
        } else {
            for (int docNum = 0; docNum < maxDoc; ++docNum) {
                if (reader.isDeleted(docNum)) continue;
                TermFreqVector[] vectors = reader.getTermFreqVectors(docNum);
                termVectorsWriter.addAllDocVectors(vectors);
                this.checkAbort.work(300.0);
            }
        }
    }

    private void copyVectorsNoDeletions(TermVectorsWriter termVectorsWriter, TermVectorsReader matchingVectorsReader, IndexReader reader) throws IOException, MergePolicy.MergeAbortedException {
        int maxDoc = reader.maxDoc();
        if (matchingVectorsReader != null) {
            int len;
            for (int docCount = 0; docCount < maxDoc; docCount += len) {
                len = Math.min(4192, maxDoc - docCount);
                matchingVectorsReader.rawDocs(this.rawDocLengths, this.rawDocLengths2, docCount, len);
                termVectorsWriter.addRawDocuments(matchingVectorsReader, this.rawDocLengths, this.rawDocLengths2, len);
                this.checkAbort.work(300 * len);
            }
        } else {
            for (int docNum = 0; docNum < maxDoc; ++docNum) {
                TermFreqVector[] vectors = reader.getTermFreqVectors(docNum);
                termVectorsWriter.addAllDocVectors(vectors);
                this.checkAbort.work(300.0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final void mergeTerms() throws CorruptIndexException, IOException {
        FormatPostingsFieldsWriter fieldsConsumer = new FormatPostingsFieldsWriter(this.segmentWriteState, this.fieldInfos);
        try {
            this.queue = new SegmentMergeQueue(this.readers.size());
            this.mergeTermInfos(fieldsConsumer);
            Object var3_2 = null;
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            try {
                ((FormatPostingsFieldsConsumer)fieldsConsumer).finish();
                Object var5_6 = null;
                if (this.queue == null) throw throwable;
            }
            catch (Throwable throwable2) {
                Object var5_7 = null;
                if (this.queue == null) throw throwable2;
                this.queue.close();
                throw throwable2;
            }
            this.queue.close();
            {
                throw throwable;
            }
        }
        try {
            ((FormatPostingsFieldsConsumer)fieldsConsumer).finish();
            Object var5_4 = null;
            if (this.queue == null) return;
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            if (this.queue == null) throw throwable;
            this.queue.close();
            throw throwable;
        }
        this.queue.close();
    }

    private final void mergeTermInfos(FormatPostingsFieldsConsumer consumer) throws CorruptIndexException, IOException {
        int base = 0;
        int readerCount = this.readers.size();
        for (int i = 0; i < readerCount; ++i) {
            int[] docMap;
            IndexReader reader = this.readers.get(i);
            TermEnum termEnum = reader.terms();
            SegmentMergeInfo smi = new SegmentMergeInfo(base, termEnum, reader);
            if (this.payloadProcessorProvider != null) {
                smi.readerPayloadProcessor = this.payloadProcessorProvider.getReaderProcessor(reader);
            }
            if ((docMap = smi.getDocMap()) != null) {
                if (this.docMaps == null) {
                    this.docMaps = new int[readerCount][];
                }
                this.docMaps[i] = docMap;
            }
            base += reader.numDocs();
            assert (reader.numDocs() == reader.maxDoc() - smi.delCount);
            if (smi.next()) {
                this.queue.add(smi);
                continue;
            }
            smi.close();
        }
        SegmentMergeInfo[] match = new SegmentMergeInfo[this.readers.size()];
        String currentField = null;
        FormatPostingsTermsConsumer termsConsumer = null;
        while (this.queue.size() > 0) {
            int matchSize = 0;
            match[matchSize++] = (SegmentMergeInfo)this.queue.pop();
            Term term = match[0].term;
            SegmentMergeInfo top = (SegmentMergeInfo)this.queue.top();
            while (top != null && term.compareTo(top.term) == 0) {
                match[matchSize++] = (SegmentMergeInfo)this.queue.pop();
                top = (SegmentMergeInfo)this.queue.top();
            }
            if (currentField != term.field) {
                currentField = term.field;
                if (termsConsumer != null) {
                    termsConsumer.finish();
                }
                FieldInfo fieldInfo = this.fieldInfos.fieldInfo(currentField);
                termsConsumer = consumer.addField(fieldInfo);
                this.indexOptions = fieldInfo.indexOptions;
            }
            int df = this.appendPostings(termsConsumer, match, matchSize);
            this.checkAbort.work((double)df / 3.0);
            while (matchSize > 0) {
                SegmentMergeInfo smi;
                if ((smi = match[--matchSize]).next()) {
                    this.queue.add(smi);
                    continue;
                }
                smi.close();
            }
        }
    }

    private final int appendPostings(FormatPostingsTermsConsumer termsConsumer, SegmentMergeInfo[] smis, int n) throws CorruptIndexException, IOException {
        FormatPostingsDocsConsumer docConsumer = termsConsumer.addTerm(smis[0].term.text);
        int df = 0;
        for (int i = 0; i < n; ++i) {
            SegmentMergeInfo smi = smis[i];
            TermPositions postings = smi.getPositions();
            assert (postings != null);
            int base = smi.base;
            int[] docMap = smi.getDocMap();
            postings.seek(smi.termEnum);
            PayloadProcessorProvider.PayloadProcessor payloadProcessor = null;
            if (smi.readerPayloadProcessor != null) {
                payloadProcessor = smi.readerPayloadProcessor.getProcessor(smi.term);
            }
            while (postings.next()) {
                ++df;
                int doc = postings.doc();
                if (docMap != null) {
                    doc = docMap[doc];
                }
                int freq = postings.freq();
                FormatPostingsPositionsConsumer posConsumer = docConsumer.addDoc(doc += base, freq);
                if (this.indexOptions != FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) continue;
                for (int j = 0; j < freq; ++j) {
                    int position = postings.nextPosition();
                    int payloadLength = postings.getPayloadLength();
                    if (payloadLength > 0) {
                        if (this.payloadBuffer == null || this.payloadBuffer.length < payloadLength) {
                            this.payloadBuffer = new byte[payloadLength];
                        }
                        postings.getPayload(this.payloadBuffer, 0);
                        if (payloadProcessor != null) {
                            this.payloadBuffer = payloadProcessor.processPayload(this.payloadBuffer, 0, payloadLength);
                            payloadLength = payloadProcessor.payloadLength();
                        }
                    }
                    posConsumer.addPosition(position, this.payloadBuffer, 0, payloadLength);
                }
                posConsumer.finish();
            }
        }
        docConsumer.finish();
        return df;
    }

    public boolean getAnyNonBulkMerges() {
        assert (this.matchedCount <= this.readers.size());
        return this.matchedCount != this.readers.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void mergeNorms() throws IOException {
        IndexOutput output;
        block11: {
            int bufferSize = 0;
            for (IndexReader reader : this.readers) {
                bufferSize = Math.max(bufferSize, reader.maxDoc());
            }
            byte[] normBuffer = null;
            output = null;
            boolean success = false;
            try {
                int numFieldInfos = this.fieldInfos.size();
                for (int i = 0; i < numFieldInfos; ++i) {
                    FieldInfo fi = this.fieldInfos.fieldInfo(i);
                    if (!fi.isIndexed || fi.omitNorms) continue;
                    if (output == null) {
                        output = this.directory.createOutput(IndexFileNames.segmentFileName(this.segment, "nrm"));
                        output.writeBytes(SegmentNorms.NORMS_HEADER, SegmentNorms.NORMS_HEADER.length);
                    }
                    if (normBuffer == null) {
                        normBuffer = new byte[bufferSize];
                    }
                    for (IndexReader reader : this.readers) {
                        int maxDoc = reader.maxDoc();
                        reader.norms(fi.name, normBuffer, 0);
                        if (!reader.hasDeletions()) {
                            output.writeBytes(normBuffer, maxDoc);
                        } else {
                            for (int k = 0; k < maxDoc; ++k) {
                                if (reader.isDeleted(k)) continue;
                                output.writeByte(normBuffer[k]);
                            }
                        }
                        this.checkAbort.work(maxDoc);
                    }
                }
                success = true;
                Object var13_12 = null;
                if (!success) break block11;
            }
            catch (Throwable throwable) {
                Object var13_13 = null;
                if (success) {
                    IOUtils.close(output);
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(output);
                throw throwable;
            }
            IOUtils.close(output);
            return;
        }
        IOUtils.closeWhileHandlingException(output);
    }

    static class CheckAbort {
        private double workCount;
        private MergePolicy.OneMerge merge;
        private Directory dir;

        public CheckAbort(MergePolicy.OneMerge merge, Directory dir) {
            this.merge = merge;
            this.dir = dir;
        }

        public void work(double units) throws MergePolicy.MergeAbortedException {
            this.workCount += units;
            if (this.workCount >= 10000.0) {
                this.merge.checkAborted(this.dir);
                this.workCount = 0.0;
            }
        }
    }
}

