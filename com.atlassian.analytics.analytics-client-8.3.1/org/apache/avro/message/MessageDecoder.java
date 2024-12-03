/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.message;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.avro.util.ReusableByteArrayInputStream;
import org.apache.avro.util.ReusableByteBufferInputStream;
import org.apache.avro.util.internal.ThreadLocalWithInitial;

public interface MessageDecoder<D> {
    public D decode(InputStream var1) throws IOException;

    public D decode(InputStream var1, D var2) throws IOException;

    public D decode(ByteBuffer var1) throws IOException;

    public D decode(ByteBuffer var1, D var2) throws IOException;

    public D decode(byte[] var1) throws IOException;

    public D decode(byte[] var1, D var2) throws IOException;

    public static abstract class BaseDecoder<D>
    implements MessageDecoder<D> {
        private static final ThreadLocal<ReusableByteArrayInputStream> BYTE_ARRAY_IN = ThreadLocalWithInitial.of(ReusableByteArrayInputStream::new);
        private static final ThreadLocal<ReusableByteBufferInputStream> BYTE_BUFFER_IN = ThreadLocalWithInitial.of(ReusableByteBufferInputStream::new);

        @Override
        public D decode(InputStream stream) throws IOException {
            return this.decode(stream, null);
        }

        @Override
        public D decode(ByteBuffer encoded) throws IOException {
            return this.decode(encoded, null);
        }

        @Override
        public D decode(byte[] encoded) throws IOException {
            return this.decode(encoded, null);
        }

        @Override
        public D decode(ByteBuffer encoded, D reuse) throws IOException {
            ReusableByteBufferInputStream in = BYTE_BUFFER_IN.get();
            in.setByteBuffer(encoded);
            return this.decode(in, reuse);
        }

        @Override
        public D decode(byte[] encoded, D reuse) throws IOException {
            ReusableByteArrayInputStream in = BYTE_ARRAY_IN.get();
            in.setByteArray(encoded, 0, encoded.length);
            return this.decode(in, reuse);
        }
    }
}

