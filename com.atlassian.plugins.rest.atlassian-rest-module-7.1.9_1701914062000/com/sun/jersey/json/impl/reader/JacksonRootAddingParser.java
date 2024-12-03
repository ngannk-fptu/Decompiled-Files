/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.reader;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.type.TypeReference;

public class JacksonRootAddingParser
extends JsonParser {
    String rootName;
    JsonParser parser;
    State state;
    boolean isClosed = false;

    public static JsonParser createRootAddingParser(JsonParser parser, String rootName) {
        return new JacksonRootAddingParser(parser, rootName);
    }

    private JacksonRootAddingParser() {
    }

    private JacksonRootAddingParser(JsonParser parser, String rootName) {
        this.parser = parser;
        this.state = State.START;
        this.rootName = rootName;
    }

    @Override
    public void enableFeature(JsonParser.Feature feature) {
        this.parser.enableFeature(feature);
    }

    @Override
    public void disableFeature(JsonParser.Feature feature) {
        this.parser.disableFeature(feature);
    }

    @Override
    public void setFeature(JsonParser.Feature feature, boolean isSet) {
        this.parser.setFeature(feature, isSet);
    }

    @Override
    public JsonToken nextValue() throws IOException, JsonParseException {
        JsonToken result = this.nextToken();
        while (!result.isScalarValue()) {
            result = this.nextToken();
        }
        return result;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    @Override
    public byte getByteValue() throws IOException, JsonParseException {
        return this.parser.getByteValue();
    }

    @Override
    public short getShortValue() throws IOException, JsonParseException {
        return this.parser.getShortValue();
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
        return this.parser.getBigIntegerValue();
    }

    @Override
    public float getFloatValue() throws IOException, JsonParseException {
        return this.parser.getFloatValue();
    }

    @Override
    public byte[] getBinaryValue(Base64Variant base64Variant) throws IOException, JsonParseException {
        return this.parser.getBinaryValue(base64Variant);
    }

    @Override
    public <T> T readValueAs(Class<T> type) throws IOException, JsonProcessingException {
        return this.parser.readValueAs(type);
    }

    @Override
    public <T> T readValueAs(TypeReference<?> typeRef) throws IOException, JsonProcessingException {
        return this.parser.readValueAs(typeRef);
    }

    @Override
    public JsonNode readValueAsTree() throws IOException, JsonProcessingException {
        return this.parser.readValueAsTree();
    }

    @Override
    public JsonStreamContext getParsingContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        switch (this.state) {
            case START: {
                this.state = State.AFTER_SO;
                this._currToken = JsonToken.START_OBJECT;
                return this._currToken;
            }
            case AFTER_SO: {
                this.state = State.AFTER_FN;
                this._currToken = JsonToken.FIELD_NAME;
                return this._currToken;
            }
            case AFTER_FN: {
                this.state = State.INNER;
            }
            case INNER: {
                this._currToken = this.parser.nextToken();
                if (this._currToken == null) {
                    this.state = State.END;
                    this._currToken = JsonToken.END_OBJECT;
                }
                return this._currToken;
            }
        }
        this._currToken = null;
        return this._currToken;
    }

    @Override
    public JsonParser skipChildren() throws IOException, JsonParseException {
        return this.parser.skipChildren();
    }

    @Override
    public String getCurrentName() throws IOException, JsonParseException {
        switch (this.state) {
            case START: {
                return null;
            }
            case AFTER_SO: {
                return null;
            }
            case AFTER_FN: {
                return this.rootName;
            }
            case INNER: {
                return this.parser.getCurrentName();
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        this.parser.close();
    }

    @Override
    public JsonLocation getTokenLocation() {
        return this.parser.getTokenLocation();
    }

    @Override
    public JsonLocation getCurrentLocation() {
        return this.parser.getCurrentLocation();
    }

    @Override
    public String getText() throws IOException, JsonParseException {
        return this.parser.getText();
    }

    @Override
    public char[] getTextCharacters() throws IOException, JsonParseException {
        return this.parser.getTextCharacters();
    }

    @Override
    public int getTextLength() throws IOException, JsonParseException {
        return this.parser.getTextLength();
    }

    @Override
    public int getTextOffset() throws IOException, JsonParseException {
        return this.parser.getTextOffset();
    }

    @Override
    public Number getNumberValue() throws IOException, JsonParseException {
        return this.parser.getNumberValue();
    }

    @Override
    public JsonParser.NumberType getNumberType() throws IOException, JsonParseException {
        return this.parser.getNumberType();
    }

    @Override
    public int getIntValue() throws IOException, JsonParseException {
        return this.parser.getIntValue();
    }

    @Override
    public long getLongValue() throws IOException, JsonParseException {
        return this.parser.getLongValue();
    }

    @Override
    public double getDoubleValue() throws IOException, JsonParseException {
        return this.parser.getDoubleValue();
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException, JsonParseException {
        return this.parser.getDecimalValue();
    }

    @Override
    public ObjectCodec getCodec() {
        return this.parser.getCodec();
    }

    @Override
    public void setCodec(ObjectCodec c) {
        this.parser.setCodec(c);
    }

    static enum State {
        START,
        AFTER_SO,
        AFTER_FN,
        INNER,
        END;

    }
}

