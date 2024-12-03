/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.compressors.xz.XZCompressorInputStream
 *  org.apache.commons.compress.compressors.xz.XZCompressorOutputStream
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
import org.apache.avro.util.NonCopyingByteArrayOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class XZCodec
extends Codec {
    public static final int DEFAULT_COMPRESSION = 6;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private int compressionLevel;

    public XZCodec(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    @Override
    public String getName() {
        return "xz";
    }

    @Override
    public ByteBuffer compress(ByteBuffer data) throws IOException {
        NonCopyingByteArrayOutputStream baos = new NonCopyingByteArrayOutputStream(8192);
        try (XZCompressorOutputStream outputStream = new XZCompressorOutputStream((OutputStream)baos, this.compressionLevel);){
            outputStream.write(data.array(), XZCodec.computeOffset(data), data.remaining());
        }
        return baos.asByteBuffer();
    }

    @Override
    public ByteBuffer decompress(ByteBuffer data) throws IOException {
        NonCopyingByteArrayOutputStream baos = new NonCopyingByteArrayOutputStream(8192);
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(data.array(), XZCodec.computeOffset(data), data.remaining());
        try (XZCompressorInputStream ios = new XZCompressorInputStream((InputStream)bytesIn);){
            IOUtils.copy((InputStream)ios, (OutputStream)baos);
        }
        return baos.asByteBuffer();
    }

    @Override
    public int hashCode() {
        return this.compressionLevel;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        XZCodec other = (XZCodec)obj;
        return this.compressionLevel == other.compressionLevel;
    }

    @Override
    public String toString() {
        return this.getName() + "-" + this.compressionLevel;
    }

    static class Option
    extends CodecFactory {
        private int compressionLevel;

        Option(int compressionLevel) {
            this.compressionLevel = compressionLevel;
        }

        @Override
        protected Codec createInstance() {
            return new XZCodec(this.compressionLevel);
        }
    }
}

