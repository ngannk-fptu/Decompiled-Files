/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.message;

import java.io.IOException;
import java.io.InputStream;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.message.MessageDecoder;

public class RawMessageDecoder<D>
extends MessageDecoder.BaseDecoder<D> {
    private static final ThreadLocal<BinaryDecoder> DECODER = new ThreadLocal();
    private final DatumReader<D> reader;

    public RawMessageDecoder(GenericData model, Schema schema) {
        this(model, schema, schema);
    }

    public RawMessageDecoder(GenericData model, Schema writeSchema, Schema readSchema) {
        Schema writeSchema1 = writeSchema;
        Schema readSchema1 = readSchema;
        this.reader = model.createDatumReader(writeSchema1, readSchema1);
    }

    @Override
    public D decode(InputStream stream, D reuse) {
        BinaryDecoder decoder = DecoderFactory.get().directBinaryDecoder(stream, DECODER.get());
        DECODER.set(decoder);
        try {
            return this.reader.read(reuse, decoder);
        }
        catch (IOException e) {
            throw new AvroRuntimeException("Decoding datum failed", e);
        }
    }
}

