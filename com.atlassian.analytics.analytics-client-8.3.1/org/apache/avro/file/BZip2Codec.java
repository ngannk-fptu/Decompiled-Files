/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
 *  org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
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
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

public class BZip2Codec
extends Codec {
    public static final int DEFAULT_BUFFER_SIZE = 65536;
    private final byte[] buffer = new byte[65536];

    @Override
    public String getName() {
        return "bzip2";
    }

    @Override
    public ByteBuffer compress(ByteBuffer uncompressedData) throws IOException {
        NonCopyingByteArrayOutputStream baos = new NonCopyingByteArrayOutputStream(65536);
        try (BZip2CompressorOutputStream outputStream = new BZip2CompressorOutputStream((OutputStream)baos);){
            outputStream.write(uncompressedData.array(), BZip2Codec.computeOffset(uncompressedData), uncompressedData.remaining());
        }
        return baos.asByteBuffer();
    }

    @Override
    public ByteBuffer decompress(ByteBuffer compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData.array(), BZip2Codec.computeOffset(compressedData), compressedData.remaining());
        NonCopyingByteArrayOutputStream baos = new NonCopyingByteArrayOutputStream(65536);
        try (BZip2CompressorInputStream inputStream = new BZip2CompressorInputStream((InputStream)bais);){
            int readCount = -1;
            while ((readCount = inputStream.read(this.buffer, compressedData.position(), this.buffer.length)) > 0) {
                baos.write(this.buffer, 0, readCount);
            }
            ByteBuffer byteBuffer = baos.asByteBuffer();
            return byteBuffer;
        }
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && obj.getClass() == this.getClass();
    }

    static class Option
    extends CodecFactory {
        Option() {
        }

        @Override
        protected Codec createInstance() {
            return new BZip2Codec();
        }
    }
}

