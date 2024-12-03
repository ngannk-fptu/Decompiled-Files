/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.BlockingBinaryEncoder;
import org.apache.avro.io.BufferedBinaryEncoder;
import org.apache.avro.io.DirectBinaryEncoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.io.ValidatingEncoder;

public class EncoderFactory {
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final int DEFAULT_BLOCK_BUFFER_SIZE = 65536;
    private static final int MIN_BLOCK_BUFFER_SIZE = 64;
    private static final int MAX_BLOCK_BUFFER_SIZE = 0x40000000;
    private static final EncoderFactory DEFAULT_FACTORY = new DefaultEncoderFactory();
    protected int binaryBufferSize = 2048;
    protected int binaryBlockSize = 65536;

    public static EncoderFactory get() {
        return DEFAULT_FACTORY;
    }

    public EncoderFactory configureBufferSize(int size) {
        if (size < 32) {
            size = 32;
        }
        if (size > 0x1000000) {
            size = 0x1000000;
        }
        this.binaryBufferSize = size;
        return this;
    }

    public int getBufferSize() {
        return this.binaryBufferSize;
    }

    public EncoderFactory configureBlockSize(int size) {
        if (size < 64) {
            size = 64;
        }
        if (size > 0x40000000) {
            size = 0x40000000;
        }
        this.binaryBlockSize = size;
        return this;
    }

    public int getBlockSize() {
        return this.binaryBlockSize;
    }

    public BinaryEncoder binaryEncoder(OutputStream out, BinaryEncoder reuse) {
        if (null == reuse || !reuse.getClass().equals(BufferedBinaryEncoder.class)) {
            return new BufferedBinaryEncoder(out, this.binaryBufferSize);
        }
        return ((BufferedBinaryEncoder)reuse).configure(out, this.binaryBufferSize);
    }

    public BinaryEncoder directBinaryEncoder(OutputStream out, BinaryEncoder reuse) {
        if (null == reuse || !reuse.getClass().equals(DirectBinaryEncoder.class)) {
            return new DirectBinaryEncoder(out);
        }
        return ((DirectBinaryEncoder)reuse).configure(out);
    }

    public BinaryEncoder blockingBinaryEncoder(OutputStream out, BinaryEncoder reuse) {
        int bufferSize;
        int blockSize = this.binaryBlockSize;
        int n = bufferSize = blockSize * 2 >= this.binaryBufferSize ? 32 : this.binaryBufferSize;
        if (null == reuse || !reuse.getClass().equals(BlockingBinaryEncoder.class)) {
            return new BlockingBinaryEncoder(out, blockSize, bufferSize);
        }
        return ((BlockingBinaryEncoder)reuse).configure(out, blockSize, bufferSize);
    }

    public JsonEncoder jsonEncoder(Schema schema, OutputStream out) throws IOException {
        return new JsonEncoder(schema, out);
    }

    public JsonEncoder jsonEncoder(Schema schema, OutputStream out, boolean pretty) throws IOException {
        return new JsonEncoder(schema, out, pretty);
    }

    JsonEncoder jsonEncoder(Schema schema, JsonGenerator gen) throws IOException {
        return new JsonEncoder(schema, gen);
    }

    public ValidatingEncoder validatingEncoder(Schema schema, Encoder encoder) throws IOException {
        return new ValidatingEncoder(schema, encoder);
    }

    private static class DefaultEncoderFactory
    extends EncoderFactory {
        private DefaultEncoderFactory() {
        }

        @Override
        public EncoderFactory configureBlockSize(int size) {
            throw new AvroRuntimeException("Default EncoderFactory cannot be configured");
        }

        @Override
        public EncoderFactory configureBufferSize(int size) {
            throw new AvroRuntimeException("Default EncoderFactory cannot be configured");
        }
    }
}

