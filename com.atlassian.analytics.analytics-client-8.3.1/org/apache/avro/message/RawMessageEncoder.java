/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.message.MessageEncoder;
import org.apache.avro.util.internal.ThreadLocalWithInitial;

public class RawMessageEncoder<D>
implements MessageEncoder<D> {
    private static final ThreadLocal<BufferOutputStream> TEMP = ThreadLocalWithInitial.of(BufferOutputStream::new);
    private static final ThreadLocal<BinaryEncoder> ENCODER = new ThreadLocal();
    private final boolean copyOutputBytes;
    private final DatumWriter<D> writer;

    public RawMessageEncoder(GenericData model, Schema schema) {
        this(model, schema, true);
    }

    public RawMessageEncoder(GenericData model, Schema schema, boolean shouldCopy) {
        Schema writeSchema = schema;
        this.copyOutputBytes = shouldCopy;
        this.writer = model.createDatumWriter(writeSchema);
    }

    @Override
    public ByteBuffer encode(D datum) throws IOException {
        BufferOutputStream temp = TEMP.get();
        temp.reset();
        this.encode(datum, temp);
        if (this.copyOutputBytes) {
            return temp.toBufferWithCopy();
        }
        return temp.toBufferWithoutCopy();
    }

    @Override
    public void encode(D datum, OutputStream stream) throws IOException {
        BinaryEncoder encoder = EncoderFactory.get().directBinaryEncoder(stream, ENCODER.get());
        ENCODER.set(encoder);
        this.writer.write(datum, encoder);
        encoder.flush();
    }

    private static class BufferOutputStream
    extends ByteArrayOutputStream {
        BufferOutputStream() {
        }

        ByteBuffer toBufferWithoutCopy() {
            return ByteBuffer.wrap(this.buf, 0, this.count);
        }

        ByteBuffer toBufferWithCopy() {
            return ByteBuffer.wrap(this.toByteArray());
        }
    }
}

