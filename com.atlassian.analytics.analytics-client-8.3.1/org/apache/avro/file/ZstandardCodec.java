/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.utils.IOUtils
 */
package org.apache.avro.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.avro.file.Codec;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.ZstandardLoader;
import org.apache.avro.util.NonCopyingByteArrayOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class ZstandardCodec
extends Codec {
    public static final int DEFAULT_COMPRESSION = 3;
    public static final boolean DEFAULT_USE_BUFFERPOOL = false;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final int compressionLevel;
    private final boolean useChecksum;
    private final boolean useBufferPool;

    public ZstandardCodec(int compressionLevel, boolean useChecksum, boolean useBufferPool) {
        this.compressionLevel = compressionLevel;
        this.useChecksum = useChecksum;
        this.useBufferPool = useBufferPool;
    }

    @Override
    public String getName() {
        return "zstandard";
    }

    @Override
    public ByteBuffer compress(ByteBuffer data) throws IOException {
        NonCopyingByteArrayOutputStream baos = new NonCopyingByteArrayOutputStream(8192);
        try (OutputStream outputStream = ZstandardLoader.output(baos, this.compressionLevel, this.useChecksum, this.useBufferPool);){
            outputStream.write(data.array(), ZstandardCodec.computeOffset(data), data.remaining());
        }
        return baos.asByteBuffer();
    }

    @Override
    public ByteBuffer decompress(ByteBuffer compressedData) throws IOException {
        NonCopyingByteArrayOutputStream baos = new NonCopyingByteArrayOutputStream(8192);
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(compressedData.array(), ZstandardCodec.computeOffset(compressedData), compressedData.remaining());
        try (InputStream ios = ZstandardLoader.input(bytesIn, this.useBufferPool);){
            IOUtils.copy((InputStream)ios, (OutputStream)baos);
        }
        return baos.asByteBuffer();
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && obj.getClass() == this.getClass();
    }

    @Override
    public String toString() {
        return this.getName() + "[" + this.compressionLevel + "]";
    }

    static class Option
    extends CodecFactory {
        private final int compressionLevel;
        private final boolean useChecksum;
        private final boolean useBufferPool;

        Option(int compressionLevel, boolean useChecksum, boolean useBufferPool) {
            this.compressionLevel = compressionLevel;
            this.useChecksum = useChecksum;
            this.useBufferPool = useBufferPool;
        }

        @Override
        protected Codec createInstance() {
            return new ZstandardCodec(this.compressionLevel, this.useChecksum, this.useBufferPool);
        }
    }
}

