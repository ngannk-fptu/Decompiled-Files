/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.lucene40.Lucene40FieldInfosReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.store.CompoundFileDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.PagedBytes;
import org.apache.lucene.util.packed.PackedInts;

@Deprecated
final class Lucene40DocValuesReader
extends DocValuesProducer {
    private final Directory dir;
    private final SegmentReadState state;
    private final String legacyKey;
    private static final String segmentSuffix = "dv";
    private final Map<Integer, NumericDocValues> numericInstances = new HashMap<Integer, NumericDocValues>();
    private final Map<Integer, BinaryDocValues> binaryInstances = new HashMap<Integer, BinaryDocValues>();
    private final Map<Integer, SortedDocValues> sortedInstances = new HashMap<Integer, SortedDocValues>();

    Lucene40DocValuesReader(SegmentReadState state, String filename, String legacyKey) throws IOException {
        this.state = state;
        this.legacyKey = legacyKey;
        this.dir = new CompoundFileDirectory(state.directory, filename, state.context, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized NumericDocValues getNumeric(FieldInfo field) throws IOException {
        NumericDocValues instance;
        block15: {
            block16: {
                IndexInput input;
                block14: {
                    instance = this.numericInstances.get(field.number);
                    if (instance != null) break block15;
                    String fileName = IndexFileNames.segmentFileName(this.state.segmentInfo.name + "_" + Integer.toString(field.number), segmentSuffix, "dat");
                    input = this.dir.openInput(fileName, this.state.context);
                    boolean success = false;
                    try {
                        switch (Lucene40FieldInfosReader.LegacyDocValuesType.valueOf(field.getAttribute(this.legacyKey))) {
                            case VAR_INTS: {
                                instance = this.loadVarIntsField(field, input);
                                break;
                            }
                            case FIXED_INTS_8: {
                                instance = this.loadByteField(field, input);
                                break;
                            }
                            case FIXED_INTS_16: {
                                instance = this.loadShortField(field, input);
                                break;
                            }
                            case FIXED_INTS_32: {
                                instance = this.loadIntField(field, input);
                                break;
                            }
                            case FIXED_INTS_64: {
                                instance = this.loadLongField(field, input);
                                break;
                            }
                            case FLOAT_32: {
                                instance = this.loadFloatField(field, input);
                                break;
                            }
                            case FLOAT_64: {
                                instance = this.loadDoubleField(field, input);
                                break;
                            }
                            default: {
                                throw new AssertionError();
                            }
                        }
                        if (input.getFilePointer() != input.length()) {
                            throw new CorruptIndexException("did not read all bytes from file \"" + fileName + "\": read " + input.getFilePointer() + " vs size " + input.length() + " (resource: " + input + ")");
                        }
                        success = true;
                        if (!success) break block14;
                    }
                    catch (Throwable throwable) {
                        if (success) {
                            IOUtils.close(input);
                        } else {
                            IOUtils.closeWhileHandlingException(input);
                        }
                        throw throwable;
                    }
                    IOUtils.close(input);
                    break block16;
                }
                IOUtils.closeWhileHandlingException(input);
            }
            this.numericInstances.put(field.number, instance);
        }
        return instance;
    }

    private NumericDocValues loadVarIntsField(FieldInfo field, IndexInput input) throws IOException {
        CodecUtil.checkHeader(input, "PackedInts", 0, 0);
        byte header = input.readByte();
        if (header == 1) {
            int maxDoc = this.state.segmentInfo.getDocCount();
            final long[] values = new long[maxDoc];
            for (int i = 0; i < values.length; ++i) {
                values[i] = input.readLong();
            }
            return new NumericDocValues(){

                @Override
                public long get(int docID) {
                    return values[docID];
                }
            };
        }
        if (header == 0) {
            final long minValue = input.readLong();
            final long defaultValue = input.readLong();
            final PackedInts.Reader reader = PackedInts.getReader(input);
            return new NumericDocValues(){

                @Override
                public long get(int docID) {
                    long value = reader.get(docID);
                    if (value == defaultValue) {
                        return 0L;
                    }
                    return minValue + value;
                }
            };
        }
        throw new CorruptIndexException("invalid VAR_INTS header byte: " + header + " (resource=" + input + ")");
    }

    private NumericDocValues loadByteField(FieldInfo field, IndexInput input) throws IOException {
        CodecUtil.checkHeader(input, "Ints", 0, 0);
        int valueSize = input.readInt();
        if (valueSize != 1) {
            throw new CorruptIndexException("invalid valueSize: " + valueSize);
        }
        int maxDoc = this.state.segmentInfo.getDocCount();
        final byte[] values = new byte[maxDoc];
        input.readBytes(values, 0, values.length);
        return new NumericDocValues(){

            @Override
            public long get(int docID) {
                return values[docID];
            }
        };
    }

    private NumericDocValues loadShortField(FieldInfo field, IndexInput input) throws IOException {
        CodecUtil.checkHeader(input, "Ints", 0, 0);
        int valueSize = input.readInt();
        if (valueSize != 2) {
            throw new CorruptIndexException("invalid valueSize: " + valueSize);
        }
        int maxDoc = this.state.segmentInfo.getDocCount();
        final short[] values = new short[maxDoc];
        for (int i = 0; i < values.length; ++i) {
            values[i] = input.readShort();
        }
        return new NumericDocValues(){

            @Override
            public long get(int docID) {
                return values[docID];
            }
        };
    }

    private NumericDocValues loadIntField(FieldInfo field, IndexInput input) throws IOException {
        CodecUtil.checkHeader(input, "Ints", 0, 0);
        int valueSize = input.readInt();
        if (valueSize != 4) {
            throw new CorruptIndexException("invalid valueSize: " + valueSize);
        }
        int maxDoc = this.state.segmentInfo.getDocCount();
        final int[] values = new int[maxDoc];
        for (int i = 0; i < values.length; ++i) {
            values[i] = input.readInt();
        }
        return new NumericDocValues(){

            @Override
            public long get(int docID) {
                return values[docID];
            }
        };
    }

    private NumericDocValues loadLongField(FieldInfo field, IndexInput input) throws IOException {
        CodecUtil.checkHeader(input, "Ints", 0, 0);
        int valueSize = input.readInt();
        if (valueSize != 8) {
            throw new CorruptIndexException("invalid valueSize: " + valueSize);
        }
        int maxDoc = this.state.segmentInfo.getDocCount();
        final long[] values = new long[maxDoc];
        for (int i = 0; i < values.length; ++i) {
            values[i] = input.readLong();
        }
        return new NumericDocValues(){

            @Override
            public long get(int docID) {
                return values[docID];
            }
        };
    }

    private NumericDocValues loadFloatField(FieldInfo field, IndexInput input) throws IOException {
        CodecUtil.checkHeader(input, "Floats", 0, 0);
        int valueSize = input.readInt();
        if (valueSize != 4) {
            throw new CorruptIndexException("invalid valueSize: " + valueSize);
        }
        int maxDoc = this.state.segmentInfo.getDocCount();
        final int[] values = new int[maxDoc];
        for (int i = 0; i < values.length; ++i) {
            values[i] = input.readInt();
        }
        return new NumericDocValues(){

            @Override
            public long get(int docID) {
                return values[docID];
            }
        };
    }

    private NumericDocValues loadDoubleField(FieldInfo field, IndexInput input) throws IOException {
        CodecUtil.checkHeader(input, "Floats", 0, 0);
        int valueSize = input.readInt();
        if (valueSize != 8) {
            throw new CorruptIndexException("invalid valueSize: " + valueSize);
        }
        int maxDoc = this.state.segmentInfo.getDocCount();
        final long[] values = new long[maxDoc];
        for (int i = 0; i < values.length; ++i) {
            values[i] = input.readLong();
        }
        return new NumericDocValues(){

            @Override
            public long get(int docID) {
                return values[docID];
            }
        };
    }

    @Override
    public synchronized BinaryDocValues getBinary(FieldInfo field) throws IOException {
        BinaryDocValues instance = this.binaryInstances.get(field.number);
        if (instance == null) {
            switch (Lucene40FieldInfosReader.LegacyDocValuesType.valueOf(field.getAttribute(this.legacyKey))) {
                case BYTES_FIXED_STRAIGHT: {
                    instance = this.loadBytesFixedStraight(field);
                    break;
                }
                case BYTES_VAR_STRAIGHT: {
                    instance = this.loadBytesVarStraight(field);
                    break;
                }
                case BYTES_FIXED_DEREF: {
                    instance = this.loadBytesFixedDeref(field);
                    break;
                }
                case BYTES_VAR_DEREF: {
                    instance = this.loadBytesVarDeref(field);
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
            this.binaryInstances.put(field.number, instance);
        }
        return instance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BinaryDocValues loadBytesFixedStraight(FieldInfo field) throws IOException {
        BinaryDocValues binaryDocValues;
        block6: {
            IndexInput input;
            block5: {
                String fileName = IndexFileNames.segmentFileName(this.state.segmentInfo.name + "_" + Integer.toString(field.number), segmentSuffix, "dat");
                input = this.dir.openInput(fileName, this.state.context);
                boolean success = false;
                try {
                    CodecUtil.checkHeader(input, "FixedStraightBytes", 0, 0);
                    final int fixedLength = input.readInt();
                    PagedBytes bytes = new PagedBytes(16);
                    bytes.copy(input, (long)fixedLength * (long)this.state.segmentInfo.getDocCount());
                    final PagedBytes.Reader bytesReader = bytes.freeze(true);
                    if (input.getFilePointer() != input.length()) {
                        throw new CorruptIndexException("did not read all bytes from file \"" + fileName + "\": read " + input.getFilePointer() + " vs size " + input.length() + " (resource: " + input + ")");
                    }
                    success = true;
                    binaryDocValues = new BinaryDocValues(){

                        @Override
                        public void get(int docID, BytesRef result) {
                            bytesReader.fillSlice(result, (long)fixedLength * (long)docID, fixedLength);
                        }
                    };
                    if (!success) break block5;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(input);
                    } else {
                        IOUtils.closeWhileHandlingException(input);
                    }
                    throw throwable;
                }
                IOUtils.close(input);
                break block6;
            }
            IOUtils.closeWhileHandlingException(input);
        }
        return binaryDocValues;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BinaryDocValues loadBytesVarStraight(FieldInfo field) throws IOException {
        BinaryDocValues binaryDocValues;
        block7: {
            IndexInput index;
            IndexInput data;
            block6: {
                String dataName = IndexFileNames.segmentFileName(this.state.segmentInfo.name + "_" + Integer.toString(field.number), segmentSuffix, "dat");
                String indexName = IndexFileNames.segmentFileName(this.state.segmentInfo.name + "_" + Integer.toString(field.number), segmentSuffix, "idx");
                data = null;
                index = null;
                boolean success = false;
                try {
                    data = this.dir.openInput(dataName, this.state.context);
                    CodecUtil.checkHeader(data, "VarStraightBytesDat", 0, 0);
                    index = this.dir.openInput(indexName, this.state.context);
                    CodecUtil.checkHeader(index, "VarStraightBytesIdx", 0, 0);
                    long totalBytes = index.readVLong();
                    PagedBytes bytes = new PagedBytes(16);
                    bytes.copy(data, totalBytes);
                    final PagedBytes.Reader bytesReader = bytes.freeze(true);
                    final PackedInts.Reader reader = PackedInts.getReader(index);
                    if (data.getFilePointer() != data.length()) {
                        throw new CorruptIndexException("did not read all bytes from file \"" + dataName + "\": read " + data.getFilePointer() + " vs size " + data.length() + " (resource: " + data + ")");
                    }
                    if (index.getFilePointer() != index.length()) {
                        throw new CorruptIndexException("did not read all bytes from file \"" + indexName + "\": read " + index.getFilePointer() + " vs size " + index.length() + " (resource: " + index + ")");
                    }
                    success = true;
                    binaryDocValues = new BinaryDocValues(){

                        @Override
                        public void get(int docID, BytesRef result) {
                            long startAddress = reader.get(docID);
                            long endAddress = reader.get(docID + 1);
                            bytesReader.fillSlice(result, startAddress, (int)(endAddress - startAddress));
                        }
                    };
                    if (!success) break block6;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(data, index);
                    } else {
                        IOUtils.closeWhileHandlingException(data, index);
                    }
                    throw throwable;
                }
                IOUtils.close(data, index);
                break block7;
            }
            IOUtils.closeWhileHandlingException(data, index);
        }
        return binaryDocValues;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BinaryDocValues loadBytesFixedDeref(FieldInfo field) throws IOException {
        BinaryDocValues binaryDocValues;
        block7: {
            IndexInput index;
            IndexInput data;
            block6: {
                String dataName = IndexFileNames.segmentFileName(this.state.segmentInfo.name + "_" + Integer.toString(field.number), segmentSuffix, "dat");
                String indexName = IndexFileNames.segmentFileName(this.state.segmentInfo.name + "_" + Integer.toString(field.number), segmentSuffix, "idx");
                data = null;
                index = null;
                boolean success = false;
                try {
                    data = this.dir.openInput(dataName, this.state.context);
                    CodecUtil.checkHeader(data, "FixedDerefBytesDat", 0, 0);
                    index = this.dir.openInput(indexName, this.state.context);
                    CodecUtil.checkHeader(index, "FixedDerefBytesIdx", 0, 0);
                    final int fixedLength = data.readInt();
                    int valueCount = index.readInt();
                    PagedBytes bytes = new PagedBytes(16);
                    bytes.copy(data, (long)fixedLength * (long)valueCount);
                    final PagedBytes.Reader bytesReader = bytes.freeze(true);
                    final PackedInts.Reader reader = PackedInts.getReader(index);
                    if (data.getFilePointer() != data.length()) {
                        throw new CorruptIndexException("did not read all bytes from file \"" + dataName + "\": read " + data.getFilePointer() + " vs size " + data.length() + " (resource: " + data + ")");
                    }
                    if (index.getFilePointer() != index.length()) {
                        throw new CorruptIndexException("did not read all bytes from file \"" + indexName + "\": read " + index.getFilePointer() + " vs size " + index.length() + " (resource: " + index + ")");
                    }
                    success = true;
                    binaryDocValues = new BinaryDocValues(){

                        @Override
                        public void get(int docID, BytesRef result) {
                            long offset = (long)fixedLength * reader.get(docID);
                            bytesReader.fillSlice(result, offset, fixedLength);
                        }
                    };
                    if (!success) break block6;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(data, index);
                    } else {
                        IOUtils.closeWhileHandlingException(data, index);
                    }
                    throw throwable;
                }
                IOUtils.close(data, index);
                break block7;
            }
            IOUtils.closeWhileHandlingException(data, index);
        }
        return binaryDocValues;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BinaryDocValues loadBytesVarDeref(FieldInfo field) throws IOException {
        BinaryDocValues binaryDocValues;
        block7: {
            IndexInput index;
            IndexInput data;
            block6: {
                String dataName = IndexFileNames.segmentFileName(this.state.segmentInfo.name + "_" + Integer.toString(field.number), segmentSuffix, "dat");
                String indexName = IndexFileNames.segmentFileName(this.state.segmentInfo.name + "_" + Integer.toString(field.number), segmentSuffix, "idx");
                data = null;
                index = null;
                boolean success = false;
                try {
                    data = this.dir.openInput(dataName, this.state.context);
                    CodecUtil.checkHeader(data, "VarDerefBytesDat", 0, 0);
                    index = this.dir.openInput(indexName, this.state.context);
                    CodecUtil.checkHeader(index, "VarDerefBytesIdx", 0, 0);
                    long totalBytes = index.readLong();
                    PagedBytes bytes = new PagedBytes(16);
                    bytes.copy(data, totalBytes);
                    final PagedBytes.Reader bytesReader = bytes.freeze(true);
                    final PackedInts.Reader reader = PackedInts.getReader(index);
                    if (data.getFilePointer() != data.length()) {
                        throw new CorruptIndexException("did not read all bytes from file \"" + dataName + "\": read " + data.getFilePointer() + " vs size " + data.length() + " (resource: " + data + ")");
                    }
                    if (index.getFilePointer() != index.length()) {
                        throw new CorruptIndexException("did not read all bytes from file \"" + indexName + "\": read " + index.getFilePointer() + " vs size " + index.length() + " (resource: " + index + ")");
                    }
                    success = true;
                    binaryDocValues = new BinaryDocValues(){

                        @Override
                        public void get(int docID, BytesRef result) {
                            long startAddress = reader.get(docID);
                            BytesRef lengthBytes = new BytesRef();
                            bytesReader.fillSlice(lengthBytes, startAddress, 1);
                            byte code = lengthBytes.bytes[lengthBytes.offset];
                            if ((code & 0x80) == 0) {
                                bytesReader.fillSlice(result, startAddress + 1L, code);
                            } else {
                                bytesReader.fillSlice(lengthBytes, startAddress + 1L, 1);
                                int length = (code & 0x7F) << 8 | lengthBytes.bytes[lengthBytes.offset] & 0xFF;
                                bytesReader.fillSlice(result, startAddress + 2L, length);
                            }
                        }
                    };
                    if (!success) break block6;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(data, index);
                    } else {
                        IOUtils.closeWhileHandlingException(data, index);
                    }
                    throw throwable;
                }
                IOUtils.close(data, index);
                break block7;
            }
            IOUtils.closeWhileHandlingException(data, index);
        }
        return binaryDocValues;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized SortedDocValues getSorted(FieldInfo field) throws IOException {
        SortedDocValues instance;
        block11: {
            block12: {
                IndexInput index;
                IndexInput data;
                block10: {
                    instance = this.sortedInstances.get(field.number);
                    if (instance != null) break block11;
                    String dataName = IndexFileNames.segmentFileName(this.state.segmentInfo.name + "_" + Integer.toString(field.number), segmentSuffix, "dat");
                    String indexName = IndexFileNames.segmentFileName(this.state.segmentInfo.name + "_" + Integer.toString(field.number), segmentSuffix, "idx");
                    data = null;
                    index = null;
                    boolean success = false;
                    try {
                        data = this.dir.openInput(dataName, this.state.context);
                        index = this.dir.openInput(indexName, this.state.context);
                        switch (Lucene40FieldInfosReader.LegacyDocValuesType.valueOf(field.getAttribute(this.legacyKey))) {
                            case BYTES_FIXED_SORTED: {
                                instance = this.loadBytesFixedSorted(field, data, index);
                                break;
                            }
                            case BYTES_VAR_SORTED: {
                                instance = this.loadBytesVarSorted(field, data, index);
                                break;
                            }
                            default: {
                                throw new AssertionError();
                            }
                        }
                        if (data.getFilePointer() != data.length()) {
                            throw new CorruptIndexException("did not read all bytes from file \"" + dataName + "\": read " + data.getFilePointer() + " vs size " + data.length() + " (resource: " + data + ")");
                        }
                        if (index.getFilePointer() != index.length()) {
                            throw new CorruptIndexException("did not read all bytes from file \"" + indexName + "\": read " + index.getFilePointer() + " vs size " + index.length() + " (resource: " + index + ")");
                        }
                        success = true;
                        if (!success) break block10;
                    }
                    catch (Throwable throwable) {
                        if (success) {
                            IOUtils.close(data, index);
                        } else {
                            IOUtils.closeWhileHandlingException(data, index);
                        }
                        throw throwable;
                    }
                    IOUtils.close(data, index);
                    break block12;
                }
                IOUtils.closeWhileHandlingException(data, index);
            }
            this.sortedInstances.put(field.number, instance);
        }
        return instance;
    }

    private SortedDocValues loadBytesFixedSorted(FieldInfo field, IndexInput data, IndexInput index) throws IOException {
        CodecUtil.checkHeader(data, "FixedSortedBytesDat", 0, 0);
        CodecUtil.checkHeader(index, "FixedSortedBytesIdx", 0, 0);
        final int fixedLength = data.readInt();
        final int valueCount = index.readInt();
        PagedBytes bytes = new PagedBytes(16);
        bytes.copy(data, (long)fixedLength * (long)valueCount);
        final PagedBytes.Reader bytesReader = bytes.freeze(true);
        final PackedInts.Reader reader = PackedInts.getReader(index);
        return this.correctBuggyOrds(new SortedDocValues(){

            @Override
            public int getOrd(int docID) {
                return (int)reader.get(docID);
            }

            @Override
            public void lookupOrd(int ord, BytesRef result) {
                bytesReader.fillSlice(result, (long)fixedLength * (long)ord, fixedLength);
            }

            @Override
            public int getValueCount() {
                return valueCount;
            }
        });
    }

    private SortedDocValues loadBytesVarSorted(FieldInfo field, IndexInput data, IndexInput index) throws IOException {
        CodecUtil.checkHeader(data, "VarDerefBytesDat", 0, 0);
        CodecUtil.checkHeader(index, "VarDerefBytesIdx", 0, 0);
        long maxAddress = index.readLong();
        PagedBytes bytes = new PagedBytes(16);
        bytes.copy(data, maxAddress);
        final PagedBytes.Reader bytesReader = bytes.freeze(true);
        final PackedInts.Reader addressReader = PackedInts.getReader(index);
        final PackedInts.Reader ordsReader = PackedInts.getReader(index);
        final int valueCount = addressReader.size() - 1;
        return this.correctBuggyOrds(new SortedDocValues(){

            @Override
            public int getOrd(int docID) {
                return (int)ordsReader.get(docID);
            }

            @Override
            public void lookupOrd(int ord, BytesRef result) {
                long startAddress = addressReader.get(ord);
                long endAddress = addressReader.get(ord + 1);
                bytesReader.fillSlice(result, startAddress, (int)(endAddress - startAddress));
            }

            @Override
            public int getValueCount() {
                return valueCount;
            }
        });
    }

    private SortedDocValues correctBuggyOrds(final SortedDocValues in) {
        int maxDoc = this.state.segmentInfo.getDocCount();
        for (int i = 0; i < maxDoc; ++i) {
            if (in.getOrd(i) != 0) continue;
            return in;
        }
        return new SortedDocValues(){

            @Override
            public int getOrd(int docID) {
                return in.getOrd(docID) - 1;
            }

            @Override
            public void lookupOrd(int ord, BytesRef result) {
                in.lookupOrd(ord + 1, result);
            }

            @Override
            public int getValueCount() {
                return in.getValueCount() - 1;
            }
        };
    }

    @Override
    public SortedSetDocValues getSortedSet(FieldInfo field) throws IOException {
        throw new IllegalStateException("Lucene 4.0 does not support SortedSet: how did you pull this off?");
    }

    @Override
    public void close() throws IOException {
        this.dir.close();
    }
}

