/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.message;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.apache.avro.generic.GenericData;
import org.apache.avro.message.MessageEncoder;
import org.apache.avro.message.RawMessageEncoder;

public class BinaryMessageEncoder<D>
implements MessageEncoder<D> {
    static final byte[] V1_HEADER = new byte[]{-61, 1};
    private final RawMessageEncoder<D> writeCodec;

    public BinaryMessageEncoder(GenericData model, Schema schema) {
        this(model, schema, true);
    }

    public BinaryMessageEncoder(GenericData model, Schema schema, boolean shouldCopy) {
        this.writeCodec = new V1MessageEncoder(model, schema, shouldCopy);
    }

    @Override
    public ByteBuffer encode(D datum) throws IOException {
        return this.writeCodec.encode(datum);
    }

    @Override
    public void encode(D datum, OutputStream stream) throws IOException {
        this.writeCodec.encode(datum, stream);
    }

    private static class V1MessageEncoder<D>
    extends RawMessageEncoder<D> {
        private final byte[] headerBytes;

        V1MessageEncoder(GenericData model, Schema schema, boolean shouldCopy) {
            super(model, schema, shouldCopy);
            this.headerBytes = V1MessageEncoder.getWriteHeader(schema);
        }

        @Override
        public void encode(D datum, OutputStream stream) throws IOException {
            stream.write(this.headerBytes);
            super.encode(datum, stream);
        }

        private static byte[] getWriteHeader(Schema schema) {
            try {
                byte[] fp = SchemaNormalization.parsingFingerprint("CRC-64-AVRO", schema);
                byte[] ret = new byte[V1_HEADER.length + fp.length];
                System.arraycopy(V1_HEADER, 0, ret, 0, V1_HEADER.length);
                System.arraycopy(fp, 0, ret, V1_HEADER.length, fp.length);
                return ret;
            }
            catch (NoSuchAlgorithmException e) {
                throw new AvroRuntimeException(e);
            }
        }
    }
}

