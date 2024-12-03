/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene42;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.PagedBytes;
import org.apache.lucene.util.fst.BytesRefFSTEnum;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.packed.BlockPackedReader;
import org.apache.lucene.util.packed.MonotonicBlockPackedReader;
import org.apache.lucene.util.packed.PackedInts;

class Lucene42DocValuesProducer
extends DocValuesProducer {
    private final Map<Integer, NumericEntry> numerics;
    private final Map<Integer, BinaryEntry> binaries;
    private final Map<Integer, FSTEntry> fsts;
    private final IndexInput data;
    private final Map<Integer, NumericDocValues> numericInstances;
    private final Map<Integer, BinaryDocValues> binaryInstances;
    private final Map<Integer, FST<Long>> fstInstances;
    private final int maxDoc;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Lucene42DocValuesProducer(SegmentReadState state, String dataCodec, String dataExtension, String metaCodec, String metaExtension) throws IOException {
        int version;
        boolean success;
        block8: {
            IndexInput in;
            block7: {
                this.numericInstances = new HashMap<Integer, NumericDocValues>();
                this.binaryInstances = new HashMap<Integer, BinaryDocValues>();
                this.fstInstances = new HashMap<Integer, FST<Long>>();
                this.maxDoc = state.segmentInfo.getDocCount();
                String metaName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, metaExtension);
                in = state.directory.openInput(metaName, state.context);
                success = false;
                try {
                    version = CodecUtil.checkHeader(in, metaCodec, 0, 1);
                    this.numerics = new HashMap<Integer, NumericEntry>();
                    this.binaries = new HashMap<Integer, BinaryEntry>();
                    this.fsts = new HashMap<Integer, FSTEntry>();
                    this.readFields(in, state.fieldInfos);
                    success = true;
                    if (!success) break block7;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(in);
                        throw throwable;
                    } else {
                        IOUtils.closeWhileHandlingException(in);
                    }
                    throw throwable;
                }
                IOUtils.close(in);
                break block8;
            }
            IOUtils.closeWhileHandlingException(in);
        }
        String dataName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, dataExtension);
        this.data = state.directory.openInput(dataName, state.context);
        success = false;
        try {
            int version2 = CodecUtil.checkHeader(this.data, dataCodec, 0, 1);
            if (version != version2) {
                throw new CorruptIndexException("Format versions mismatch");
            }
            success = true;
            if (success) return;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(this.data);
            throw throwable;
        }
        IOUtils.closeWhileHandlingException(this.data);
    }

    private void readFields(IndexInput meta, FieldInfos infos) throws IOException {
        int fieldNumber = meta.readVInt();
        while (fieldNumber != -1) {
            Object entry;
            byte fieldType = meta.readByte();
            if (fieldType == 0) {
                entry = new NumericEntry();
                ((NumericEntry)entry).offset = meta.readLong();
                ((NumericEntry)entry).format = meta.readByte();
                switch (((NumericEntry)entry).format) {
                    case 0: 
                    case 1: 
                    case 2: 
                    case 3: {
                        break;
                    }
                    default: {
                        throw new CorruptIndexException("Unknown format: " + ((NumericEntry)entry).format + ", input=" + meta);
                    }
                }
                if (((NumericEntry)entry).format != 2) {
                    ((NumericEntry)entry).packedIntsVersion = meta.readVInt();
                }
                this.numerics.put(fieldNumber, (NumericEntry)entry);
            } else if (fieldType == 1) {
                entry = new BinaryEntry();
                ((BinaryEntry)entry).offset = meta.readLong();
                ((BinaryEntry)entry).numBytes = meta.readLong();
                ((BinaryEntry)entry).minLength = meta.readVInt();
                ((BinaryEntry)entry).maxLength = meta.readVInt();
                if (((BinaryEntry)entry).minLength != ((BinaryEntry)entry).maxLength) {
                    ((BinaryEntry)entry).packedIntsVersion = meta.readVInt();
                    ((BinaryEntry)entry).blockSize = meta.readVInt();
                }
                this.binaries.put(fieldNumber, (BinaryEntry)entry);
            } else if (fieldType == 2) {
                entry = new FSTEntry();
                ((FSTEntry)entry).offset = meta.readLong();
                ((FSTEntry)entry).numOrds = meta.readVLong();
                this.fsts.put(fieldNumber, (FSTEntry)entry);
            } else {
                throw new CorruptIndexException("invalid entry type: " + fieldType + ", input=" + meta);
            }
            fieldNumber = meta.readVInt();
        }
    }

    @Override
    public synchronized NumericDocValues getNumeric(FieldInfo field) throws IOException {
        NumericDocValues instance = this.numericInstances.get(field.number);
        if (instance == null) {
            instance = this.loadNumeric(field);
            this.numericInstances.put(field.number, instance);
        }
        return instance;
    }

    private NumericDocValues loadNumeric(FieldInfo field) throws IOException {
        NumericEntry entry = this.numerics.get(field.number);
        this.data.seek(entry.offset);
        switch (entry.format) {
            case 1: {
                int size = this.data.readVInt();
                if (size > 256) {
                    throw new CorruptIndexException("TABLE_COMPRESSED cannot have more than 256 distinct values, input=" + this.data);
                }
                final long[] decode = new long[size];
                for (int i = 0; i < decode.length; ++i) {
                    decode[i] = this.data.readLong();
                }
                int formatID = this.data.readVInt();
                int bitsPerValue = this.data.readVInt();
                final PackedInts.Reader ordsReader = PackedInts.getReaderNoHeader(this.data, PackedInts.Format.byId(formatID), entry.packedIntsVersion, this.maxDoc, bitsPerValue);
                return new NumericDocValues(){

                    @Override
                    public long get(int docID) {
                        return decode[(int)ordsReader.get(docID)];
                    }
                };
            }
            case 0: {
                int blockSize = this.data.readVInt();
                final BlockPackedReader reader = new BlockPackedReader(this.data, entry.packedIntsVersion, blockSize, this.maxDoc, false);
                return new NumericDocValues(){

                    @Override
                    public long get(int docID) {
                        return reader.get(docID);
                    }
                };
            }
            case 2: {
                final byte[] bytes = new byte[this.maxDoc];
                this.data.readBytes(bytes, 0, bytes.length);
                return new NumericDocValues(){

                    @Override
                    public long get(int docID) {
                        return bytes[docID];
                    }
                };
            }
            case 3: {
                final long min = this.data.readLong();
                final long mult = this.data.readLong();
                int quotientBlockSize = this.data.readVInt();
                final BlockPackedReader quotientReader = new BlockPackedReader(this.data, entry.packedIntsVersion, quotientBlockSize, this.maxDoc, false);
                return new NumericDocValues(){

                    @Override
                    public long get(int docID) {
                        return min + mult * quotientReader.get(docID);
                    }
                };
            }
        }
        throw new AssertionError();
    }

    @Override
    public synchronized BinaryDocValues getBinary(FieldInfo field) throws IOException {
        BinaryDocValues instance = this.binaryInstances.get(field.number);
        if (instance == null) {
            instance = this.loadBinary(field);
            this.binaryInstances.put(field.number, instance);
        }
        return instance;
    }

    private BinaryDocValues loadBinary(FieldInfo field) throws IOException {
        BinaryEntry entry = this.binaries.get(field.number);
        this.data.seek(entry.offset);
        PagedBytes bytes = new PagedBytes(16);
        bytes.copy(this.data, entry.numBytes);
        final PagedBytes.Reader bytesReader = bytes.freeze(true);
        if (entry.minLength == entry.maxLength) {
            final int fixedLength = entry.minLength;
            return new BinaryDocValues(){

                @Override
                public void get(int docID, BytesRef result) {
                    bytesReader.fillSlice(result, (long)fixedLength * (long)docID, fixedLength);
                }
            };
        }
        final MonotonicBlockPackedReader addresses = new MonotonicBlockPackedReader(this.data, entry.packedIntsVersion, entry.blockSize, this.maxDoc, false);
        return new BinaryDocValues(){

            @Override
            public void get(int docID, BytesRef result) {
                long startAddress = docID == 0 ? 0L : addresses.get(docID - 1);
                long endAddress = addresses.get(docID);
                bytesReader.fillSlice(result, startAddress, (int)(endAddress - startAddress));
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SortedDocValues getSorted(FieldInfo field) throws IOException {
        FST<Long> instance;
        final FSTEntry entry = this.fsts.get(field.number);
        Lucene42DocValuesProducer lucene42DocValuesProducer = this;
        synchronized (lucene42DocValuesProducer) {
            instance = this.fstInstances.get(field.number);
            if (instance == null) {
                this.data.seek(entry.offset);
                instance = new FST<Long>(this.data, PositiveIntOutputs.getSingleton());
                this.fstInstances.put(field.number, instance);
            }
        }
        final NumericDocValues docToOrd = this.getNumeric(field);
        final FST<Long> fst = instance;
        final FST.BytesReader in = fst.getBytesReader();
        final FST.Arc firstArc = new FST.Arc();
        final FST.Arc scratchArc = new FST.Arc();
        final IntsRef scratchInts = new IntsRef();
        final BytesRefFSTEnum<Long> fstEnum = new BytesRefFSTEnum<Long>(fst);
        return new SortedDocValues(){

            @Override
            public int getOrd(int docID) {
                return (int)docToOrd.get(docID);
            }

            @Override
            public void lookupOrd(int ord, BytesRef result) {
                try {
                    in.setPosition(0L);
                    fst.getFirstArc(firstArc);
                    IntsRef output = Util.getByOutput(fst, ord, in, firstArc, scratchArc, scratchInts);
                    result.bytes = new byte[output.length];
                    result.offset = 0;
                    result.length = 0;
                    Util.toBytesRef(output, result);
                }
                catch (IOException bogus) {
                    throw new RuntimeException(bogus);
                }
            }

            @Override
            public int lookupTerm(BytesRef key) {
                try {
                    BytesRefFSTEnum.InputOutput o = fstEnum.seekCeil(key);
                    if (o == null) {
                        return -this.getValueCount() - 1;
                    }
                    if (o.input.equals(key)) {
                        return ((Long)o.output).intValue();
                    }
                    return (int)(-((Long)o.output).longValue()) - 1;
                }
                catch (IOException bogus) {
                    throw new RuntimeException(bogus);
                }
            }

            @Override
            public int getValueCount() {
                return (int)entry.numOrds;
            }

            @Override
            public TermsEnum termsEnum() {
                return new FSTTermsEnum(fst);
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SortedSetDocValues getSortedSet(FieldInfo field) throws IOException {
        FST<Long> instance;
        final FSTEntry entry = this.fsts.get(field.number);
        if (entry.numOrds == 0L) {
            return SortedSetDocValues.EMPTY;
        }
        Lucene42DocValuesProducer lucene42DocValuesProducer = this;
        synchronized (lucene42DocValuesProducer) {
            instance = this.fstInstances.get(field.number);
            if (instance == null) {
                this.data.seek(entry.offset);
                instance = new FST<Long>(this.data, PositiveIntOutputs.getSingleton());
                this.fstInstances.put(field.number, instance);
            }
        }
        final BinaryDocValues docToOrds = this.getBinary(field);
        final FST<Long> fst = instance;
        final FST.BytesReader in = fst.getBytesReader();
        final FST.Arc firstArc = new FST.Arc();
        final FST.Arc scratchArc = new FST.Arc();
        final IntsRef scratchInts = new IntsRef();
        final BytesRefFSTEnum<Long> fstEnum = new BytesRefFSTEnum<Long>(fst);
        final BytesRef ref = new BytesRef();
        final ByteArrayDataInput input = new ByteArrayDataInput();
        return new SortedSetDocValues(){
            long currentOrd;

            @Override
            public long nextOrd() {
                if (input.eof()) {
                    return -1L;
                }
                this.currentOrd += input.readVLong();
                return this.currentOrd;
            }

            @Override
            public void setDocument(int docID) {
                docToOrds.get(docID, ref);
                input.reset(ref.bytes, ref.offset, ref.length);
                this.currentOrd = 0L;
            }

            @Override
            public void lookupOrd(long ord, BytesRef result) {
                try {
                    in.setPosition(0L);
                    fst.getFirstArc(firstArc);
                    IntsRef output = Util.getByOutput(fst, ord, in, firstArc, scratchArc, scratchInts);
                    result.bytes = new byte[output.length];
                    result.offset = 0;
                    result.length = 0;
                    Util.toBytesRef(output, result);
                }
                catch (IOException bogus) {
                    throw new RuntimeException(bogus);
                }
            }

            @Override
            public long lookupTerm(BytesRef key) {
                try {
                    BytesRefFSTEnum.InputOutput o = fstEnum.seekCeil(key);
                    if (o == null) {
                        return -this.getValueCount() - 1L;
                    }
                    if (o.input.equals(key)) {
                        return ((Long)o.output).intValue();
                    }
                    return -((Long)o.output).longValue() - 1L;
                }
                catch (IOException bogus) {
                    throw new RuntimeException(bogus);
                }
            }

            @Override
            public long getValueCount() {
                return entry.numOrds;
            }

            @Override
            public TermsEnum termsEnum() {
                return new FSTTermsEnum(fst);
            }
        };
    }

    @Override
    public void close() throws IOException {
        this.data.close();
    }

    static class FSTTermsEnum
    extends TermsEnum {
        final BytesRefFSTEnum<Long> in;
        final FST<Long> fst;
        final FST.BytesReader bytesReader;
        final FST.Arc<Long> firstArc = new FST.Arc();
        final FST.Arc<Long> scratchArc = new FST.Arc();
        final IntsRef scratchInts = new IntsRef();
        final BytesRef scratchBytes = new BytesRef();

        FSTTermsEnum(FST<Long> fst) {
            this.fst = fst;
            this.in = new BytesRefFSTEnum<Long>(fst);
            this.bytesReader = fst.getBytesReader();
        }

        @Override
        public BytesRef next() throws IOException {
            BytesRefFSTEnum.InputOutput<Long> io = this.in.next();
            if (io == null) {
                return null;
            }
            return io.input;
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return BytesRef.getUTF8SortedAsUnicodeComparator();
        }

        @Override
        public TermsEnum.SeekStatus seekCeil(BytesRef text, boolean useCache) throws IOException {
            if (this.in.seekCeil(text) == null) {
                return TermsEnum.SeekStatus.END;
            }
            if (this.term().equals(text)) {
                return TermsEnum.SeekStatus.FOUND;
            }
            return TermsEnum.SeekStatus.NOT_FOUND;
        }

        @Override
        public boolean seekExact(BytesRef text, boolean useCache) throws IOException {
            return this.in.seekExact(text) != null;
        }

        @Override
        public void seekExact(long ord) throws IOException {
            this.bytesReader.setPosition(0L);
            this.fst.getFirstArc(this.firstArc);
            IntsRef output = Util.getByOutput(this.fst, ord, this.bytesReader, this.firstArc, this.scratchArc, this.scratchInts);
            this.scratchBytes.bytes = new byte[output.length];
            this.scratchBytes.offset = 0;
            this.scratchBytes.length = 0;
            Util.toBytesRef(output, this.scratchBytes);
            this.in.seekExact(this.scratchBytes);
        }

        @Override
        public BytesRef term() throws IOException {
            return this.in.current().input;
        }

        @Override
        public long ord() throws IOException {
            return (Long)this.in.current().output;
        }

        @Override
        public int docFreq() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long totalTermFreq() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public DocsAndPositionsEnum docsAndPositions(Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
            throw new UnsupportedOperationException();
        }
    }

    static class FSTEntry {
        long offset;
        long numOrds;

        FSTEntry() {
        }
    }

    static class BinaryEntry {
        long offset;
        long numBytes;
        int minLength;
        int maxLength;
        int packedIntsVersion;
        int blockSize;

        BinaryEntry() {
        }
    }

    static class NumericEntry {
        long offset;
        byte format;
        int packedIntsVersion;

        NumericEntry() {
        }
    }
}

