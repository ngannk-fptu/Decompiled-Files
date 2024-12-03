/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoFormat;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexFormatTooNewException;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.CompoundFileDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.IOUtils;

@Deprecated
final class Lucene3xStoredFieldsReader
extends StoredFieldsReader
implements Cloneable,
Closeable {
    private static final int FORMAT_SIZE = 4;
    public static final String FIELDS_EXTENSION = "fdt";
    public static final String FIELDS_INDEX_EXTENSION = "fdx";
    static final int FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS = 2;
    static final int FORMAT_LUCENE_3_2_NUMERIC_FIELDS = 3;
    public static final int FORMAT_CURRENT = 3;
    static final int FORMAT_MINIMUM = 2;
    public static final int FIELD_IS_BINARY = 2;
    private static final int _NUMERIC_BIT_SHIFT = 3;
    static final int FIELD_IS_NUMERIC_MASK = 56;
    public static final int FIELD_IS_NUMERIC_INT = 8;
    public static final int FIELD_IS_NUMERIC_LONG = 16;
    public static final int FIELD_IS_NUMERIC_FLOAT = 24;
    public static final int FIELD_IS_NUMERIC_DOUBLE = 32;
    private final FieldInfos fieldInfos;
    private final IndexInput fieldsStream;
    private final IndexInput indexStream;
    private int numTotalDocs;
    private int size;
    private boolean closed;
    private final int format;
    private int docStoreOffset;
    private final CompoundFileDirectory storeCFSReader;

    @Override
    public Lucene3xStoredFieldsReader clone() {
        this.ensureOpen();
        return new Lucene3xStoredFieldsReader(this.fieldInfos, this.numTotalDocs, this.size, this.format, this.docStoreOffset, this.fieldsStream.clone(), this.indexStream.clone());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void checkCodeVersion(Directory dir, String segment) throws IOException {
        String indexStreamFN = IndexFileNames.segmentFileName(segment, "", FIELDS_INDEX_EXTENSION);
        try (IndexInput idxStream = dir.openInput(indexStreamFN, IOContext.DEFAULT);){
            int format = idxStream.readInt();
            if (format < 2) {
                throw new IndexFormatTooOldException(idxStream, format, 2, 3);
            }
            if (format > 3) {
                throw new IndexFormatTooNewException(idxStream, format, 2, 3);
            }
        }
    }

    private Lucene3xStoredFieldsReader(FieldInfos fieldInfos, int numTotalDocs, int size, int format, int docStoreOffset, IndexInput fieldsStream, IndexInput indexStream) {
        this.fieldInfos = fieldInfos;
        this.numTotalDocs = numTotalDocs;
        this.size = size;
        this.format = format;
        this.docStoreOffset = docStoreOffset;
        this.fieldsStream = fieldsStream;
        this.indexStream = indexStream;
        this.storeCFSReader = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Lucene3xStoredFieldsReader(Directory d, SegmentInfo si, FieldInfos fn, IOContext context) throws IOException {
        String segment = Lucene3xSegmentInfoFormat.getDocStoreSegment(si);
        int docStoreOffset = Lucene3xSegmentInfoFormat.getDocStoreOffset(si);
        int size = si.getDocCount();
        boolean success = false;
        this.fieldInfos = fn;
        try {
            if (docStoreOffset != -1 && Lucene3xSegmentInfoFormat.getDocStoreIsCompoundFile(si)) {
                this.storeCFSReader = new CompoundFileDirectory(si.dir, IndexFileNames.segmentFileName(segment, "", "cfx"), context, false);
                d = this.storeCFSReader;
            } else {
                this.storeCFSReader = null;
            }
            this.fieldsStream = d.openInput(IndexFileNames.segmentFileName(segment, "", FIELDS_EXTENSION), context);
            String indexStreamFN = IndexFileNames.segmentFileName(segment, "", FIELDS_INDEX_EXTENSION);
            this.indexStream = d.openInput(indexStreamFN, context);
            this.format = this.indexStream.readInt();
            if (this.format < 2) {
                throw new IndexFormatTooOldException(this.indexStream, this.format, 2, 3);
            }
            if (this.format > 3) {
                throw new IndexFormatTooNewException(this.indexStream, this.format, 2, 3);
            }
            long indexSize = this.indexStream.length() - 4L;
            if (docStoreOffset != -1) {
                this.docStoreOffset = docStoreOffset;
                this.size = size;
                assert ((int)(indexSize / 8L) >= size + this.docStoreOffset) : "indexSize=" + indexSize + " size=" + size + " docStoreOffset=" + docStoreOffset;
            } else {
                this.docStoreOffset = 0;
                this.size = (int)(indexSize >> 3);
                if (this.size != si.getDocCount()) {
                    throw new CorruptIndexException("doc counts differ for segment " + segment + ": fieldsReader shows " + this.size + " but segmentInfo shows " + si.getDocCount());
                }
            }
            this.numTotalDocs = (int)(indexSize >> 3);
            success = true;
        }
        finally {
            if (!success) {
                try {
                    this.close();
                }
                catch (Throwable throwable) {}
            }
        }
    }

    private void ensureOpen() throws AlreadyClosedException {
        if (this.closed) {
            throw new AlreadyClosedException("this FieldsReader is closed");
        }
    }

    @Override
    public final void close() throws IOException {
        if (!this.closed) {
            IOUtils.close(this.fieldsStream, this.indexStream, this.storeCFSReader);
            this.closed = true;
        }
    }

    private void seekIndex(int docID) throws IOException {
        this.indexStream.seek(4L + (long)(docID + this.docStoreOffset) * 8L);
    }

    @Override
    public final void visitDocument(int n, StoredFieldVisitor visitor) throws CorruptIndexException, IOException {
        this.seekIndex(n);
        this.fieldsStream.seek(this.indexStream.readLong());
        int numFields = this.fieldsStream.readVInt();
        block5: for (int fieldIDX = 0; fieldIDX < numFields; ++fieldIDX) {
            int fieldNumber = this.fieldsStream.readVInt();
            FieldInfo fieldInfo = this.fieldInfos.fieldInfo(fieldNumber);
            int bits = this.fieldsStream.readByte() & 0xFF;
            assert (bits <= 58) : "bits=" + Integer.toHexString(bits);
            switch (visitor.needsField(fieldInfo)) {
                case YES: {
                    this.readField(visitor, fieldInfo, bits);
                    continue block5;
                }
                case NO: {
                    this.skipField(bits);
                    continue block5;
                }
                case STOP: {
                    return;
                }
            }
        }
    }

    private void readField(StoredFieldVisitor visitor, FieldInfo info, int bits) throws IOException {
        int numeric = bits & 0x38;
        if (numeric != 0) {
            switch (numeric) {
                case 8: {
                    visitor.intField(info, this.fieldsStream.readInt());
                    return;
                }
                case 16: {
                    visitor.longField(info, this.fieldsStream.readLong());
                    return;
                }
                case 24: {
                    visitor.floatField(info, Float.intBitsToFloat(this.fieldsStream.readInt()));
                    return;
                }
                case 32: {
                    visitor.doubleField(info, Double.longBitsToDouble(this.fieldsStream.readLong()));
                    return;
                }
            }
            throw new CorruptIndexException("Invalid numeric type: " + Integer.toHexString(numeric));
        }
        int length = this.fieldsStream.readVInt();
        byte[] bytes = new byte[length];
        this.fieldsStream.readBytes(bytes, 0, length);
        if ((bits & 2) != 0) {
            visitor.binaryField(info, bytes);
        } else {
            visitor.stringField(info, new String(bytes, 0, bytes.length, IOUtils.CHARSET_UTF_8));
        }
    }

    private void skipField(int bits) throws IOException {
        int numeric = bits & 0x38;
        if (numeric != 0) {
            switch (numeric) {
                case 8: 
                case 24: {
                    this.fieldsStream.readInt();
                    return;
                }
                case 16: 
                case 32: {
                    this.fieldsStream.readLong();
                    return;
                }
            }
            throw new CorruptIndexException("Invalid numeric type: " + Integer.toHexString(numeric));
        }
        int length = this.fieldsStream.readVInt();
        this.fieldsStream.seek(this.fieldsStream.getFilePointer() + (long)length);
    }
}

