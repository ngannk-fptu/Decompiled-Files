/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.document.AbstractField;
import com.atlassian.lucene36.document.CompressionTools;
import com.atlassian.lucene36.document.Document;
import com.atlassian.lucene36.document.Field;
import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.document.FieldSelectorResult;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.document.NumericField;
import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.FieldReaderException;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.index.IndexFormatTooNewException;
import com.atlassian.lucene36.store.AlreadyClosedException;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.util.CloseableThreadLocal;
import com.atlassian.lucene36.util.IOUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.zip.DataFormatException;

final class FieldsReader
implements Cloneable,
Closeable {
    private final FieldInfos fieldInfos;
    private final IndexInput cloneableFieldsStream;
    private final IndexInput fieldsStream;
    private final IndexInput cloneableIndexStream;
    private final IndexInput indexStream;
    private int numTotalDocs;
    private int size;
    private boolean closed;
    private final int format;
    private final int formatSize;
    private int docStoreOffset;
    private CloseableThreadLocal<IndexInput> fieldsStreamTL = new CloseableThreadLocal();
    private boolean isOriginal = false;

    public Object clone() {
        this.ensureOpen();
        return new FieldsReader(this.fieldInfos, this.numTotalDocs, this.size, this.format, this.formatSize, this.docStoreOffset, this.cloneableFieldsStream, this.cloneableIndexStream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String detectCodeVersion(Directory dir, String segment) throws IOException {
        IndexInput idxStream = dir.openInput(IndexFileNames.segmentFileName(segment, "fdx"), 1024);
        try {
            int format = idxStream.readInt();
            if (format < 2) {
                String string = "2.x";
                return string;
            }
            String string = "3.0";
            return string;
        }
        finally {
            idxStream.close();
        }
    }

    private FieldsReader(FieldInfos fieldInfos, int numTotalDocs, int size, int format, int formatSize, int docStoreOffset, IndexInput cloneableFieldsStream, IndexInput cloneableIndexStream) {
        this.fieldInfos = fieldInfos;
        this.numTotalDocs = numTotalDocs;
        this.size = size;
        this.format = format;
        this.formatSize = formatSize;
        this.docStoreOffset = docStoreOffset;
        this.cloneableFieldsStream = cloneableFieldsStream;
        this.cloneableIndexStream = cloneableIndexStream;
        this.fieldsStream = (IndexInput)cloneableFieldsStream.clone();
        this.indexStream = (IndexInput)cloneableIndexStream.clone();
    }

    FieldsReader(Directory d, String segment, FieldInfos fn) throws IOException {
        this(d, segment, fn, 1024, -1, 0);
    }

    FieldsReader(Directory d, String segment, FieldInfos fn, int readBufferSize) throws IOException {
        this(d, segment, fn, readBufferSize, -1, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    FieldsReader(Directory d, String segment, FieldInfos fn, int readBufferSize, int docStoreOffset, int size) throws IOException {
        boolean success = false;
        this.isOriginal = true;
        try {
            this.fieldInfos = fn;
            this.cloneableFieldsStream = d.openInput(IndexFileNames.segmentFileName(segment, "fdt"), readBufferSize);
            String indexStreamFN = IndexFileNames.segmentFileName(segment, "fdx");
            this.cloneableIndexStream = d.openInput(indexStreamFN, readBufferSize);
            int firstInt = this.cloneableIndexStream.readInt();
            this.format = firstInt == 0 ? 0 : firstInt;
            if (this.format > 3) {
                throw new IndexFormatTooNewException(this.cloneableIndexStream, this.format, 0, 3);
            }
            this.formatSize = this.format > 0 ? 4 : 0;
            if (this.format < 1) {
                this.cloneableFieldsStream.setModifiedUTF8StringsMode();
            }
            this.fieldsStream = (IndexInput)this.cloneableFieldsStream.clone();
            long indexSize = this.cloneableIndexStream.length() - (long)this.formatSize;
            if (docStoreOffset != -1) {
                this.docStoreOffset = docStoreOffset;
                this.size = size;
                assert ((int)(indexSize / 8L) >= size + this.docStoreOffset) : "indexSize=" + indexSize + " size=" + size + " docStoreOffset=" + docStoreOffset;
            } else {
                this.docStoreOffset = 0;
                this.size = (int)(indexSize >> 3);
            }
            this.indexStream = (IndexInput)this.cloneableIndexStream.clone();
            this.numTotalDocs = (int)(indexSize >> 3);
            success = true;
        }
        finally {
            if (!success) {
                this.close();
            }
        }
    }

    private void ensureOpen() throws AlreadyClosedException {
        if (this.closed) {
            throw new AlreadyClosedException("this FieldsReader is closed");
        }
    }

    public final void close() throws IOException {
        if (!this.closed) {
            if (this.isOriginal) {
                IOUtils.close(this.fieldsStream, this.indexStream, this.fieldsStreamTL, this.cloneableFieldsStream, this.cloneableIndexStream);
            } else {
                IOUtils.close(this.fieldsStream, this.indexStream, this.fieldsStreamTL);
            }
            this.closed = true;
        }
    }

    final int size() {
        return this.size;
    }

    private final void seekIndex(int docID) throws IOException {
        this.indexStream.seek((long)this.formatSize + (long)(docID + this.docStoreOffset) * 8L);
    }

    boolean canReadRawDocs() {
        return this.format >= 2;
    }

    final Document doc(int n, FieldSelector fieldSelector) throws CorruptIndexException, IOException {
        this.seekIndex(n);
        long position = this.indexStream.readLong();
        this.fieldsStream.seek(position);
        Document doc = new Document();
        int numFields = this.fieldsStream.readVInt();
        block8: for (int i = 0; i < numFields; ++i) {
            boolean compressed;
            int fieldNumber = this.fieldsStream.readVInt();
            FieldInfo fi = this.fieldInfos.fieldInfo(fieldNumber);
            FieldSelectorResult acceptField = fieldSelector == null ? FieldSelectorResult.LOAD : fieldSelector.accept(fi.name);
            int bits = this.fieldsStream.readByte() & 0xFF;
            assert (bits <= 63) : "bits=" + Integer.toHexString(bits);
            boolean bl = compressed = (bits & 4) != 0;
            assert (!compressed || this.format < 2) : "compressed fields are only allowed in indexes of version <= 2.9";
            boolean tokenize = (bits & 1) != 0;
            boolean binary = (bits & 2) != 0;
            int numeric = bits & 0x38;
            switch (acceptField) {
                case LOAD: {
                    this.addField(doc, fi, binary, compressed, tokenize, numeric);
                    continue block8;
                }
                case LOAD_AND_BREAK: {
                    this.addField(doc, fi, binary, compressed, tokenize, numeric);
                    break block8;
                }
                case LAZY_LOAD: {
                    this.addFieldLazy(doc, fi, binary, compressed, tokenize, true, numeric);
                    continue block8;
                }
                case LATENT: {
                    this.addFieldLazy(doc, fi, binary, compressed, tokenize, false, numeric);
                    continue block8;
                }
                case SIZE: {
                    this.skipFieldBytes(binary, compressed, this.addFieldSize(doc, fi, binary, compressed, numeric));
                    continue block8;
                }
                case SIZE_AND_BREAK: {
                    this.addFieldSize(doc, fi, binary, compressed, numeric);
                    break block8;
                }
                default: {
                    this.skipField(binary, compressed, numeric);
                }
            }
        }
        return doc;
    }

    final IndexInput rawDocs(int[] lengths, int startDocID, int numDocs) throws IOException {
        long startOffset;
        this.seekIndex(startDocID);
        long lastOffset = startOffset = this.indexStream.readLong();
        int count = 0;
        while (count < numDocs) {
            int docID = this.docStoreOffset + startDocID + count + 1;
            assert (docID <= this.numTotalDocs);
            long offset = docID < this.numTotalDocs ? this.indexStream.readLong() : this.fieldsStream.length();
            lengths[count++] = (int)(offset - lastOffset);
            lastOffset = offset;
        }
        this.fieldsStream.seek(startOffset);
        return this.fieldsStream;
    }

    private void skipField(boolean binary, boolean compressed, int numeric) throws IOException {
        int numBytes;
        switch (numeric) {
            case 0: {
                numBytes = this.fieldsStream.readVInt();
                break;
            }
            case 8: 
            case 24: {
                numBytes = 4;
                break;
            }
            case 16: 
            case 32: {
                numBytes = 8;
                break;
            }
            default: {
                throw new CorruptIndexException("Invalid numeric type: " + Integer.toHexString(numeric));
            }
        }
        this.skipFieldBytes(binary, compressed, numBytes);
    }

    private void skipFieldBytes(boolean binary, boolean compressed, int toRead) throws IOException {
        if (this.format >= 1 || binary || compressed) {
            this.fieldsStream.seek(this.fieldsStream.getFilePointer() + (long)toRead);
        } else {
            this.fieldsStream.skipChars(toRead);
        }
    }

    private NumericField loadNumericField(FieldInfo fi, int numeric) throws IOException {
        assert (numeric != 0);
        switch (numeric) {
            case 8: {
                return new NumericField(fi.name, Field.Store.YES, fi.isIndexed).setIntValue(this.fieldsStream.readInt());
            }
            case 16: {
                return new NumericField(fi.name, Field.Store.YES, fi.isIndexed).setLongValue(this.fieldsStream.readLong());
            }
            case 24: {
                return new NumericField(fi.name, Field.Store.YES, fi.isIndexed).setFloatValue(Float.intBitsToFloat(this.fieldsStream.readInt()));
            }
            case 32: {
                return new NumericField(fi.name, Field.Store.YES, fi.isIndexed).setDoubleValue(Double.longBitsToDouble(this.fieldsStream.readLong()));
            }
        }
        throw new CorruptIndexException("Invalid numeric type: " + Integer.toHexString(numeric));
    }

    private void addFieldLazy(Document doc, FieldInfo fi, boolean binary, boolean compressed, boolean tokenize, boolean cacheResult, int numeric) throws IOException {
        AbstractField f;
        if (binary) {
            int toRead = this.fieldsStream.readVInt();
            long pointer = this.fieldsStream.getFilePointer();
            f = new LazyField(fi.name, Field.Store.YES, toRead, pointer, binary, compressed, cacheResult);
            this.fieldsStream.seek(pointer + (long)toRead);
        } else if (numeric != 0) {
            f = this.loadNumericField(fi, numeric);
        } else {
            Field.Store store = Field.Store.YES;
            Field.Index index = Field.Index.toIndex(fi.isIndexed, tokenize);
            Field.TermVector termVector = Field.TermVector.toTermVector(fi.storeTermVector, false, false);
            if (compressed) {
                int toRead = this.fieldsStream.readVInt();
                long pointer = this.fieldsStream.getFilePointer();
                f = new LazyField(fi.name, store, toRead, pointer, binary, compressed, cacheResult);
                this.fieldsStream.seek(pointer + (long)toRead);
            } else {
                int length = this.fieldsStream.readVInt();
                long pointer = this.fieldsStream.getFilePointer();
                if (this.format >= 1) {
                    this.fieldsStream.seek(pointer + (long)length);
                } else {
                    this.fieldsStream.skipChars(length);
                }
                f = new LazyField(fi.name, store, index, termVector, length, pointer, binary, compressed, cacheResult);
            }
        }
        f.setOmitNorms(fi.omitNorms);
        f.setIndexOptions(fi.indexOptions);
        doc.add(f);
    }

    private void addField(Document doc, FieldInfo fi, boolean binary, boolean compressed, boolean tokenize, int numeric) throws CorruptIndexException, IOException {
        AbstractField f;
        if (binary) {
            int toRead = this.fieldsStream.readVInt();
            byte[] b = new byte[toRead];
            this.fieldsStream.readBytes(b, 0, b.length);
            f = compressed ? new Field(fi.name, this.uncompress(b)) : new Field(fi.name, b);
        } else if (numeric != 0) {
            f = this.loadNumericField(fi, numeric);
        } else {
            Field.Store store = Field.Store.YES;
            Field.Index index = Field.Index.toIndex(fi.isIndexed, tokenize);
            Field.TermVector termVector = Field.TermVector.toTermVector(fi.storeTermVector, false, false);
            if (compressed) {
                int toRead = this.fieldsStream.readVInt();
                byte[] b = new byte[toRead];
                this.fieldsStream.readBytes(b, 0, b.length);
                f = new Field(fi.name, false, new String(this.uncompress(b), "UTF-8"), store, index, termVector);
            } else {
                f = new Field(fi.name, false, this.fieldsStream.readString(), store, index, termVector);
            }
        }
        f.setIndexOptions(fi.indexOptions);
        f.setOmitNorms(fi.omitNorms);
        doc.add(f);
    }

    private int addFieldSize(Document doc, FieldInfo fi, boolean binary, boolean compressed, int numeric) throws IOException {
        int bytesize;
        int size;
        switch (numeric) {
            case 0: {
                size = this.fieldsStream.readVInt();
                bytesize = binary || compressed ? size : 2 * size;
                break;
            }
            case 8: 
            case 24: {
                bytesize = 4;
                size = 4;
                break;
            }
            case 16: 
            case 32: {
                bytesize = 8;
                size = 8;
                break;
            }
            default: {
                throw new CorruptIndexException("Invalid numeric type: " + Integer.toHexString(numeric));
            }
        }
        byte[] sizebytes = new byte[]{(byte)(bytesize >>> 24), (byte)(bytesize >>> 16), (byte)(bytesize >>> 8), (byte)bytesize};
        doc.add(new Field(fi.name, sizebytes));
        return size;
    }

    private byte[] uncompress(byte[] b) throws CorruptIndexException {
        try {
            return CompressionTools.decompress(b);
        }
        catch (DataFormatException e) {
            CorruptIndexException newException = new CorruptIndexException("field data are in wrong format: " + e.toString());
            newException.initCause(e);
            throw newException;
        }
    }

    private class LazyField
    extends AbstractField
    implements Fieldable {
        private int toRead;
        private long pointer;
        @Deprecated
        private boolean isCompressed;
        private boolean cacheResult;

        public LazyField(String name, Field.Store store, int toRead, long pointer, boolean isBinary, boolean isCompressed, boolean cacheResult) {
            super(name, store, Field.Index.NO, Field.TermVector.NO);
            this.toRead = toRead;
            this.pointer = pointer;
            this.isBinary = isBinary;
            this.cacheResult = cacheResult;
            if (isBinary) {
                this.binaryLength = toRead;
            }
            this.lazy = true;
            this.isCompressed = isCompressed;
        }

        public LazyField(String name, Field.Store store, Field.Index index, Field.TermVector termVector, int toRead, long pointer, boolean isBinary, boolean isCompressed, boolean cacheResult) {
            super(name, store, index, termVector);
            this.toRead = toRead;
            this.pointer = pointer;
            this.isBinary = isBinary;
            this.cacheResult = cacheResult;
            if (isBinary) {
                this.binaryLength = toRead;
            }
            this.lazy = true;
            this.isCompressed = isCompressed;
        }

        private IndexInput getFieldStream() {
            IndexInput localFieldsStream = (IndexInput)FieldsReader.this.fieldsStreamTL.get();
            if (localFieldsStream == null) {
                localFieldsStream = (IndexInput)FieldsReader.this.cloneableFieldsStream.clone();
                FieldsReader.this.fieldsStreamTL.set(localFieldsStream);
            }
            return localFieldsStream;
        }

        public Reader readerValue() {
            FieldsReader.this.ensureOpen();
            return null;
        }

        public TokenStream tokenStreamValue() {
            FieldsReader.this.ensureOpen();
            return null;
        }

        public String stringValue() {
            FieldsReader.this.ensureOpen();
            if (this.isBinary) {
                return null;
            }
            if (this.fieldsData == null) {
                String value;
                IndexInput localFieldsStream = this.getFieldStream();
                try {
                    localFieldsStream.seek(this.pointer);
                    if (this.isCompressed) {
                        byte[] b = new byte[this.toRead];
                        localFieldsStream.readBytes(b, 0, b.length);
                        value = new String(FieldsReader.this.uncompress(b), "UTF-8");
                    } else if (FieldsReader.this.format >= 1) {
                        byte[] bytes = new byte[this.toRead];
                        localFieldsStream.readBytes(bytes, 0, this.toRead);
                        value = new String(bytes, "UTF-8");
                    } else {
                        char[] chars = new char[this.toRead];
                        localFieldsStream.readChars(chars, 0, this.toRead);
                        value = new String(chars);
                    }
                }
                catch (IOException e) {
                    throw new FieldReaderException(e);
                }
                if (this.cacheResult) {
                    this.fieldsData = value;
                }
                return value;
            }
            return (String)this.fieldsData;
        }

        public byte[] getBinaryValue(byte[] result) {
            FieldsReader.this.ensureOpen();
            if (this.isBinary) {
                if (this.fieldsData == null) {
                    byte[] value;
                    byte[] b = result == null || result.length < this.toRead ? new byte[this.toRead] : result;
                    IndexInput localFieldsStream = this.getFieldStream();
                    try {
                        localFieldsStream.seek(this.pointer);
                        localFieldsStream.readBytes(b, 0, this.toRead);
                        value = this.isCompressed ? FieldsReader.this.uncompress(b) : b;
                    }
                    catch (IOException e) {
                        throw new FieldReaderException(e);
                    }
                    this.binaryOffset = 0;
                    this.binaryLength = this.toRead;
                    if (this.cacheResult) {
                        this.fieldsData = value;
                    }
                    return value;
                }
                return (byte[])this.fieldsData;
            }
            return null;
        }
    }
}

