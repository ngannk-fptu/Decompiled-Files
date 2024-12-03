/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.xerial.snappy.Snappy
 */
package org.apache.avro.file;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import org.apache.avro.file.Codec;
import org.apache.avro.file.CodecFactory;
import org.xerial.snappy.Snappy;

public class SnappyCodec
extends Codec {
    private CRC32 crc32 = new CRC32();

    private SnappyCodec() {
    }

    @Override
    public String getName() {
        return "snappy";
    }

    @Override
    public ByteBuffer compress(ByteBuffer in) throws IOException {
        int offset = SnappyCodec.computeOffset(in);
        ByteBuffer out = ByteBuffer.allocate(Snappy.maxCompressedLength((int)in.remaining()) + 4);
        int size = Snappy.compress((byte[])in.array(), (int)offset, (int)in.remaining(), (byte[])out.array(), (int)0);
        this.crc32.reset();
        this.crc32.update(in.array(), offset, in.remaining());
        out.putInt(size, (int)this.crc32.getValue());
        ((Buffer)out).limit(size + 4);
        return out;
    }

    @Override
    public ByteBuffer decompress(ByteBuffer in) throws IOException {
        int offset = SnappyCodec.computeOffset(in);
        ByteBuffer out = ByteBuffer.allocate(Snappy.uncompressedLength((byte[])in.array(), (int)offset, (int)(in.remaining() - 4)));
        int size = Snappy.uncompress((byte[])in.array(), (int)offset, (int)(in.remaining() - 4), (byte[])out.array(), (int)0);
        ((Buffer)out).limit(size);
        this.crc32.reset();
        this.crc32.update(out.array(), 0, size);
        if (in.getInt(in.limit() - 4) != (int)this.crc32.getValue()) {
            throw new IOException("Checksum failure");
        }
        return out;
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
            return new SnappyCodec();
        }

        static {
            Snappy.getNativeLibraryVersion();
        }
    }
}

