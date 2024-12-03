/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.lucene40.Lucene40StoredFieldsWriter;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.IOUtils;

public final class Lucene40StoredFieldsReader
extends StoredFieldsReader
implements Cloneable,
Closeable {
    private final FieldInfos fieldInfos;
    private final IndexInput fieldsStream;
    private final IndexInput indexStream;
    private int numTotalDocs;
    private int size;
    private boolean closed;

    @Override
    public Lucene40StoredFieldsReader clone() {
        this.ensureOpen();
        return new Lucene40StoredFieldsReader(this.fieldInfos, this.numTotalDocs, this.size, this.fieldsStream.clone(), this.indexStream.clone());
    }

    private Lucene40StoredFieldsReader(FieldInfos fieldInfos, int numTotalDocs, int size, IndexInput fieldsStream, IndexInput indexStream) {
        this.fieldInfos = fieldInfos;
        this.numTotalDocs = numTotalDocs;
        this.size = size;
        this.fieldsStream = fieldsStream;
        this.indexStream = indexStream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Lucene40StoredFieldsReader(Directory d, SegmentInfo si, FieldInfos fn, IOContext context) throws IOException {
        String segment = si.name;
        boolean success = false;
        this.fieldInfos = fn;
        try {
            this.fieldsStream = d.openInput(IndexFileNames.segmentFileName(segment, "", "fdt"), context);
            String indexStreamFN = IndexFileNames.segmentFileName(segment, "", "fdx");
            this.indexStream = d.openInput(indexStreamFN, context);
            CodecUtil.checkHeader(this.indexStream, "Lucene40StoredFieldsIndex", 0, 0);
            CodecUtil.checkHeader(this.fieldsStream, "Lucene40StoredFieldsData", 0, 0);
            assert (Lucene40StoredFieldsWriter.HEADER_LENGTH_DAT == this.fieldsStream.getFilePointer());
            assert (Lucene40StoredFieldsWriter.HEADER_LENGTH_IDX == this.indexStream.getFilePointer());
            long indexSize = this.indexStream.length() - Lucene40StoredFieldsWriter.HEADER_LENGTH_IDX;
            this.size = (int)(indexSize >> 3);
            if (this.size != si.getDocCount()) {
                throw new CorruptIndexException("doc counts differ for segment " + segment + ": fieldsReader shows " + this.size + " but segmentInfo shows " + si.getDocCount());
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
            IOUtils.close(this.fieldsStream, this.indexStream);
            this.closed = true;
        }
    }

    public final int size() {
        return this.size;
    }

    private void seekIndex(int docID) throws IOException {
        this.indexStream.seek(Lucene40StoredFieldsWriter.HEADER_LENGTH_IDX + (long)docID * 8L);
    }

    @Override
    public final void visitDocument(int n, StoredFieldVisitor visitor) throws IOException {
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

    public final IndexInput rawDocs(int[] lengths, int startDocID, int numDocs) throws IOException {
        long startOffset;
        this.seekIndex(startDocID);
        long lastOffset = startOffset = this.indexStream.readLong();
        int count = 0;
        while (count < numDocs) {
            int docID = startDocID + count + 1;
            assert (docID <= this.numTotalDocs);
            long offset = docID < this.numTotalDocs ? this.indexStream.readLong() : this.fieldsStream.length();
            lengths[count++] = (int)(offset - lastOffset);
            lastOffset = offset;
        }
        this.fieldsStream.seek(startOffset);
        return this.fieldsStream;
    }
}

