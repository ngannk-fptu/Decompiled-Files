/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.avro.file.Codec;
import org.apache.avro.file.CodecFactory;

final class NullCodec
extends Codec {
    private static final NullCodec INSTANCE = new NullCodec();
    public static final CodecFactory OPTION = new Option();

    NullCodec() {
    }

    @Override
    public String getName() {
        return "null";
    }

    @Override
    public ByteBuffer compress(ByteBuffer buffer) throws IOException {
        return buffer;
    }

    @Override
    public ByteBuffer decompress(ByteBuffer data) throws IOException {
        return data;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other != null && other.getClass() == this.getClass();
    }

    @Override
    public int hashCode() {
        return 2;
    }

    static class Option
    extends CodecFactory {
        Option() {
        }

        @Override
        protected Codec createInstance() {
            return INSTANCE;
        }
    }
}

