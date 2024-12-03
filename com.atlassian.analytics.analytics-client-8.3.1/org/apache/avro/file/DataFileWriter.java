/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.utils.IOUtils
 */
package org.apache.avro.file;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.file.Codec;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileConstants;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.SeekableFileInput;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.file.Syncable;
import org.apache.avro.file.SyncableFileOutputStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.util.NonCopyingByteArrayOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class DataFileWriter<D>
implements Closeable,
Flushable {
    private Schema schema;
    private DatumWriter<D> dout;
    private OutputStream underlyingStream;
    private BufferedFileOutputStream out;
    private BinaryEncoder vout;
    private final Map<String, byte[]> meta = new HashMap<String, byte[]>();
    private long blockCount;
    private NonCopyingByteArrayOutputStream buffer;
    private BinaryEncoder bufOut;
    private byte[] sync;
    private int syncInterval = 64000;
    private boolean isOpen;
    private Codec codec;
    private boolean flushOnEveryBlock = true;

    public DataFileWriter(DatumWriter<D> dout) {
        this.dout = dout;
    }

    private void assertOpen() {
        if (!this.isOpen) {
            throw new AvroRuntimeException("not open");
        }
    }

    private void assertNotOpen() {
        if (this.isOpen) {
            throw new AvroRuntimeException("already open");
        }
    }

    public DataFileWriter<D> setCodec(CodecFactory c) {
        this.assertNotOpen();
        this.codec = c.createInstance();
        this.setMetaInternal("avro.codec", this.codec.getName());
        return this;
    }

    public DataFileWriter<D> setSyncInterval(int syncInterval) {
        if (syncInterval < 32 || syncInterval > 0x40000000) {
            throw new IllegalArgumentException("Invalid syncInterval value: " + syncInterval);
        }
        this.syncInterval = syncInterval;
        return this;
    }

    public DataFileWriter<D> create(Schema schema, File file) throws IOException {
        SyncableFileOutputStream sfos = new SyncableFileOutputStream(file);
        try {
            return this.create(schema, sfos, null);
        }
        catch (Throwable e) {
            IOUtils.closeQuietly((Closeable)sfos);
            throw e;
        }
    }

    public DataFileWriter<D> create(Schema schema, OutputStream outs) throws IOException {
        return this.create(schema, outs, null);
    }

    public DataFileWriter<D> create(Schema schema, OutputStream outs, byte[] sync) throws IOException {
        this.assertNotOpen();
        this.schema = schema;
        this.setMetaInternal("avro.schema", schema.toString());
        if (sync == null) {
            this.sync = DataFileWriter.generateSync();
        } else if (sync.length == 16) {
            this.sync = sync;
        } else {
            throw new IOException("sync must be exactly 16 bytes");
        }
        this.init(outs);
        this.vout.writeFixed(DataFileConstants.MAGIC);
        this.vout.writeMapStart();
        this.vout.setItemCount(this.meta.size());
        for (Map.Entry<String, byte[]> entry : this.meta.entrySet()) {
            this.vout.startItem();
            this.vout.writeString(entry.getKey());
            this.vout.writeBytes(entry.getValue());
        }
        this.vout.writeMapEnd();
        this.vout.writeFixed(this.sync);
        this.vout.flush();
        return this;
    }

    public void setFlushOnEveryBlock(boolean flushOnEveryBlock) {
        this.flushOnEveryBlock = flushOnEveryBlock;
    }

    public boolean isFlushOnEveryBlock() {
        return this.flushOnEveryBlock;
    }

    public DataFileWriter<D> appendTo(File file) throws IOException {
        try (SeekableFileInput input = new SeekableFileInput(file);){
            SyncableFileOutputStream output = new SyncableFileOutputStream(file, true);
            DataFileWriter<D> dataFileWriter = this.appendTo(input, output);
            return dataFileWriter;
        }
    }

    public DataFileWriter<D> appendTo(SeekableInput in, OutputStream out) throws IOException {
        this.assertNotOpen();
        DataFileReader reader = new DataFileReader(in, new GenericDatumReader());
        this.schema = reader.getSchema();
        this.sync = reader.getHeader().sync;
        this.meta.putAll(reader.getHeader().meta);
        byte[] codecBytes = this.meta.get("avro.codec");
        if (codecBytes != null) {
            String strCodec = new String(codecBytes, StandardCharsets.UTF_8);
            this.codec = CodecFactory.fromString(strCodec).createInstance();
        } else {
            this.codec = CodecFactory.nullCodec().createInstance();
        }
        this.init(out);
        return this;
    }

    private void init(OutputStream outs) throws IOException {
        this.underlyingStream = outs;
        this.out = new BufferedFileOutputStream(outs);
        EncoderFactory efactory = new EncoderFactory();
        this.vout = efactory.directBinaryEncoder(this.out, null);
        this.dout.setSchema(this.schema);
        this.buffer = new NonCopyingByteArrayOutputStream(Math.min((int)((double)this.syncInterval * 1.25), 0x3FFFFFFE));
        this.bufOut = efactory.directBinaryEncoder(this.buffer, null);
        if (this.codec == null) {
            this.codec = CodecFactory.nullCodec().createInstance();
        }
        this.isOpen = true;
    }

    private static byte[] generateSync() {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            long time = System.currentTimeMillis();
            digester.update((UUID.randomUUID() + "@" + time).getBytes(StandardCharsets.UTF_8));
            return digester.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private DataFileWriter<D> setMetaInternal(String key, byte[] value) {
        this.assertNotOpen();
        this.meta.put(key, value);
        return this;
    }

    private DataFileWriter<D> setMetaInternal(String key, String value) {
        return this.setMetaInternal(key, value.getBytes(StandardCharsets.UTF_8));
    }

    public DataFileWriter<D> setMeta(String key, byte[] value) {
        if (DataFileWriter.isReservedMeta(key)) {
            throw new AvroRuntimeException("Cannot set reserved meta key: " + key);
        }
        return this.setMetaInternal(key, value);
    }

    public static boolean isReservedMeta(String key) {
        return key.startsWith("avro.");
    }

    public DataFileWriter<D> setMeta(String key, String value) {
        return this.setMeta(key, value.getBytes(StandardCharsets.UTF_8));
    }

    public DataFileWriter<D> setMeta(String key, long value) {
        return this.setMeta(key, Long.toString(value));
    }

    public void append(D datum) throws IOException {
        this.assertOpen();
        int usedBuffer = this.bufferInUse();
        try {
            this.dout.write(datum, this.bufOut);
        }
        catch (IOException | RuntimeException e) {
            this.resetBufferTo(usedBuffer);
            throw new AppendWriteException(e);
        }
        ++this.blockCount;
        this.writeIfBlockFull();
    }

    private void resetBufferTo(int size) throws IOException {
        this.bufOut.flush();
        byte[] data = this.buffer.toByteArray();
        this.buffer.reset();
        this.buffer.write(data, 0, size);
    }

    public void appendEncoded(ByteBuffer datum) throws IOException {
        this.assertOpen();
        this.bufOut.writeFixed(datum);
        ++this.blockCount;
        this.writeIfBlockFull();
    }

    private int bufferInUse() {
        return this.buffer.size() + this.bufOut.bytesBuffered();
    }

    private void writeIfBlockFull() throws IOException {
        if (this.bufferInUse() >= this.syncInterval) {
            this.writeBlock();
        }
    }

    public void appendAllFrom(DataFileStream<D> otherFile, boolean recompress) throws IOException {
        this.assertOpen();
        Schema otherSchema = otherFile.getSchema();
        if (!this.schema.equals(otherSchema)) {
            throw new IOException("Schema from file " + otherFile + " does not match");
        }
        this.writeBlock();
        Codec otherCodec = otherFile.resolveCodec();
        DataFileStream.DataBlock nextBlockRaw = null;
        if (this.codec.equals(otherCodec) && !recompress) {
            while (otherFile.hasNextBlock()) {
                nextBlockRaw = otherFile.nextRawBlock(nextBlockRaw);
                nextBlockRaw.writeBlockTo(this.vout, this.sync);
            }
        } else {
            while (otherFile.hasNextBlock()) {
                nextBlockRaw = otherFile.nextRawBlock(nextBlockRaw);
                nextBlockRaw.decompressUsing(otherCodec);
                nextBlockRaw.compressUsing(this.codec);
                nextBlockRaw.writeBlockTo(this.vout, this.sync);
            }
        }
    }

    private void writeBlock() throws IOException {
        if (this.blockCount > 0L) {
            try {
                this.bufOut.flush();
                ByteBuffer uncompressed = this.buffer.asByteBuffer();
                DataFileStream.DataBlock block = new DataFileStream.DataBlock(uncompressed, this.blockCount);
                block.setFlushOnWrite(this.flushOnEveryBlock);
                block.compressUsing(this.codec);
                block.writeBlockTo(this.vout, this.sync);
            }
            finally {
                this.buffer.reset();
                this.blockCount = 0L;
            }
        }
    }

    public long sync() throws IOException {
        this.assertOpen();
        this.writeBlock();
        return this.out.tell();
    }

    @Override
    public void flush() throws IOException {
        this.sync();
        this.vout.flush();
    }

    public void fSync() throws IOException {
        this.flush();
        if (this.underlyingStream instanceof Syncable) {
            ((Syncable)((Object)this.underlyingStream)).sync();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.isOpen) {
            this.flush();
            this.out.close();
            this.isOpen = false;
        }
    }

    private class BufferedFileOutputStream
    extends BufferedOutputStream {
        private long position;

        public BufferedFileOutputStream(OutputStream out) throws IOException {
            super(null);
            this.out = new PositionFilter(out);
        }

        public long tell() {
            return this.position + (long)this.count;
        }

        @Override
        public synchronized void flush() throws IOException {
            try {
                super.flush();
            }
            finally {
                this.count = 0;
            }
        }

        private class PositionFilter
        extends FilterOutputStream {
            public PositionFilter(OutputStream out) throws IOException {
                super(out);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                this.out.write(b, off, len);
                BufferedFileOutputStream.this.position = BufferedFileOutputStream.this.position + (long)len;
            }
        }
    }

    public static class AppendWriteException
    extends RuntimeException {
        public AppendWriteException(Exception e) {
            super(e);
        }
    }
}

