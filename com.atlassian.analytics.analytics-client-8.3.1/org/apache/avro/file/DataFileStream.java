/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.file;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.InvalidAvroMagicException;
import org.apache.avro.Schema;
import org.apache.avro.file.Codec;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileConstants;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;

public class DataFileStream<D>
implements Iterator<D>,
Iterable<D>,
Closeable {
    private DatumReader<D> reader;
    private long blockSize;
    private boolean availableBlock = false;
    private Header header;
    BinaryDecoder vin;
    BinaryDecoder datumIn = null;
    ByteBuffer blockBuffer;
    long blockCount;
    long blockRemaining;
    byte[] syncBuffer = new byte[16];
    private Codec codec;
    private DataBlock block = null;

    public DataFileStream(InputStream in, DatumReader<D> reader) throws IOException {
        this.reader = reader;
        this.initialize(in, null);
    }

    protected DataFileStream(DatumReader<D> reader) throws IOException {
        this.reader = reader;
    }

    byte[] readMagic() throws IOException {
        if (this.vin == null) {
            throw new IOException("InputStream is not initialized");
        }
        byte[] magic = new byte[DataFileConstants.MAGIC.length];
        try {
            this.vin.readFixed(magic);
        }
        catch (IOException e) {
            throw new IOException("Not an Avro data file.", e);
        }
        return magic;
    }

    void validateMagic(byte[] magic) throws InvalidAvroMagicException {
        if (!Arrays.equals(DataFileConstants.MAGIC, magic)) {
            throw new InvalidAvroMagicException("Not an Avro data file.");
        }
    }

    void initialize(InputStream in, byte[] magic) throws IOException {
        this.header = new Header();
        this.vin = DecoderFactory.get().binaryDecoder(in, this.vin);
        magic = magic == null ? this.readMagic() : magic;
        this.validateMagic(magic);
        long l = this.vin.readMapStart();
        if (l > 0L) {
            do {
                for (long i = 0L; i < l; ++i) {
                    String key = this.vin.readString(null).toString();
                    ByteBuffer value = this.vin.readBytes(null);
                    byte[] bb = new byte[value.remaining()];
                    value.get(bb);
                    this.header.meta.put(key, bb);
                    this.header.metaKeyList.add(key);
                }
            } while ((l = this.vin.mapNext()) != 0L);
        }
        this.vin.readFixed(this.header.sync);
        this.header.metaKeyList = Collections.unmodifiableList(this.header.metaKeyList);
        this.header.schema = new Schema.Parser().setValidate(false).setValidateDefaults(false).parse(this.getMetaString("avro.schema"));
        this.codec = this.resolveCodec();
        this.reader.setSchema(this.header.schema);
    }

    void initialize(Header header) throws IOException {
        this.header = header;
        this.codec = this.resolveCodec();
        this.reader.setSchema(header.schema);
    }

    Codec resolveCodec() {
        String codecStr = this.getMetaString("avro.codec");
        if (codecStr != null) {
            return CodecFactory.fromString(codecStr).createInstance();
        }
        return CodecFactory.nullCodec().createInstance();
    }

    public Header getHeader() {
        return this.header;
    }

    public Schema getSchema() {
        return this.header.schema;
    }

    public List<String> getMetaKeys() {
        return this.header.metaKeyList;
    }

    public byte[] getMeta(String key) {
        return this.header.meta.get(key);
    }

    public String getMetaString(String key) {
        byte[] value = this.getMeta(key);
        if (value == null) {
            return null;
        }
        return new String(value, StandardCharsets.UTF_8);
    }

    public long getMetaLong(String key) {
        return Long.parseLong(this.getMetaString(key));
    }

    @Override
    public Iterator<D> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        try {
            if (this.blockRemaining == 0L) {
                boolean atEnd;
                if (null != this.datumIn && !(atEnd = this.datumIn.isEnd())) {
                    throw new IOException("Block read partially, the data may be corrupt");
                }
                if (this.hasNextBlock()) {
                    this.block = this.nextRawBlock(this.block);
                    this.block.decompressUsing(this.codec);
                    this.blockBuffer = this.block.getAsByteBuffer();
                    this.datumIn = DecoderFactory.get().binaryDecoder(this.blockBuffer.array(), this.blockBuffer.arrayOffset() + this.blockBuffer.position(), this.blockBuffer.remaining(), this.datumIn);
                }
            }
            return this.blockRemaining != 0L;
        }
        catch (EOFException e) {
            return false;
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }

    @Override
    public D next() {
        try {
            return this.next(null);
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }

    public D next(D reuse) throws IOException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        D result = this.reader.read(reuse, this.datumIn);
        if (0L == --this.blockRemaining) {
            this.blockFinished();
        }
        return result;
    }

    public ByteBuffer nextBlock() throws IOException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        if (this.blockRemaining != this.blockCount) {
            throw new IllegalStateException("Not at block start.");
        }
        this.blockRemaining = 0L;
        this.datumIn = null;
        return this.blockBuffer;
    }

    public long getBlockCount() {
        return this.blockCount;
    }

    public long getBlockSize() {
        return this.blockSize;
    }

    protected void blockFinished() throws IOException {
    }

    boolean hasNextBlock() {
        try {
            if (this.availableBlock) {
                return true;
            }
            if (this.vin.isEnd()) {
                return false;
            }
            this.blockRemaining = this.vin.readLong();
            this.blockSize = this.vin.readLong();
            if (this.blockSize > Integer.MAX_VALUE || this.blockSize < 0L) {
                throw new IOException("Block size invalid or too large for this implementation: " + this.blockSize);
            }
            this.blockCount = this.blockRemaining;
            this.availableBlock = true;
            return true;
        }
        catch (EOFException eof) {
            return false;
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }

    DataBlock nextRawBlock(DataBlock reuse) throws IOException {
        if (!this.hasNextBlock()) {
            throw new NoSuchElementException();
        }
        if (reuse == null || reuse.data.length < (int)this.blockSize) {
            reuse = new DataBlock(this.blockRemaining, (int)this.blockSize);
        } else {
            reuse.numEntries = this.blockRemaining;
            reuse.blockSize = (int)this.blockSize;
        }
        this.vin.readFixed(reuse.data, 0, reuse.blockSize);
        this.vin.readFixed(this.syncBuffer);
        this.availableBlock = false;
        if (!Arrays.equals(this.syncBuffer, this.header.sync)) {
            throw new IOException("Invalid sync!");
        }
        return reuse;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        this.vin.inputStream().close();
    }

    static class DataBlock {
        private byte[] data;
        private long numEntries;
        private int blockSize;
        private int offset = 0;
        private boolean flushOnWrite = true;

        private DataBlock(long numEntries, int blockSize) {
            this.data = new byte[blockSize];
            this.numEntries = numEntries;
            this.blockSize = blockSize;
        }

        DataBlock(ByteBuffer block, long numEntries) {
            this.data = block.array();
            this.blockSize = block.remaining();
            this.offset = block.arrayOffset() + block.position();
            this.numEntries = numEntries;
        }

        byte[] getData() {
            return this.data;
        }

        long getNumEntries() {
            return this.numEntries;
        }

        int getBlockSize() {
            return this.blockSize;
        }

        boolean isFlushOnWrite() {
            return this.flushOnWrite;
        }

        void setFlushOnWrite(boolean flushOnWrite) {
            this.flushOnWrite = flushOnWrite;
        }

        ByteBuffer getAsByteBuffer() {
            return ByteBuffer.wrap(this.data, this.offset, this.blockSize);
        }

        void decompressUsing(Codec c) throws IOException {
            ByteBuffer result = c.decompress(this.getAsByteBuffer());
            this.data = result.array();
            this.blockSize = result.remaining();
        }

        void compressUsing(Codec c) throws IOException {
            ByteBuffer result = c.compress(this.getAsByteBuffer());
            this.data = result.array();
            this.blockSize = result.remaining();
        }

        void writeBlockTo(BinaryEncoder e, byte[] sync) throws IOException {
            e.writeLong(this.numEntries);
            e.writeLong(this.blockSize);
            e.writeFixed(this.data, this.offset, this.blockSize);
            e.writeFixed(sync);
            if (this.flushOnWrite) {
                e.flush();
            }
        }
    }

    public static final class Header {
        Schema schema;
        Map<String, byte[]> meta = new HashMap<String, byte[]>();
        private transient List<String> metaKeyList = new ArrayList<String>();
        byte[] sync = new byte[16];

        private Header() {
        }
    }
}

