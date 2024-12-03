/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.document.NumericField;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.RAMOutputStream;
import com.atlassian.lucene36.util.IOUtils;
import java.io.IOException;
import java.util.List;

final class FieldsWriter {
    static final int FIELD_IS_TOKENIZED = 1;
    static final int FIELD_IS_BINARY = 2;
    @Deprecated
    static final int FIELD_IS_COMPRESSED = 4;
    private static final int _NUMERIC_BIT_SHIFT = 3;
    static final int FIELD_IS_NUMERIC_MASK = 56;
    static final int FIELD_IS_NUMERIC_INT = 8;
    static final int FIELD_IS_NUMERIC_LONG = 16;
    static final int FIELD_IS_NUMERIC_FLOAT = 24;
    static final int FIELD_IS_NUMERIC_DOUBLE = 32;
    static final int FORMAT = 0;
    static final int FORMAT_VERSION_UTF8_LENGTH_IN_BYTES = 1;
    static final int FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS = 2;
    static final int FORMAT_LUCENE_3_2_NUMERIC_FIELDS = 3;
    static final int FORMAT_CURRENT = 3;
    private FieldInfos fieldInfos;
    private Directory directory;
    private String segment;
    private IndexOutput fieldsStream;
    private IndexOutput indexStream;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    FieldsWriter(Directory directory, String segment, FieldInfos fn) throws IOException {
        this.directory = directory;
        this.segment = segment;
        this.fieldInfos = fn;
        boolean success = false;
        try {
            this.fieldsStream = directory.createOutput(IndexFileNames.segmentFileName(segment, "fdt"));
            this.indexStream = directory.createOutput(IndexFileNames.segmentFileName(segment, "fdx"));
            this.fieldsStream.writeInt(3);
            this.indexStream.writeInt(3);
            success = true;
            Object var6_5 = null;
            if (!success) {
                this.abort();
            }
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            if (!success) {
                this.abort();
            }
            throw throwable;
        }
    }

    FieldsWriter(IndexOutput fdx, IndexOutput fdt, FieldInfos fn) {
        this.directory = null;
        this.segment = null;
        this.fieldInfos = fn;
        this.fieldsStream = fdt;
        this.indexStream = fdx;
    }

    void setFieldsStream(IndexOutput stream) {
        this.fieldsStream = stream;
    }

    void flushDocument(int numStoredFields, RAMOutputStream buffer) throws IOException {
        this.indexStream.writeLong(this.fieldsStream.getFilePointer());
        this.fieldsStream.writeVInt(numStoredFields);
        buffer.writeTo(this.fieldsStream);
    }

    void skipDocument() throws IOException {
        this.indexStream.writeLong(this.fieldsStream.getFilePointer());
        this.fieldsStream.writeVInt(0);
    }

    void finish(int numDocs) throws IOException {
        if (4L + (long)numDocs * 8L != this.indexStream.getFilePointer()) {
            String fieldsIdxName = IndexFileNames.segmentFileName(this.segment, "fdx");
            throw new RuntimeException("fdx size mismatch: " + numDocs + " docs vs " + this.indexStream.getFilePointer() + " length in bytes of " + fieldsIdxName + " file exists?=" + this.directory.fileExists(fieldsIdxName));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void close() throws IOException {
        if (this.directory != null) {
            try {
                IOUtils.close(this.fieldsStream, this.indexStream);
                Object var2_1 = null;
                this.indexStream = null;
                this.fieldsStream = null;
            }
            catch (Throwable throwable) {
                Object var2_2 = null;
                this.indexStream = null;
                this.fieldsStream = null;
                throw throwable;
            }
        }
    }

    void abort() {
        if (this.directory != null) {
            try {
                this.close();
            }
            catch (IOException ignored) {
                // empty catch block
            }
            try {
                this.directory.deleteFile(IndexFileNames.segmentFileName(this.segment, "fdt"));
            }
            catch (IOException ignored) {
                // empty catch block
            }
            try {
                this.directory.deleteFile(IndexFileNames.segmentFileName(this.segment, "fdx"));
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    final void writeField(FieldInfo fi, Fieldable field) throws IOException {
        this.fieldsStream.writeVInt(fi.number);
        int bits = 0;
        if (field.isTokenized()) {
            bits |= 1;
        }
        if (field.isBinary()) {
            bits |= 2;
        }
        if (field instanceof NumericField) {
            switch (((NumericField)field).getDataType()) {
                case INT: {
                    bits |= 8;
                    break;
                }
                case LONG: {
                    bits |= 0x10;
                    break;
                }
                case FLOAT: {
                    bits |= 0x18;
                    break;
                }
                case DOUBLE: {
                    bits |= 0x20;
                    break;
                }
                default: {
                    assert (false) : "Should never get here";
                    break;
                }
            }
        }
        this.fieldsStream.writeByte((byte)bits);
        if (field.isBinary()) {
            byte[] data = field.getBinaryValue();
            int len = field.getBinaryLength();
            int offset = field.getBinaryOffset();
            this.fieldsStream.writeVInt(len);
            this.fieldsStream.writeBytes(data, offset, len);
        } else if (field instanceof NumericField) {
            NumericField nf = (NumericField)field;
            Number n = nf.getNumericValue();
            switch (nf.getDataType()) {
                case INT: {
                    this.fieldsStream.writeInt(n.intValue());
                    break;
                }
                case LONG: {
                    this.fieldsStream.writeLong(n.longValue());
                    break;
                }
                case FLOAT: {
                    this.fieldsStream.writeInt(Float.floatToIntBits(n.floatValue()));
                    break;
                }
                case DOUBLE: {
                    this.fieldsStream.writeLong(Double.doubleToLongBits(n.doubleValue()));
                    break;
                }
                default: {
                    assert (false) : "Should never get here";
                    {
                        break;
                    }
                }
            }
        } else {
            this.fieldsStream.writeString(field.stringValue());
        }
    }

    final void addRawDocuments(IndexInput stream, int[] lengths, int numDocs) throws IOException {
        long position;
        long start = position = this.fieldsStream.getFilePointer();
        for (int i = 0; i < numDocs; ++i) {
            this.indexStream.writeLong(position);
            position += (long)lengths[i];
        }
        this.fieldsStream.copyBytes(stream, position - start);
        assert (this.fieldsStream.getFilePointer() == position);
    }

    final void addDocument(Document doc) throws IOException {
        this.indexStream.writeLong(this.fieldsStream.getFilePointer());
        int storedCount = 0;
        List<Fieldable> fields = doc.getFields();
        for (Fieldable field : fields) {
            if (!field.isStored()) continue;
            ++storedCount;
        }
        this.fieldsStream.writeVInt(storedCount);
        for (Fieldable field : fields) {
            if (!field.isStored()) continue;
            this.writeField(this.fieldInfos.fieldInfo(field.name()), field);
        }
    }
}

