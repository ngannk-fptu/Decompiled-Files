/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsIndexReader;
import org.apache.lucene.codecs.compressing.CompressingStoredFieldsWriter;
import org.apache.lucene.codecs.compressing.CompressionMode;
import org.apache.lucene.codecs.compressing.Decompressor;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.packed.PackedInts;

public final class CompressingStoredFieldsReader
extends StoredFieldsReader {
    private static final int BUFFER_REUSE_THRESHOLD = 32768;
    private final FieldInfos fieldInfos;
    private final CompressingStoredFieldsIndexReader indexReader;
    private final IndexInput fieldsStream;
    private final int packedIntsVersion;
    private final CompressionMode compressionMode;
    private final Decompressor decompressor;
    private final BytesRef bytes;
    private final int numDocs;
    private boolean closed;

    private CompressingStoredFieldsReader(CompressingStoredFieldsReader reader) {
        this.fieldInfos = reader.fieldInfos;
        this.fieldsStream = reader.fieldsStream.clone();
        this.indexReader = reader.indexReader.clone();
        this.packedIntsVersion = reader.packedIntsVersion;
        this.compressionMode = reader.compressionMode;
        this.decompressor = reader.decompressor.clone();
        this.numDocs = reader.numDocs;
        this.bytes = new BytesRef(reader.bytes.bytes.length);
        this.closed = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public CompressingStoredFieldsReader(Directory d, SegmentInfo si, String segmentSuffix, FieldInfos fn, IOContext context, String formatName, CompressionMode compressionMode) throws IOException {
        this.compressionMode = compressionMode;
        String segment = si.name;
        boolean success = false;
        this.fieldInfos = fn;
        this.numDocs = si.getDocCount();
        IndexInput indexStream = null;
        try {
            String indexStreamFN = IndexFileNames.segmentFileName(segment, segmentSuffix, "fdx");
            indexStream = d.openInput(indexStreamFN, context);
            String codecNameIdx = formatName + "Index";
            CodecUtil.checkHeader(indexStream, codecNameIdx, 0, 0);
            assert ((long)CodecUtil.headerLength(codecNameIdx) == indexStream.getFilePointer());
            this.indexReader = new CompressingStoredFieldsIndexReader(indexStream, si);
            indexStream.close();
            indexStream = null;
            String fieldsStreamFN = IndexFileNames.segmentFileName(segment, segmentSuffix, "fdt");
            this.fieldsStream = d.openInput(fieldsStreamFN, context);
            String codecNameDat = formatName + "Data";
            CodecUtil.checkHeader(this.fieldsStream, codecNameDat, 0, 0);
            assert ((long)CodecUtil.headerLength(codecNameDat) == this.fieldsStream.getFilePointer());
            this.packedIntsVersion = this.fieldsStream.readVInt();
            this.decompressor = compressionMode.newDecompressor();
            this.bytes = new BytesRef();
            success = true;
            if (success) return;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(this, indexStream);
            throw throwable;
        }
        IOUtils.closeWhileHandlingException(this, indexStream);
    }

    private void ensureOpen() throws AlreadyClosedException {
        if (this.closed) {
            throw new AlreadyClosedException("this FieldsReader is closed");
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            IOUtils.close(this.fieldsStream);
            this.closed = true;
        }
    }

    private static void readField(ByteArrayDataInput in, StoredFieldVisitor visitor, FieldInfo info, int bits) throws IOException {
        switch (bits & CompressingStoredFieldsWriter.TYPE_MASK) {
            case 1: {
                int length = in.readVInt();
                byte[] data = new byte[length];
                in.readBytes(data, 0, length);
                visitor.binaryField(info, data);
                break;
            }
            case 0: {
                int length = in.readVInt();
                byte[] data = new byte[length];
                in.readBytes(data, 0, length);
                visitor.stringField(info, new String(data, IOUtils.CHARSET_UTF_8));
                break;
            }
            case 2: {
                visitor.intField(info, in.readInt());
                break;
            }
            case 3: {
                visitor.floatField(info, Float.intBitsToFloat(in.readInt()));
                break;
            }
            case 4: {
                visitor.longField(info, in.readLong());
                break;
            }
            case 5: {
                visitor.doubleField(info, Double.longBitsToDouble(in.readLong()));
                break;
            }
            default: {
                throw new AssertionError((Object)("Unknown type flag: " + Integer.toHexString(bits)));
            }
        }
    }

    private static void skipField(ByteArrayDataInput in, int bits) throws IOException {
        switch (bits & CompressingStoredFieldsWriter.TYPE_MASK) {
            case 0: 
            case 1: {
                int length = in.readVInt();
                in.skipBytes(length);
                break;
            }
            case 2: 
            case 3: {
                in.readInt();
                break;
            }
            case 4: 
            case 5: {
                in.readLong();
                break;
            }
            default: {
                throw new AssertionError((Object)("Unknown type flag: " + Integer.toHexString(bits)));
            }
        }
    }

    @Override
    public void visitDocument(int docID, StoredFieldVisitor visitor) throws IOException {
        int totalLength;
        int length;
        int offset;
        int numStoredFields;
        this.fieldsStream.seek(this.indexReader.getStartPointer(docID));
        int docBase = this.fieldsStream.readVInt();
        int chunkDocs = this.fieldsStream.readVInt();
        if (docID < docBase || docID >= docBase + chunkDocs || docBase + chunkDocs > this.numDocs) {
            throw new CorruptIndexException("Corrupted: docID=" + docID + ", docBase=" + docBase + ", chunkDocs=" + chunkDocs + ", numDocs=" + this.numDocs + " (resource=" + this.fieldsStream + ")");
        }
        if (chunkDocs == 1) {
            numStoredFields = this.fieldsStream.readVInt();
            offset = 0;
            totalLength = length = this.fieldsStream.readVInt();
        } else {
            int bitsPerStoredFields = this.fieldsStream.readVInt();
            if (bitsPerStoredFields == 0) {
                numStoredFields = this.fieldsStream.readVInt();
            } else {
                if (bitsPerStoredFields > 31) {
                    throw new CorruptIndexException("bitsPerStoredFields=" + bitsPerStoredFields + " (resource=" + this.fieldsStream + ")");
                }
                long filePointer = this.fieldsStream.getFilePointer();
                PackedInts.Reader reader = PackedInts.getDirectReaderNoHeader(this.fieldsStream, PackedInts.Format.PACKED, this.packedIntsVersion, chunkDocs, bitsPerStoredFields);
                numStoredFields = (int)reader.get(docID - docBase);
                this.fieldsStream.seek(filePointer + PackedInts.Format.PACKED.byteCount(this.packedIntsVersion, chunkDocs, bitsPerStoredFields));
            }
            int bitsPerLength = this.fieldsStream.readVInt();
            if (bitsPerLength == 0) {
                length = this.fieldsStream.readVInt();
                offset = (docID - docBase) * length;
                totalLength = chunkDocs * length;
            } else {
                int i;
                if (bitsPerStoredFields > 31) {
                    throw new CorruptIndexException("bitsPerLength=" + bitsPerLength + " (resource=" + this.fieldsStream + ")");
                }
                PackedInts.ReaderIterator it = PackedInts.getReaderIteratorNoHeader(this.fieldsStream, PackedInts.Format.PACKED, this.packedIntsVersion, chunkDocs, bitsPerLength, 1);
                int off = 0;
                for (i = 0; i < docID - docBase; ++i) {
                    off = (int)((long)off + it.next());
                }
                offset = off;
                length = (int)it.next();
                off += length;
                for (i = docID - docBase + 1; i < chunkDocs; ++i) {
                    off = (int)((long)off + it.next());
                }
                totalLength = off;
            }
        }
        if (length == 0 != (numStoredFields == 0)) {
            throw new CorruptIndexException("length=" + length + ", numStoredFields=" + numStoredFields + " (resource=" + this.fieldsStream + ")");
        }
        if (numStoredFields == 0) {
            return;
        }
        BytesRef bytes = totalLength <= 32768 ? this.bytes : new BytesRef();
        this.decompressor.decompress(this.fieldsStream, totalLength, offset, length, bytes);
        assert (bytes.length == length);
        ByteArrayDataInput documentInput = new ByteArrayDataInput(bytes.bytes, bytes.offset, bytes.length);
        block7: for (int fieldIDX = 0; fieldIDX < numStoredFields; ++fieldIDX) {
            long infoAndBits = documentInput.readVLong();
            int fieldNumber = (int)(infoAndBits >>> CompressingStoredFieldsWriter.TYPE_BITS);
            FieldInfo fieldInfo = this.fieldInfos.fieldInfo(fieldNumber);
            int bits = (int)(infoAndBits & (long)CompressingStoredFieldsWriter.TYPE_MASK);
            assert (bits <= 5) : "bits=" + Integer.toHexString(bits);
            switch (visitor.needsField(fieldInfo)) {
                case YES: {
                    CompressingStoredFieldsReader.readField(documentInput, visitor, fieldInfo, bits);
                    assert (documentInput.getPosition() <= bytes.offset + bytes.length) : documentInput.getPosition() + " " + bytes.offset + bytes.length;
                    continue block7;
                }
                case NO: {
                    CompressingStoredFieldsReader.skipField(documentInput, bits);
                    assert (documentInput.getPosition() <= bytes.offset + bytes.length) : documentInput.getPosition() + " " + bytes.offset + bytes.length;
                    continue block7;
                }
                case STOP: {
                    return;
                }
            }
        }
        assert (documentInput.getPosition() == bytes.offset + bytes.length) : documentInput.getPosition() + " " + bytes.offset + " " + bytes.length;
    }

    @Override
    public StoredFieldsReader clone() {
        this.ensureOpen();
        return new CompressingStoredFieldsReader(this);
    }

    CompressionMode getCompressionMode() {
        return this.compressionMode;
    }

    ChunkIterator chunkIterator(int startDocID) throws IOException {
        this.ensureOpen();
        this.fieldsStream.seek(this.indexReader.getStartPointer(startDocID));
        return new ChunkIterator();
    }

    final class ChunkIterator {
        BytesRef bytes = new BytesRef();
        int docBase = -1;
        int chunkDocs;
        int[] numStoredFields = new int[1];
        int[] lengths = new int[1];

        private ChunkIterator() {
        }

        int chunkSize() {
            int sum = 0;
            for (int i = 0; i < this.chunkDocs; ++i) {
                sum += this.lengths[i];
            }
            return sum;
        }

        void next(int doc) throws IOException {
            assert (doc >= this.docBase + this.chunkDocs) : doc + " " + this.docBase + " " + this.chunkDocs;
            CompressingStoredFieldsReader.this.fieldsStream.seek(CompressingStoredFieldsReader.this.indexReader.getStartPointer(doc));
            int docBase = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
            int chunkDocs = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
            if (docBase < this.docBase + this.chunkDocs || docBase + chunkDocs > CompressingStoredFieldsReader.this.numDocs) {
                throw new CorruptIndexException("Corrupted: current docBase=" + this.docBase + ", current numDocs=" + this.chunkDocs + ", new docBase=" + docBase + ", new numDocs=" + chunkDocs + " (resource=" + CompressingStoredFieldsReader.this.fieldsStream + ")");
            }
            this.docBase = docBase;
            this.chunkDocs = chunkDocs;
            if (chunkDocs > this.numStoredFields.length) {
                int newLength = ArrayUtil.oversize(chunkDocs, 4);
                this.numStoredFields = new int[newLength];
                this.lengths = new int[newLength];
            }
            if (chunkDocs == 1) {
                this.numStoredFields[0] = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
                this.lengths[0] = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
            } else {
                int bitsPerStoredFields = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
                if (bitsPerStoredFields == 0) {
                    Arrays.fill(this.numStoredFields, 0, chunkDocs, CompressingStoredFieldsReader.this.fieldsStream.readVInt());
                } else {
                    if (bitsPerStoredFields > 31) {
                        throw new CorruptIndexException("bitsPerStoredFields=" + bitsPerStoredFields + " (resource=" + CompressingStoredFieldsReader.this.fieldsStream + ")");
                    }
                    PackedInts.ReaderIterator it = PackedInts.getReaderIteratorNoHeader(CompressingStoredFieldsReader.this.fieldsStream, PackedInts.Format.PACKED, CompressingStoredFieldsReader.this.packedIntsVersion, chunkDocs, bitsPerStoredFields, 1);
                    for (int i = 0; i < chunkDocs; ++i) {
                        this.numStoredFields[i] = (int)it.next();
                    }
                }
                int bitsPerLength = CompressingStoredFieldsReader.this.fieldsStream.readVInt();
                if (bitsPerLength == 0) {
                    Arrays.fill(this.lengths, 0, chunkDocs, CompressingStoredFieldsReader.this.fieldsStream.readVInt());
                } else {
                    if (bitsPerLength > 31) {
                        throw new CorruptIndexException("bitsPerLength=" + bitsPerLength);
                    }
                    PackedInts.ReaderIterator it = PackedInts.getReaderIteratorNoHeader(CompressingStoredFieldsReader.this.fieldsStream, PackedInts.Format.PACKED, CompressingStoredFieldsReader.this.packedIntsVersion, chunkDocs, bitsPerLength, 1);
                    for (int i = 0; i < chunkDocs; ++i) {
                        this.lengths[i] = (int)it.next();
                    }
                }
            }
        }

        void decompress() throws IOException {
            int chunkSize = this.chunkSize();
            CompressingStoredFieldsReader.this.decompressor.decompress(CompressingStoredFieldsReader.this.fieldsStream, chunkSize, 0, chunkSize, this.bytes);
            if (this.bytes.length != chunkSize) {
                throw new CorruptIndexException("Corrupted: expected chunk size = " + this.chunkSize() + ", got " + this.bytes.length + " (resource=" + CompressingStoredFieldsReader.this.fieldsStream + ")");
            }
        }

        void copyCompressedData(DataOutput out) throws IOException {
            long chunkEnd = this.docBase + this.chunkDocs == CompressingStoredFieldsReader.this.numDocs ? CompressingStoredFieldsReader.this.fieldsStream.length() : CompressingStoredFieldsReader.this.indexReader.getStartPointer(this.docBase + this.chunkDocs);
            out.copyBytes(CompressingStoredFieldsReader.this.fieldsStream, chunkEnd - CompressingStoredFieldsReader.this.fieldsStream.getFilePointer());
        }
    }
}

