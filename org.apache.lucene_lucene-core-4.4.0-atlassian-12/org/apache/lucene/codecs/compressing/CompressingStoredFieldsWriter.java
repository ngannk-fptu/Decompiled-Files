/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.StoredFieldsWriter;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsIndexWriter;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsReader;
import org.apache.lucene.codecs.compressing.CompressionMode;
import org.apache.lucene.codecs.compressing.Compressor;
import org.apache.lucene.codecs.compressing.GrowableByteArrayDataOutput;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.packed.PackedInts;

public final class CompressingStoredFieldsWriter
extends StoredFieldsWriter {
    static final int MAX_DOCUMENTS_PER_CHUNK = 128;
    static final int STRING = 0;
    static final int BYTE_ARR = 1;
    static final int NUMERIC_INT = 2;
    static final int NUMERIC_FLOAT = 3;
    static final int NUMERIC_LONG = 4;
    static final int NUMERIC_DOUBLE = 5;
    static final int TYPE_BITS = PackedInts.bitsRequired(5L);
    static final int TYPE_MASK = (int)PackedInts.maxValue(TYPE_BITS);
    static final String CODEC_SFX_IDX = "Index";
    static final String CODEC_SFX_DAT = "Data";
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 0;
    private final Directory directory;
    private final String segment;
    private final String segmentSuffix;
    private CompressingStoredFieldsIndexWriter indexWriter;
    private IndexOutput fieldsStream;
    private final CompressionMode compressionMode;
    private final Compressor compressor;
    private final int chunkSize;
    private final GrowableByteArrayDataOutput bufferedDocs;
    private int[] numStoredFields;
    private int[] endOffsets;
    private int docBase;
    private int numBufferedDocs;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public CompressingStoredFieldsWriter(Directory directory, SegmentInfo si, String segmentSuffix, IOContext context, String formatName, CompressionMode compressionMode, int chunkSize) throws IOException {
        assert (directory != null);
        this.directory = directory;
        this.segment = si.name;
        this.segmentSuffix = segmentSuffix;
        this.compressionMode = compressionMode;
        this.compressor = compressionMode.newCompressor();
        this.chunkSize = chunkSize;
        this.docBase = 0;
        this.bufferedDocs = new GrowableByteArrayDataOutput(chunkSize);
        this.numStoredFields = new int[16];
        this.endOffsets = new int[16];
        this.numBufferedDocs = 0;
        boolean success = false;
        IndexOutput indexStream = directory.createOutput(IndexFileNames.segmentFileName(this.segment, segmentSuffix, "fdx"), context);
        try {
            this.fieldsStream = directory.createOutput(IndexFileNames.segmentFileName(this.segment, segmentSuffix, "fdt"), context);
            String codecNameIdx = formatName + CODEC_SFX_IDX;
            String codecNameDat = formatName + CODEC_SFX_DAT;
            CodecUtil.writeHeader(indexStream, codecNameIdx, 0);
            CodecUtil.writeHeader(this.fieldsStream, codecNameDat, 0);
            assert ((long)CodecUtil.headerLength(codecNameDat) == this.fieldsStream.getFilePointer());
            assert ((long)CodecUtil.headerLength(codecNameIdx) == indexStream.getFilePointer());
            this.indexWriter = new CompressingStoredFieldsIndexWriter(indexStream);
            indexStream = null;
            this.fieldsStream.writeVInt(1);
            return;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(indexStream);
            this.abort();
            throw throwable;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            IOUtils.close(this.fieldsStream, this.indexWriter);
        }
        finally {
            this.fieldsStream = null;
            this.indexWriter = null;
        }
    }

    @Override
    public void startDocument(int numStoredFields) throws IOException {
        if (this.numBufferedDocs == this.numStoredFields.length) {
            int newLength = ArrayUtil.oversize(this.numBufferedDocs + 1, 4);
            this.numStoredFields = Arrays.copyOf(this.numStoredFields, newLength);
            this.endOffsets = Arrays.copyOf(this.endOffsets, newLength);
        }
        this.numStoredFields[this.numBufferedDocs] = numStoredFields;
        ++this.numBufferedDocs;
    }

    @Override
    public void finishDocument() throws IOException {
        this.endOffsets[this.numBufferedDocs - 1] = this.bufferedDocs.length;
        if (this.triggerFlush()) {
            this.flush();
        }
    }

    private static void saveInts(int[] values, int length, DataOutput out) throws IOException {
        assert (length > 0);
        if (length == 1) {
            out.writeVInt(values[0]);
        } else {
            boolean allEqual = true;
            for (int i = 1; i < length; ++i) {
                if (values[i] == values[0]) continue;
                allEqual = false;
                break;
            }
            if (allEqual) {
                out.writeVInt(0);
                out.writeVInt(values[0]);
            } else {
                long max = 0L;
                for (int i = 0; i < length; ++i) {
                    max |= (long)values[i];
                }
                int bitsRequired = PackedInts.bitsRequired(max);
                out.writeVInt(bitsRequired);
                PackedInts.Writer w = PackedInts.getWriterNoHeader(out, PackedInts.Format.PACKED, length, bitsRequired, 1);
                for (int i = 0; i < length; ++i) {
                    w.add(values[i]);
                }
                w.finish();
            }
        }
    }

    private void writeHeader(int docBase, int numBufferedDocs, int[] numStoredFields, int[] lengths) throws IOException {
        this.fieldsStream.writeVInt(docBase);
        this.fieldsStream.writeVInt(numBufferedDocs);
        CompressingStoredFieldsWriter.saveInts(numStoredFields, numBufferedDocs, this.fieldsStream);
        CompressingStoredFieldsWriter.saveInts(lengths, numBufferedDocs, this.fieldsStream);
    }

    private boolean triggerFlush() {
        return this.bufferedDocs.length >= this.chunkSize || this.numBufferedDocs >= 128;
    }

    private void flush() throws IOException {
        this.indexWriter.writeIndex(this.numBufferedDocs, this.fieldsStream.getFilePointer());
        int[] lengths = this.endOffsets;
        for (int i = this.numBufferedDocs - 1; i > 0; --i) {
            lengths[i] = this.endOffsets[i] - this.endOffsets[i - 1];
            assert (lengths[i] >= 0);
        }
        this.writeHeader(this.docBase, this.numBufferedDocs, this.numStoredFields, lengths);
        this.compressor.compress(this.bufferedDocs.bytes, 0, this.bufferedDocs.length, this.fieldsStream);
        this.docBase += this.numBufferedDocs;
        this.numBufferedDocs = 0;
        this.bufferedDocs.length = 0;
    }

    @Override
    public void writeField(FieldInfo info, IndexableField field) throws IOException {
        BytesRef bytes;
        String string;
        int bits = 0;
        Number number = field.numericValue();
        if (number != null) {
            if (number instanceof Byte || number instanceof Short || number instanceof Integer) {
                bits = 2;
            } else if (number instanceof Long) {
                bits = 4;
            } else if (number instanceof Float) {
                bits = 3;
            } else if (number instanceof Double) {
                bits = 5;
            } else {
                throw new IllegalArgumentException("cannot store numeric type " + number.getClass());
            }
            string = null;
            bytes = null;
        } else {
            bytes = field.binaryValue();
            if (bytes != null) {
                bits = 1;
                string = null;
            } else {
                bits = 0;
                string = field.stringValue();
                if (string == null) {
                    throw new IllegalArgumentException("field " + field.name() + " is stored but does not have binaryValue, stringValue nor numericValue");
                }
            }
        }
        long infoAndBits = (long)info.number << TYPE_BITS | (long)bits;
        this.bufferedDocs.writeVLong(infoAndBits);
        if (bytes != null) {
            this.bufferedDocs.writeVInt(bytes.length);
            this.bufferedDocs.writeBytes(bytes.bytes, bytes.offset, bytes.length);
        } else if (string != null) {
            this.bufferedDocs.writeString(field.stringValue());
        } else if (number instanceof Byte || number instanceof Short || number instanceof Integer) {
            this.bufferedDocs.writeInt(number.intValue());
        } else if (number instanceof Long) {
            this.bufferedDocs.writeLong(number.longValue());
        } else if (number instanceof Float) {
            this.bufferedDocs.writeInt(Float.floatToIntBits(number.floatValue()));
        } else if (number instanceof Double) {
            this.bufferedDocs.writeLong(Double.doubleToLongBits(number.doubleValue()));
        } else {
            throw new AssertionError((Object)"Cannot get here");
        }
    }

    @Override
    public void abort() {
        IOUtils.closeWhileHandlingException(this);
        IOUtils.deleteFilesIgnoringExceptions(this.directory, IndexFileNames.segmentFileName(this.segment, this.segmentSuffix, "fdt"), IndexFileNames.segmentFileName(this.segment, this.segmentSuffix, "fdx"));
    }

    @Override
    public void finish(FieldInfos fis, int numDocs) throws IOException {
        if (this.numBufferedDocs > 0) {
            this.flush();
        } else assert (this.bufferedDocs.length == 0);
        if (this.docBase != numDocs) {
            throw new RuntimeException("Wrote " + this.docBase + " docs, finish called with numDocs=" + numDocs);
        }
        this.indexWriter.finish(numDocs);
        assert (this.bufferedDocs.length == 0);
    }

    @Override
    public int merge(MergeState mergeState) throws IOException {
        int docCount = 0;
        int idx = 0;
        for (AtomicReader reader : mergeState.readers) {
            StoredFieldsReader fieldsReader;
            SegmentReader matchingSegmentReader = mergeState.matchingSegmentReaders[idx++];
            CompressingStoredFieldsReader matchingFieldsReader = null;
            if (matchingSegmentReader != null && (fieldsReader = matchingSegmentReader.getFieldsReader()) != null && fieldsReader instanceof CompressingStoredFieldsReader) {
                matchingFieldsReader = (CompressingStoredFieldsReader)fieldsReader;
            }
            int maxDoc = reader.maxDoc();
            Bits liveDocs = reader.getLiveDocs();
            if (matchingFieldsReader == null) {
                int i = CompressingStoredFieldsWriter.nextLiveDoc(0, liveDocs, maxDoc);
                while (i < maxDoc) {
                    Document doc = reader.document(i);
                    this.addDocument(doc, mergeState.fieldInfos);
                    ++docCount;
                    mergeState.checkAbort.work(300.0);
                    i = CompressingStoredFieldsWriter.nextLiveDoc(i + 1, liveDocs, maxDoc);
                }
                continue;
            }
            int docID = CompressingStoredFieldsWriter.nextLiveDoc(0, liveDocs, maxDoc);
            if (docID >= maxDoc) continue;
            CompressingStoredFieldsReader.ChunkIterator it = matchingFieldsReader.chunkIterator(docID);
            int[] startOffsets = new int[]{};
            do {
                it.next(docID);
                if (startOffsets.length < it.chunkDocs) {
                    startOffsets = new int[ArrayUtil.oversize(it.chunkDocs, 4)];
                }
                for (int i = 1; i < it.chunkDocs; ++i) {
                    startOffsets[i] = startOffsets[i - 1] + it.lengths[i - 1];
                }
                if (this.compressionMode == matchingFieldsReader.getCompressionMode() && this.numBufferedDocs == 0 && startOffsets[it.chunkDocs - 1] < this.chunkSize && startOffsets[it.chunkDocs - 1] + it.lengths[it.chunkDocs - 1] >= this.chunkSize && CompressingStoredFieldsWriter.nextDeletedDoc(it.docBase, liveDocs, it.docBase + it.chunkDocs) == it.docBase + it.chunkDocs) {
                    assert (docID == it.docBase);
                    this.indexWriter.writeIndex(it.chunkDocs, this.fieldsStream.getFilePointer());
                    this.writeHeader(this.docBase, it.chunkDocs, it.numStoredFields, it.lengths);
                    it.copyCompressedData(this.fieldsStream);
                    this.docBase += it.chunkDocs;
                    docID = CompressingStoredFieldsWriter.nextLiveDoc(it.docBase + it.chunkDocs, liveDocs, maxDoc);
                    docCount += it.chunkDocs;
                    mergeState.checkAbort.work(300 * it.chunkDocs);
                    continue;
                }
                it.decompress();
                if (startOffsets[it.chunkDocs - 1] + it.lengths[it.chunkDocs - 1] != it.bytes.length) {
                    throw new CorruptIndexException("Corrupted: expected chunk size=" + startOffsets[it.chunkDocs - 1] + it.lengths[it.chunkDocs - 1] + ", got " + it.bytes.length);
                }
                while (docID < it.docBase + it.chunkDocs) {
                    int diff = docID - it.docBase;
                    this.startDocument(it.numStoredFields[diff]);
                    this.bufferedDocs.writeBytes(it.bytes.bytes, it.bytes.offset + startOffsets[diff], it.lengths[diff]);
                    this.finishDocument();
                    ++docCount;
                    mergeState.checkAbort.work(300.0);
                    docID = CompressingStoredFieldsWriter.nextLiveDoc(docID + 1, liveDocs, maxDoc);
                }
            } while (docID < maxDoc);
        }
        this.finish(mergeState.fieldInfos, docCount);
        return docCount;
    }

    private static int nextLiveDoc(int doc, Bits liveDocs, int maxDoc) {
        if (liveDocs == null) {
            return doc;
        }
        while (doc < maxDoc && !liveDocs.get(doc)) {
            ++doc;
        }
        return doc;
    }

    private static int nextDeletedDoc(int doc, Bits liveDocs, int maxDoc) {
        if (liveDocs == null) {
            return maxDoc;
        }
        while (doc < maxDoc && liveDocs.get(doc)) {
            ++doc;
        }
        return doc;
    }
}

