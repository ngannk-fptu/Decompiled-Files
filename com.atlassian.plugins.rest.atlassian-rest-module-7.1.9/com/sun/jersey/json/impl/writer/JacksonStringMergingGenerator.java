/*
 * Decompiled with CFR 0.152.
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
import org.codehaus.jackson.ObjectCodec;

public class JacksonStringMergingGenerator
extends JsonGenerator {
    JsonGenerator generator;
    boolean isClosed;
    String previousString;

    private JacksonStringMergingGenerator() {
    }

    private JacksonStringMergingGenerator(JsonGenerator generator) {
        this.generator = generator;
    }

    public static JacksonStringMergingGenerator createGenerator(JsonGenerator g) {
        return new JacksonStringMergingGenerator(g);
    }

    @Override
    @Deprecated
    public void enableFeature(JsonGenerator.Feature feature) {
        this.generator.enableFeature(feature);
    }

    @Override
    public JsonGenerator enable(JsonGenerator.Feature feature) {
        return this.generator.enable(feature);
    }

    @Override
    @Deprecated
    public void disableFeature(JsonGenerator.Feature feature) {
        this.generator.disableFeature(feature);
    }

    @Override
    public JsonGenerator disable(JsonGenerator.Feature feature) {
        return this.generator.disable(feature);
    }

    @Override
    public void setFeature(JsonGenerator.Feature feature, boolean enabled) {
        this.generator.setFeature(feature, enabled);
    }

    @Override
    @Deprecated
    public boolean isFeatureEnabled(JsonGenerator.Feature feature) {
        return this.generator.isFeatureEnabled(feature);
    }

    @Override
    public boolean isEnabled(JsonGenerator.Feature f) {
        return this.generator.isEnabled(f);
    }

    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        return this.generator.useDefaultPrettyPrinter();
    }

    @Override
    public void writeStartArray() throws IOException, JsonGenerationException {
        this.generator.writeStartArray();
    }

    @Override
    public void writeEndArray() throws IOException, JsonGenerationException {
        this.flushPreviousString();
        this.generator.writeEndArray();
    }

    @Override
    public void writeStartObject() throws IOException, JsonGenerationException {
        this.generator.writeStartObject();
    }

    @Override
    public void writeEndObject() throws IOException, JsonGenerationException {
        this.flushPreviousString();
        this.generator.writeEndObject();
    }

    @Override
    public void writeFieldName(String name) throws IOException, JsonGenerationException {
        this.flushPreviousString();
        this.generator.writeFieldName(name);
    }

    @Override
    public void writeString(String s) throws IOException, JsonGenerationException {
        this.generator.writeString(s);
    }

    public void writeStringToMerge(String s) throws IOException, JsonGenerationException {
        this.previousString = this.previousString == null ? s : this.previousString + s;
    }

    @Override
    public void writeString(char[] text, int start, int length) throws IOException, JsonGenerationException {
        this.generator.writeString(text, start, length);
    }

    @Override
    public void writeRawUTF8String(byte[] bytes, int start, int length) throws IOException, JsonGenerationException {
        this.generator.writeRawUTF8String(bytes, start, length);
    }

    @Override
    public void writeUTF8String(byte[] bytes, int start, int length) throws IOException, JsonGenerationException {
        this.generator.writeUTF8String(bytes, start, length);
    }

    @Override
    public void writeRaw(String raw) throws IOException, JsonGenerationException {
        this.generator.writeRaw(raw);
    }

    @Override
    public void writeRaw(String raw, int start, int length) throws IOException, JsonGenerationException {
        this.generator.writeRaw(raw, start, length);
    }

    @Override
    public void writeRaw(char[] raw, int start, int count) throws IOException, JsonGenerationException {
        this.generator.writeRaw(raw, start, count);
    }

    @Override
    public void writeRaw(char c) throws IOException, JsonGenerationException {
        this.generator.writeRaw(c);
    }

    @Override
    public void writeBinary(Base64Variant variant, byte[] bytes, int start, int count) throws IOException, JsonGenerationException {
        this.generator.writeBinary(variant, bytes, start, count);
    }

    @Override
    public void writeNumber(int i) throws IOException, JsonGenerationException {
        this.generator.writeNumber(i);
    }

    @Override
    public void writeNumber(long l) throws IOException, JsonGenerationException {
        this.generator.writeNumber(l);
    }

    @Override
    public void writeNumber(double d) throws IOException, JsonGenerationException {
        this.generator.writeNumber(d);
    }

    @Override
    public void writeNumber(float f) throws IOException, JsonGenerationException {
        this.generator.writeNumber(f);
    }

    @Override
    public void writeNumber(BigDecimal bd) throws IOException, JsonGenerationException {
        this.generator.writeNumber(bd);
    }

    @Override
    public void writeNumber(String number) throws IOException, JsonGenerationException, UnsupportedOperationException {
        this.generator.writeNumber(number);
    }

    @Override
    public void writeBoolean(boolean b) throws IOException, JsonGenerationException {
        this.generator.writeBoolean(b);
    }

    @Override
    public void writeNull() throws IOException, JsonGenerationException {
        this.generator.writeNull();
    }

    @Override
    public void copyCurrentEvent(JsonParser parser) throws IOException, JsonProcessingException {
        this.flushPreviousString();
        this.generator.copyCurrentEvent(parser);
    }

    @Override
    public void copyCurrentStructure(JsonParser parser) throws IOException, JsonProcessingException {
        this.flushPreviousString();
        this.generator.copyCurrentStructure(parser);
    }

    @Override
    public void flush() throws IOException {
        this.generator.flush();
    }

    @Override
    public void close() throws IOException {
        this.generator.close();
        this.isClosed = true;
    }

    @Override
    public JsonGenerator setCodec(ObjectCodec codec) {
        return this.generator.setCodec(codec);
    }

    @Override
    public ObjectCodec getCodec() {
        return this.generator.getCodec();
    }

    @Override
    public void writeRawValue(String rawString) throws IOException, JsonGenerationException {
        this.generator.writeRawValue(rawString);
    }

    @Override
    public void writeRawValue(String rawString, int startIndex, int length) throws IOException, JsonGenerationException {
        this.generator.writeRawValue(rawString, startIndex, length);
    }

    @Override
    public void writeRawValue(char[] rawChars, int startIndex, int length) throws IOException, JsonGenerationException {
        this.generator.writeRawValue(rawChars, startIndex, length);
    }

    @Override
    public void writeNumber(BigInteger number) throws IOException, JsonGenerationException {
        this.generator.writeNumber(number);
    }

    @Override
    public void writeObject(Object o) throws IOException, JsonProcessingException {
        this.generator.writeObject(o);
    }

    @Override
    public void writeTree(JsonNode node) throws IOException, JsonProcessingException {
        this.generator.writeTree(node);
    }

    @Override
    public JsonStreamContext getOutputContext() {
        return this.generator.getOutputContext();
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    private void flushPreviousString() throws IOException {
        if (this.previousString != null) {
            this.generator.writeString(this.previousString);
            this.previousString = null;
        }
    }
}

