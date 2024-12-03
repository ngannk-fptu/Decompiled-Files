/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.Base64Variant
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonGenerator$Feature
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonStreamContext
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.ObjectCodec
 */
package com.sun.jersey.json.impl.writer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.ObjectCodec;

public class JacksonArrayWrapperGenerator
extends JsonGenerator {
    State state = State.START;
    int depth = 0;
    final JsonGenerator generator;
    private boolean isClosed = false;
    final int arrayDepth;

    private JacksonArrayWrapperGenerator() {
        this(null, 0);
    }

    private JacksonArrayWrapperGenerator(JsonGenerator generator) {
        this(generator, 0);
    }

    private JacksonArrayWrapperGenerator(JsonGenerator generator, int arrayDepth) {
        this.generator = generator;
        this.arrayDepth = arrayDepth;
    }

    public static JsonGenerator createArrayWrapperGenerator(JsonGenerator g) {
        return new JacksonArrayWrapperGenerator(g);
    }

    public static JsonGenerator createArrayWrapperGenerator(JsonGenerator g, int arrayDepth) {
        return new JacksonArrayWrapperGenerator(g, arrayDepth);
    }

    private void aboutToWriteANonNull() throws IOException {
        if (this.depth == this.arrayDepth) {
            if (this.state == State.START) {
                this.generator.writeStartArray();
                this.state = State.IN_THE_MIDDLE;
            } else if (this.state == State.AFTER_NULL) {
                this.generator.writeStartArray();
                this.generator.writeNull();
                this.state = State.IN_THE_MIDDLE;
            }
        }
    }

    @Deprecated
    public void enableFeature(JsonGenerator.Feature feature) {
        this.generator.enableFeature(feature);
    }

    public JsonGenerator enable(JsonGenerator.Feature feature) {
        return this.generator.enable(feature);
    }

    @Deprecated
    public void disableFeature(JsonGenerator.Feature feature) {
        this.generator.disableFeature(feature);
    }

    public JsonGenerator disable(JsonGenerator.Feature feature) {
        return this.generator.disable(feature);
    }

    public void setFeature(JsonGenerator.Feature feature, boolean enabled) {
        this.generator.setFeature(feature, enabled);
    }

    @Deprecated
    public boolean isFeatureEnabled(JsonGenerator.Feature feature) {
        return this.generator.isFeatureEnabled(feature);
    }

    public boolean isEnabled(JsonGenerator.Feature f) {
        return this.generator.isEnabled(f);
    }

    public JsonGenerator useDefaultPrettyPrinter() {
        return this.generator.useDefaultPrettyPrinter();
    }

    public JsonGenerator setCodec(ObjectCodec codec) {
        return this.generator.setCodec(codec);
    }

    public ObjectCodec getCodec() {
        return this.generator.getCodec();
    }

    public void writeRawValue(String rawString) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRawValue(rawString);
    }

    public void writeRawValue(String rawString, int startIndex, int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRawValue(rawString, startIndex, length);
    }

    public void writeRawValue(char[] rawChars, int startIndex, int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRaw(rawChars, startIndex, length);
    }

    public void writeNumber(BigInteger number) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(number);
    }

    public void writeObject(Object o) throws IOException, JsonProcessingException {
        this.aboutToWriteANonNull();
        this.generator.writeObject(o);
    }

    public void writeTree(JsonNode node) throws IOException, JsonProcessingException {
        this.aboutToWriteANonNull();
        this.generator.writeTree(node);
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    public JsonStreamContext getOutputContext() {
        return this.generator.getOutputContext();
    }

    public void writeStartArray() throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeStartArray();
    }

    public void writeEndArray() throws IOException, JsonGenerationException {
        this.generator.writeEndArray();
    }

    public void writeStartObject() throws IOException, JsonGenerationException {
        if (this.arrayDepth > 0 && this.depth == this.arrayDepth) {
            this.generator.writeStartArray();
            this.generator.writeStartObject();
        } else {
            this.aboutToWriteANonNull();
            this.generator.writeStartObject();
        }
        ++this.depth;
    }

    public void writeEndObject() throws IOException, JsonGenerationException {
        if (this.arrayDepth > 0 && this.depth == this.arrayDepth) {
            this.generator.writeEndObject();
            this.generator.writeEndArray();
        } else {
            this.generator.writeEndObject();
        }
        --this.depth;
    }

    public void writeFieldName(String name) throws IOException, JsonGenerationException {
        this.generator.writeFieldName(name);
    }

    public void writeString(String s) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeString(s);
    }

    public void writeString(char[] text, int start, int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeString(text, start, length);
    }

    public void writeRawUTF8String(byte[] bytes, int start, int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRawUTF8String(bytes, start, length);
    }

    public void writeUTF8String(byte[] bytes, int start, int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeUTF8String(bytes, start, length);
    }

    public void writeRaw(String raw) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRaw(raw);
    }

    public void writeRaw(String raw, int start, int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRaw(raw, start, length);
    }

    public void writeRaw(char[] raw, int start, int count) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRaw(raw, start, count);
    }

    public void writeRaw(char c) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRaw(c);
    }

    public void writeBinary(Base64Variant variant, byte[] bytes, int start, int count) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeBinary(variant, bytes, start, count);
    }

    public void writeNumber(int i) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(i);
    }

    public void writeNumber(long l) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(l);
    }

    public void writeNumber(double d) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(d);
    }

    public void writeNumber(float f) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(f);
    }

    public void writeNumber(BigDecimal bd) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(bd);
    }

    public void writeNumber(String number) throws IOException, JsonGenerationException, UnsupportedOperationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(number);
    }

    public void writeBoolean(boolean b) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeBoolean(b);
    }

    public void writeNull() throws IOException, JsonGenerationException {
        switch (this.state) {
            case START: {
                this.state = State.AFTER_NULL;
                break;
            }
            case AFTER_NULL: {
                this.generator.writeStartArray();
                this.generator.writeNull();
                this.state = State.IN_THE_MIDDLE;
            }
            default: {
                this.generator.writeNull();
            }
        }
    }

    public void copyCurrentEvent(JsonParser parser) throws IOException, JsonProcessingException {
        if (JsonToken.VALUE_NULL != parser.getCurrentToken()) {
            this.aboutToWriteANonNull();
            this.generator.copyCurrentEvent(parser);
        } else {
            this.writeNull();
        }
    }

    public void copyCurrentStructure(JsonParser parser) throws IOException, JsonProcessingException {
        this.generator.copyCurrentStructure(parser);
    }

    public void flush() throws IOException {
        if (this.depth == this.arrayDepth) {
            switch (this.state) {
                case IN_THE_MIDDLE: {
                    this.generator.writeEndArray();
                    break;
                }
                case START: 
                case AFTER_NULL: {
                    this.generator.writeStartArray();
                    this.generator.writeEndArray();
                }
            }
        }
        this.generator.flush();
    }

    public void close() throws IOException {
        this.flush();
        this.generator.close();
        this.isClosed = true;
    }

    private static enum State {
        START,
        AFTER_NULL,
        IN_THE_MIDDLE;

    }
}

