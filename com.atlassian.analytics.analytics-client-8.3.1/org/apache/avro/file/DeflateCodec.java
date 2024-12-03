/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.file;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;
import org.apache.avro.file.Codec;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.util.NonCopyingByteArrayOutputStream;

public class DeflateCodec
extends Codec {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private Deflater deflater;
    private Inflater inflater;
    private boolean nowrap = true;
    private int compressionLevel;

    public DeflateCodec(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    @Override
    public String getName() {
        return "deflate";
    }

    @Override
    public ByteBuffer compress(ByteBuffer data) throws IOException {
        NonCopyingByteArrayOutputStream baos = new NonCopyingByteArrayOutputStream(8192);
        try (DeflaterOutputStream outputStream = new DeflaterOutputStream((OutputStream)baos, this.getDeflater());){
            ((OutputStream)outputStream).write(data.array(), DeflateCodec.computeOffset(data), data.remaining());
        }
        return baos.asByteBuffer();
    }

    @Override
    public ByteBuffer decompress(ByteBuffer data) throws IOException {
        NonCopyingByteArrayOutputStream baos = new NonCopyingByteArrayOutputStream(8192);
        try (InflaterOutputStream outputStream = new InflaterOutputStream(baos, this.getInflater());){
            ((OutputStream)outputStream).write(data.array(), DeflateCodec.computeOffset(data), data.remaining());
        }
        return baos.asByteBuffer();
    }

    private Inflater getInflater() {
        if (null == this.inflater) {
            this.inflater = new Inflater(this.nowrap);
        } else {
            this.inflater.reset();
        }
        return this.inflater;
    }

    private Deflater getDeflater() {
        if (null == this.deflater) {
            this.deflater = new Deflater(this.compressionLevel, this.nowrap);
        } else {
            this.deflater.reset();
        }
        return this.deflater;
    }

    @Override
    public int hashCode() {
        return this.nowrap ? 0 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        DeflateCodec other = (DeflateCodec)obj;
        return this.nowrap == other.nowrap;
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
            return new DeflateCodec(this.compressionLevel);
        }
    }
}

