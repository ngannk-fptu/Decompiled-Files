/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene42;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.store.ByteArrayDataOutput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.MathUtil;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.packed.BlockPackedWriter;
import org.apache.lucene.util.packed.MonotonicBlockPackedWriter;
import org.apache.lucene.util.packed.PackedInts;

class Lucene42DocValuesConsumer
extends DocValuesConsumer {
    static final int VERSION_START = 0;
    static final int VERSION_GCD_COMPRESSION = 1;
    static final int VERSION_CURRENT = 1;
    static final byte NUMBER = 0;
    static final byte BYTES = 1;
    static final byte FST = 2;
    static final int BLOCK_SIZE = 4096;
    static final byte DELTA_COMPRESSED = 0;
    static final byte TABLE_COMPRESSED = 1;
    static final byte UNCOMPRESSED = 2;
    static final byte GCD_COMPRESSED = 3;
    final IndexOutput data;
    final IndexOutput meta;
    final int maxDoc;
    final float acceptableOverheadRatio;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Lucene42DocValuesConsumer(SegmentWriteState state, String dataCodec, String dataExtension, String metaCodec, String metaExtension, float acceptableOverheadRatio) throws IOException {
        this.acceptableOverheadRatio = acceptableOverheadRatio;
        this.maxDoc = state.segmentInfo.getDocCount();
        boolean success = false;
        try {
            String dataName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, dataExtension);
            this.data = state.directory.createOutput(dataName, state.context);
            CodecUtil.writeHeader(this.data, dataCodec, 1);
            String metaName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, metaExtension);
            this.meta = state.directory.createOutput(metaName, state.context);
            CodecUtil.writeHeader(this.meta, metaCodec, 1);
            return;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(this);
            throw throwable;
        }
    }

    @Override
    public void addNumericField(FieldInfo field, Iterable<Number> values) throws IOException {
        this.addNumericField(field, values, true);
    }

    void addNumericField(FieldInfo field, Iterable<Number> values, boolean optimizeStorage) throws IOException {
        this.meta.writeVInt(field.number);
        this.meta.writeByte((byte)0);
        this.meta.writeLong(this.data.getFilePointer());
        long minValue = Long.MAX_VALUE;
        long maxValue = Long.MIN_VALUE;
        long gcd = 0L;
        HashSet<Long> uniqueValues = null;
        if (optimizeStorage) {
            uniqueValues = new HashSet<Long>();
            long count = 0L;
            for (Number nv : values) {
                long v = nv.longValue();
                if (gcd != 1L) {
                    if (v < -4611686018427387904L || v > 0x3FFFFFFFFFFFFFFFL) {
                        gcd = 1L;
                    } else if (count != 0L) {
                        gcd = MathUtil.gcd(gcd, v - minValue);
                    }
                }
                minValue = Math.min(minValue, v);
                maxValue = Math.max(maxValue, v);
                if (uniqueValues != null && uniqueValues.add(v) && uniqueValues.size() > 256) {
                    uniqueValues = null;
                }
                ++count;
            }
            assert (count == (long)this.maxDoc);
        }
        if (uniqueValues != null) {
            int bitsPerValue = PackedInts.bitsRequired(uniqueValues.size() - 1);
            PackedInts.FormatAndBits formatAndBits = PackedInts.fastestFormatAndBits(this.maxDoc, bitsPerValue, this.acceptableOverheadRatio);
            if (formatAndBits.bitsPerValue == 8 && minValue >= -128L && maxValue <= 127L) {
                this.meta.writeByte((byte)2);
                for (Number nv : values) {
                    this.data.writeByte((byte)nv.longValue());
                }
            } else {
                this.meta.writeByte((byte)1);
                Long[] decode = uniqueValues.toArray(new Long[uniqueValues.size()]);
                HashMap<Long, Integer> encode = new HashMap<Long, Integer>();
                this.data.writeVInt(decode.length);
                for (int i = 0; i < decode.length; ++i) {
                    this.data.writeLong(decode[i]);
                    encode.put(decode[i], i);
                }
                this.meta.writeVInt(1);
                this.data.writeVInt(formatAndBits.format.getId());
                this.data.writeVInt(formatAndBits.bitsPerValue);
                PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.data, formatAndBits.format, this.maxDoc, formatAndBits.bitsPerValue, 1024);
                for (Number nv : values) {
                    writer.add(((Integer)encode.get(nv.longValue())).intValue());
                }
                writer.finish();
            }
        } else if (gcd != 0L && gcd != 1L) {
            this.meta.writeByte((byte)3);
            this.meta.writeVInt(1);
            this.data.writeLong(minValue);
            this.data.writeLong(gcd);
            this.data.writeVInt(4096);
            BlockPackedWriter writer = new BlockPackedWriter(this.data, 4096);
            for (Number nv : values) {
                writer.add((nv.longValue() - minValue) / gcd);
            }
            writer.finish();
        } else {
            this.meta.writeByte((byte)0);
            this.meta.writeVInt(1);
            this.data.writeVInt(4096);
            BlockPackedWriter writer = new BlockPackedWriter(this.data, 4096);
            for (Number nv : values) {
                writer.add(nv.longValue());
            }
            writer.finish();
        }
    }

    @Override
    public void close() throws IOException {
        block6: {
            block5: {
                boolean success = false;
                try {
                    if (this.meta != null) {
                        this.meta.writeVInt(-1);
                    }
                    if (!(success = true)) break block5;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(this.data, this.meta);
                    } else {
                        IOUtils.closeWhileHandlingException(this.data, this.meta);
                    }
                    throw throwable;
                }
                IOUtils.close(this.data, this.meta);
                break block6;
            }
            IOUtils.closeWhileHandlingException(this.data, this.meta);
        }
    }

    @Override
    public void addBinaryField(FieldInfo field, Iterable<BytesRef> values) throws IOException {
        this.meta.writeVInt(field.number);
        this.meta.writeByte((byte)1);
        int minLength = Integer.MAX_VALUE;
        int maxLength = Integer.MIN_VALUE;
        long startFP = this.data.getFilePointer();
        for (BytesRef v : values) {
            minLength = Math.min(minLength, v.length);
            maxLength = Math.max(maxLength, v.length);
            this.data.writeBytes(v.bytes, v.offset, v.length);
        }
        this.meta.writeLong(startFP);
        this.meta.writeLong(this.data.getFilePointer() - startFP);
        this.meta.writeVInt(minLength);
        this.meta.writeVInt(maxLength);
        if (minLength != maxLength) {
            this.meta.writeVInt(1);
            this.meta.writeVInt(4096);
            MonotonicBlockPackedWriter writer = new MonotonicBlockPackedWriter(this.data, 4096);
            long addr = 0L;
            for (BytesRef v : values) {
                writer.add(addr += (long)v.length);
            }
            writer.finish();
        }
    }

    private void writeFST(FieldInfo field, Iterable<BytesRef> values) throws IOException {
        this.meta.writeVInt(field.number);
        this.meta.writeByte((byte)2);
        this.meta.writeLong(this.data.getFilePointer());
        PositiveIntOutputs outputs = PositiveIntOutputs.getSingleton();
        Builder<Long> builder = new Builder<Long>(FST.INPUT_TYPE.BYTE1, outputs);
        IntsRef scratch = new IntsRef();
        long ord = 0L;
        for (BytesRef v : values) {
            builder.add(Util.toIntsRef(v, scratch), ord);
            ++ord;
        }
        FST<Long> fst = builder.finish();
        if (fst != null) {
            fst.save(this.data);
        }
        this.meta.writeVLong(ord);
    }

    @Override
    public void addSortedField(FieldInfo field, Iterable<BytesRef> values, Iterable<Number> docToOrd) throws IOException {
        this.addNumericField(field, docToOrd, false);
        this.writeFST(field, values);
    }

    @Override
    public void addSortedSetField(FieldInfo field, Iterable<BytesRef> values, final Iterable<Number> docToOrdCount, final Iterable<Number> ords) throws IOException {
        this.addBinaryField(field, new Iterable<BytesRef>(){

            @Override
            public Iterator<BytesRef> iterator() {
                return new SortedSetIterator(docToOrdCount.iterator(), ords.iterator());
            }
        });
        this.writeFST(field, values);
    }

    static class SortedSetIterator
    implements Iterator<BytesRef> {
        byte[] buffer = new byte[10];
        ByteArrayDataOutput out = new ByteArrayDataOutput();
        BytesRef ref = new BytesRef();
        final Iterator<Number> counts;
        final Iterator<Number> ords;

        SortedSetIterator(Iterator<Number> counts, Iterator<Number> ords) {
            this.counts = counts;
            this.ords = ords;
        }

        @Override
        public boolean hasNext() {
            return this.counts.hasNext();
        }

        @Override
        public BytesRef next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            int count = this.counts.next().intValue();
            int maxSize = count * 9;
            if (maxSize > this.buffer.length) {
                this.buffer = ArrayUtil.grow(this.buffer, maxSize);
            }
            try {
                this.encodeValues(count);
            }
            catch (IOException bogus) {
                throw new RuntimeException(bogus);
            }
            this.ref.bytes = this.buffer;
            this.ref.offset = 0;
            this.ref.length = this.out.getPosition();
            return this.ref;
        }

        private void encodeValues(int count) throws IOException {
            this.out.reset(this.buffer);
            long lastOrd = 0L;
            for (int i = 0; i < count; ++i) {
                long ord = this.ords.next().longValue();
                this.out.writeVLong(ord - lastOrd);
                lastOrd = ord;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

