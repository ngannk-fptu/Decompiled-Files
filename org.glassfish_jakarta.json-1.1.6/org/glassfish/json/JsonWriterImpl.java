/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import org.glassfish.json.JsonGeneratorImpl;
import org.glassfish.json.JsonMessages;
import org.glassfish.json.JsonPrettyGeneratorImpl;
import org.glassfish.json.api.BufferPool;

class JsonWriterImpl
implements JsonWriter {
    private final JsonGeneratorImpl generator;
    private boolean writeDone;
    private final NoFlushOutputStream os;

    JsonWriterImpl(Writer writer, BufferPool bufferPool) {
        this(writer, false, bufferPool);
    }

    JsonWriterImpl(Writer writer, boolean prettyPrinting, BufferPool bufferPool) {
        this.generator = prettyPrinting ? new JsonPrettyGeneratorImpl(writer, bufferPool) : new JsonGeneratorImpl(writer, bufferPool);
        this.os = null;
    }

    JsonWriterImpl(OutputStream out, BufferPool bufferPool) {
        this(out, StandardCharsets.UTF_8, false, bufferPool);
    }

    JsonWriterImpl(OutputStream out, boolean prettyPrinting, BufferPool bufferPool) {
        this(out, StandardCharsets.UTF_8, prettyPrinting, bufferPool);
    }

    JsonWriterImpl(OutputStream out, Charset charset, boolean prettyPrinting, BufferPool bufferPool) {
        this.os = new NoFlushOutputStream(out);
        this.generator = prettyPrinting ? new JsonPrettyGeneratorImpl(this.os, charset, bufferPool) : new JsonGeneratorImpl(this.os, charset, bufferPool);
    }

    @Override
    public void writeArray(JsonArray array) {
        if (this.writeDone) {
            throw new IllegalStateException(JsonMessages.WRITER_WRITE_ALREADY_CALLED());
        }
        this.writeDone = true;
        this.generator.writeStartArray();
        for (JsonValue value : array) {
            this.generator.write(value);
        }
        this.generator.writeEnd();
        this.generator.flushBuffer();
        if (this.os != null) {
            this.generator.flush();
        }
    }

    @Override
    public void writeObject(JsonObject object) {
        if (this.writeDone) {
            throw new IllegalStateException(JsonMessages.WRITER_WRITE_ALREADY_CALLED());
        }
        this.writeDone = true;
        this.generator.writeStartObject();
        for (Map.Entry e : object.entrySet()) {
            this.generator.write((String)e.getKey(), (JsonValue)e.getValue());
        }
        this.generator.writeEnd();
        this.generator.flushBuffer();
        if (this.os != null) {
            this.generator.flush();
        }
    }

    @Override
    public void write(JsonStructure value) {
        if (value instanceof JsonArray) {
            this.writeArray((JsonArray)value);
        } else {
            this.writeObject((JsonObject)value);
        }
    }

    @Override
    public void write(JsonValue value) {
        switch (value.getValueType()) {
            case OBJECT: {
                this.writeObject((JsonObject)value);
                return;
            }
            case ARRAY: {
                this.writeArray((JsonArray)value);
                return;
            }
        }
        if (this.writeDone) {
            throw new IllegalStateException(JsonMessages.WRITER_WRITE_ALREADY_CALLED());
        }
        this.writeDone = true;
        this.generator.write(value);
        this.generator.flushBuffer();
        if (this.os != null) {
            this.generator.flush();
        }
    }

    @Override
    public void close() {
        this.writeDone = true;
        this.generator.close();
    }

    private static final class NoFlushOutputStream
    extends FilterOutputStream {
        public NoFlushOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.out.write(b, off, len);
        }

        @Override
        public void flush() {
        }
    }
}

