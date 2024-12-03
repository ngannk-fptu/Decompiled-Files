/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.analytics.event.serialization;

import com.atlassian.analytics.EventMessage;
import com.atlassian.analytics.event.serialization.SchemaProvider;
import com.google.common.base.Preconditions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;

public class EventDeserializer {
    private static final int BYTES_PER_INT = 4;
    final DatumReader<EventMessage> dataReader = new Utf16SpecificDatumReader<EventMessage>(EventMessage.SCHEMA$);
    final DatumReader<EventMessage> legacyDataReader;

    public EventDeserializer() {
        Schema legacySchema;
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("legacy-event.avsc");
        try {
            Schema.Parser parser = new Schema.Parser();
            legacySchema = parser.parse(is);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException iOException) {}
        }
        this.legacyDataReader = new Utf16SpecificDatumReader<EventMessage>(legacySchema, EventMessage.SCHEMA$);
    }

    public EventMessage deserialize(byte[] data) throws IOException {
        EventMessage eventMessage;
        DataFileStream<EventMessage> dataStream = new DataFileStream<EventMessage>(new ByteArrayInputStream(data), this.dataReader);
        try {
            eventMessage = dataStream.next();
        }
        catch (NoSuchElementException e) {
            try {
                try {
                    throw new IllegalArgumentException("Data did not contain any serialised event.");
                }
                catch (Throwable throwable) {
                    dataStream.close();
                    throw throwable;
                }
            }
            catch (IOException e2) {
                if (!e2.getMessage().contains("Not a data file")) {
                    throw e2;
                }
                BinaryDecoder decoder = DecoderFactory.get().directBinaryDecoder(new ByteArrayInputStream(data), null);
                return this.legacyDataReader.read(null, decoder);
            }
        }
        dataStream.close();
        return eventMessage;
    }

    public EventMessage deserialize(@Nonnull byte[] data, @Nonnull SchemaProvider schemaProvider) throws IOException {
        Preconditions.checkNotNull((Object)data, (Object)"Input byte array must NOT be null!");
        if (data.length == 0) {
            throw new IllegalArgumentException("Input byte array must not be empty!");
        }
        Preconditions.checkNotNull((Object)schemaProvider, (Object)"Input SchemaProvider must NOT be null!");
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        int schemaId = byteBuffer.getInt();
        try (ByteArrayInputStream nonSchemaIdByteStream = new ByteArrayInputStream(data, 4, data.length - 4);){
            BinaryDecoder decoder = DecoderFactory.get().directBinaryDecoder(nonSchemaIdByteStream, null);
            Utf16SpecificDatumReader datumReader = new Utf16SpecificDatumReader(schemaProvider.get(schemaId), EventMessage.SCHEMA$);
            EventMessage eventMessage = datumReader.read(null, decoder);
            return eventMessage;
        }
    }

    private static class Utf16SpecificDatumReader<T>
    extends SpecificDatumReader<T> {
        public Utf16SpecificDatumReader() {
            this(null, null, SpecificData.get());
        }

        public Utf16SpecificDatumReader(Class<T> c) {
            this(SpecificData.get().getSchema(c));
        }

        public Utf16SpecificDatumReader(Schema schema) {
            this(schema, schema, SpecificData.get());
        }

        public Utf16SpecificDatumReader(Schema writer, Schema reader) {
            this(writer, reader, SpecificData.get());
        }

        public Utf16SpecificDatumReader(@Nullable Schema writer, @Nullable Schema reader, SpecificData data) {
            super(writer, reader, data);
        }

        @Override
        protected Object readString(Object old, Decoder in) throws IOException {
            Object orig = super.readString(old, in);
            return orig == null ? null : orig.toString();
        }
    }
}

