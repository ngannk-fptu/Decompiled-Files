/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.analytics.event.serialization;

import com.atlassian.analytics.EventMessage;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

public class EventSerializer {
    private final DatumWriter<EventMessage> spw = new SpecificDatumWriter<EventMessage>(EventMessage.SCHEMA$);
    public static final int SCHEMA_ID = 2;

    public void serialize(EventMessage message, OutputStream out) throws IOException {
        try (DataFileWriter<EventMessage> dataWriter = new DataFileWriter<EventMessage>(this.spw);){
            dataWriter.create(EventMessage.SCHEMA$, out);
            dataWriter.append(message);
        }
    }

    public void serializeWithSchemaId(@Nonnull EventMessage message, @Nonnull OutputStream out) throws IOException {
        Preconditions.checkNotNull((Object)message, (Object)"Input EventMessage must NOT be null!");
        Preconditions.checkNotNull((Object)out, (Object)"OutputStream must NOT be null!");
        out.write(ByteBuffer.allocate(4).putInt(2).array());
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        this.spw.write(message, encoder);
        encoder.flush();
    }
}

