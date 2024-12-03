/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.StoredFieldsWriter;
import org.apache.lucene.codecs.lucene40.Lucene40StoredFieldsReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MergeState;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;

public final class Lucene40StoredFieldsWriter
extends StoredFieldsWriter {
    static final int FIELD_IS_BINARY = 2;
    private static final int _NUMERIC_BIT_SHIFT = 3;
    static final int FIELD_IS_NUMERIC_MASK = 56;
    static final int FIELD_IS_NUMERIC_INT = 8;
    static final int FIELD_IS_NUMERIC_LONG = 16;
    static final int FIELD_IS_NUMERIC_FLOAT = 24;
    static final int FIELD_IS_NUMERIC_DOUBLE = 32;
    static final String CODEC_NAME_IDX = "Lucene40StoredFieldsIndex";
    static final String CODEC_NAME_DAT = "Lucene40StoredFieldsData";
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 0;
    static final long HEADER_LENGTH_IDX = CodecUtil.headerLength("Lucene40StoredFieldsIndex");
    static final long HEADER_LENGTH_DAT = CodecUtil.headerLength("Lucene40StoredFieldsData");
    public static final String FIELDS_EXTENSION = "fdt";
    public static final String FIELDS_INDEX_EXTENSION = "fdx";
    private final Directory directory;
    private final String segment;
    private IndexOutput fieldsStream;
    private IndexOutput indexStream;
    private static final int MAX_RAW_MERGE_DOCS = 4192;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Lucene40StoredFieldsWriter(Directory directory, String segment, IOContext context) throws IOException {
        assert (directory != null);
        this.directory = directory;
        this.segment = segment;
        boolean success = false;
        try {
            this.fieldsStream = directory.createOutput(IndexFileNames.segmentFileName(segment, "", FIELDS_EXTENSION), context);
            this.indexStream = directory.createOutput(IndexFileNames.segmentFileName(segment, "", FIELDS_INDEX_EXTENSION), context);
            CodecUtil.writeHeader(this.fieldsStream, CODEC_NAME_DAT, 0);
            CodecUtil.writeHeader(this.indexStream, CODEC_NAME_IDX, 0);
            assert (HEADER_LENGTH_DAT == this.fieldsStream.getFilePointer());
            assert (HEADER_LENGTH_IDX == this.indexStream.getFilePointer());
            success = true;
        }
        finally {
            if (!success) {
                this.abort();
            }
        }
    }

    @Override
    public void startDocument(int numStoredFields) throws IOException {
        this.indexStream.writeLong(this.fieldsStream.getFilePointer());
        this.fieldsStream.writeVInt(numStoredFields);
    }

    @Override
    public void close() throws IOException {
        try {
            IOUtils.close(this.fieldsStream, this.indexStream);
        }
        finally {
            this.indexStream = null;
            this.fieldsStream = null;
        }
    }

    @Override
    public void abort() {
        try {
            this.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        IOUtils.deleteFilesIgnoringExceptions(this.directory, IndexFileNames.segmentFileName(this.segment, "", FIELDS_EXTENSION), IndexFileNames.segmentFileName(this.segment, "", FIELDS_INDEX_EXTENSION));
    }

    @Override
    public void writeField(FieldInfo info, IndexableField field) throws IOException {
        BytesRef bytes;
        String string;
        this.fieldsStream.writeVInt(info.number);
        int bits = 0;
        Number number = field.numericValue();
        if (number != null) {
            if (number instanceof Byte || number instanceof Short || number instanceof Integer) {
                bits |= 8;
            } else if (number instanceof Long) {
                bits |= 0x10;
            } else if (number instanceof Float) {
                bits |= 0x18;
            } else if (number instanceof Double) {
                bits |= 0x20;
            } else {
                throw new IllegalArgumentException("cannot store numeric type " + number.getClass());
            }
            string = null;
            bytes = null;
        } else {
            bytes = field.binaryValue();
            if (bytes != null) {
                bits |= 2;
                string = null;
            } else {
                string = field.stringValue();
                if (string == null) {
                    throw new IllegalArgumentException("field " + field.name() + " is stored but does not have binaryValue, stringValue nor numericValue");
                }
            }
        }
        this.fieldsStream.writeByte((byte)bits);
        if (bytes != null) {
            this.fieldsStream.writeVInt(bytes.length);
            this.fieldsStream.writeBytes(bytes.bytes, bytes.offset, bytes.length);
        } else if (string != null) {
            this.fieldsStream.writeString(field.stringValue());
        } else if (number instanceof Byte || number instanceof Short || number instanceof Integer) {
            this.fieldsStream.writeInt(number.intValue());
        } else if (number instanceof Long) {
            this.fieldsStream.writeLong(number.longValue());
        } else if (number instanceof Float) {
            this.fieldsStream.writeInt(Float.floatToIntBits(number.floatValue()));
        } else if (number instanceof Double) {
            this.fieldsStream.writeLong(Double.doubleToLongBits(number.doubleValue()));
        } else {
            throw new AssertionError((Object)"Cannot get here");
        }
    }

    public void addRawDocuments(IndexInput stream, int[] lengths, int numDocs) throws IOException {
        long position;
        long start = position = this.fieldsStream.getFilePointer();
        for (int i = 0; i < numDocs; ++i) {
            this.indexStream.writeLong(position);
            position += (long)lengths[i];
        }
        this.fieldsStream.copyBytes(stream, position - start);
        assert (this.fieldsStream.getFilePointer() == position);
    }

    @Override
    public void finish(FieldInfos fis, int numDocs) {
        if (HEADER_LENGTH_IDX + (long)numDocs * 8L != this.indexStream.getFilePointer()) {
            throw new RuntimeException("fdx size mismatch: docCount is " + numDocs + " but fdx file size is " + this.indexStream.getFilePointer() + " file=" + this.indexStream.toString() + "; now aborting this merge to prevent index corruption");
        }
    }

    @Override
    public int merge(MergeState mergeState) throws IOException {
        int docCount = 0;
        int[] rawDocLengths = new int[4192];
        int idx = 0;
        for (AtomicReader reader : mergeState.readers) {
            StoredFieldsReader fieldsReader;
            SegmentReader matchingSegmentReader = mergeState.matchingSegmentReaders[idx++];
            Lucene40StoredFieldsReader matchingFieldsReader = null;
            if (matchingSegmentReader != null && (fieldsReader = matchingSegmentReader.getFieldsReader()) != null && fieldsReader instanceof Lucene40StoredFieldsReader) {
                matchingFieldsReader = (Lucene40StoredFieldsReader)fieldsReader;
            }
            if (reader.getLiveDocs() != null) {
                docCount += this.copyFieldsWithDeletions(mergeState, reader, matchingFieldsReader, rawDocLengths);
                continue;
            }
            docCount += this.copyFieldsNoDeletions(mergeState, reader, matchingFieldsReader, rawDocLengths);
        }
        this.finish(mergeState.fieldInfos, docCount);
        return docCount;
    }

    private int copyFieldsWithDeletions(MergeState mergeState, AtomicReader reader, Lucene40StoredFieldsReader matchingFieldsReader, int[] rawDocLengths) throws IOException {
        int docCount = 0;
        int maxDoc = reader.maxDoc();
        Bits liveDocs = reader.getLiveDocs();
        assert (liveDocs != null);
        if (matchingFieldsReader != null) {
            int j = 0;
            while (j < maxDoc) {
                if (!liveDocs.get(j)) {
                    ++j;
                    continue;
                }
                int start = j;
                int numDocs = 0;
                do {
                    ++numDocs;
                    if (++j >= maxDoc) break;
                    if (liveDocs.get(j)) continue;
                    ++j;
                    break;
                } while (numDocs < 4192);
                IndexInput stream = matchingFieldsReader.rawDocs(rawDocLengths, start, numDocs);
                this.addRawDocuments(stream, rawDocLengths, numDocs);
                docCount += numDocs;
                mergeState.checkAbort.work(300 * numDocs);
            }
        } else {
            for (int j = 0; j < maxDoc; ++j) {
                if (!liveDocs.get(j)) continue;
                Document doc = reader.document(j);
                this.addDocument(doc, mergeState.fieldInfos);
                ++docCount;
                mergeState.checkAbort.work(300.0);
            }
        }
        return docCount;
    }

    private int copyFieldsNoDeletions(MergeState mergeState, AtomicReader reader, Lucene40StoredFieldsReader matchingFieldsReader, int[] rawDocLengths) throws IOException {
        int docCount;
        int maxDoc = reader.maxDoc();
        if (matchingFieldsReader != null) {
            int len;
            for (docCount = 0; docCount < maxDoc; docCount += len) {
                len = Math.min(4192, maxDoc - docCount);
                IndexInput stream = matchingFieldsReader.rawDocs(rawDocLengths, docCount, len);
                this.addRawDocuments(stream, rawDocLengths, len);
                mergeState.checkAbort.work(300 * len);
            }
        } else {
            while (docCount < maxDoc) {
                Document doc = reader.document(docCount);
                this.addDocument(doc, mergeState.fieldInfos);
                mergeState.checkAbort.work(300.0);
                ++docCount;
            }
        }
        return docCount;
    }
}

