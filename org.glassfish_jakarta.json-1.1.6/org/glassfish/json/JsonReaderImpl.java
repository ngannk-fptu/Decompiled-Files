/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;
import org.glassfish.json.JsonMessages;
import org.glassfish.json.JsonParserImpl;
import org.glassfish.json.api.BufferPool;

class JsonReaderImpl
implements JsonReader {
    private final JsonParserImpl parser;
    private boolean readDone;
    private final BufferPool bufferPool;

    JsonReaderImpl(Reader reader, BufferPool bufferPool) {
        this.parser = new JsonParserImpl(reader, bufferPool);
        this.bufferPool = bufferPool;
    }

    JsonReaderImpl(InputStream in, BufferPool bufferPool) {
        this.parser = new JsonParserImpl(in, bufferPool);
        this.bufferPool = bufferPool;
    }

    JsonReaderImpl(InputStream in, Charset charset, BufferPool bufferPool) {
        this.parser = new JsonParserImpl(in, charset, bufferPool);
        this.bufferPool = bufferPool;
    }

    @Override
    public JsonStructure read() {
        if (this.readDone) {
            throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
        }
        this.readDone = true;
        if (this.parser.hasNext()) {
            try {
                JsonParser.Event e = this.parser.next();
                if (e == JsonParser.Event.START_ARRAY) {
                    return this.parser.getArray();
                }
                if (e == JsonParser.Event.START_OBJECT) {
                    return this.parser.getObject();
                }
            }
            catch (IllegalStateException ise) {
                throw new JsonParsingException(ise.getMessage(), ise, this.parser.getLastCharLocation());
            }
        }
        throw new JsonException(JsonMessages.INTERNAL_ERROR());
    }

    @Override
    public JsonObject readObject() {
        if (this.readDone) {
            throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
        }
        this.readDone = true;
        if (this.parser.hasNext()) {
            try {
                this.parser.next();
                return this.parser.getObject();
            }
            catch (IllegalStateException ise) {
                throw new JsonParsingException(ise.getMessage(), ise, this.parser.getLastCharLocation());
            }
        }
        throw new JsonException(JsonMessages.INTERNAL_ERROR());
    }

    @Override
    public JsonArray readArray() {
        if (this.readDone) {
            throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
        }
        this.readDone = true;
        if (this.parser.hasNext()) {
            try {
                this.parser.next();
                return this.parser.getArray();
            }
            catch (IllegalStateException ise) {
                throw new JsonParsingException(ise.getMessage(), ise, this.parser.getLastCharLocation());
            }
        }
        throw new JsonException(JsonMessages.INTERNAL_ERROR());
    }

    @Override
    public JsonValue readValue() {
        if (this.readDone) {
            throw new IllegalStateException(JsonMessages.READER_READ_ALREADY_CALLED());
        }
        this.readDone = true;
        if (this.parser.hasNext()) {
            try {
                this.parser.next();
                return this.parser.getValue();
            }
            catch (IllegalStateException ise) {
                throw new JsonParsingException(ise.getMessage(), ise, this.parser.getLastCharLocation());
            }
        }
        throw new JsonException(JsonMessages.INTERNAL_ERROR());
    }

    @Override
    public void close() {
        this.readDone = true;
        this.parser.close();
    }
}

