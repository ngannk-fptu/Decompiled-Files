/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.Base64Variant
 *  org.codehaus.jackson.JsonLocation
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonParseException
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonParser$Feature
 *  org.codehaus.jackson.JsonParser$NumberType
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonStreamContext
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.ObjectCodec
 *  org.codehaus.jackson.type.TypeReference
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

    public void enableFeature(JsonParser.Feature feature) {
        this.parser.enableFeature(feature);
    }

    public void disableFeature(JsonParser.Feature feature) {
        this.parser.disableFeature(feature);
    }

    public void setFeature(JsonParser.Feature feature, boolean isSet) {
        this.parser.setFeature(feature, isSet);
    }

    public JsonToken nextValue() throws IOException, JsonParseException {
        JsonToken result = this.nextToken();
        while (!result.isScalarValue()) {
            result = this.nextToken();
        }
        return result;
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    public byte getByteValue() throws IOException, JsonParseException {
        return this.parser.getByteValue();
    }

    public short getShortValue() throws IOException, JsonParseException {
        return this.parser.getShortValue();
    }

    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
        return this.parser.getBigIntegerValue();
    }

    public float getFloatValue() throws IOException, JsonParseException {
        return this.parser.getFloatValue();
    }

    public byte[] getBinaryValue(Base64Variant base64Variant) throws IOException, JsonParseException {
        return this.parser.getBinaryValue(base64Variant);
    }

    public <T> T readValueAs(Class<T> type) throws IOException, JsonProcessingException {
        return (T)this.parser.readValueAs(type);
    }

    public <T> T readValueAs(TypeReference<?> typeRef) throws IOException, JsonProcessingException {
        return (T)this.parser.readValueAs(typeRef);
    }

    public JsonNode readValueAsTree() throws IOException, JsonProcessingException {
        return this.parser.readValueAsTree();
    }

    public JsonStreamContext getParsingContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

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

    public JsonParser skipChildren() throws IOException, JsonParseException {
        return this.parser.skipChildren();
    }

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

    public void close() throws IOException {
        this.parser.close();
    }

    public JsonLocation getTokenLocation() {
        return this.parser.getTokenLocation();
    }

    public JsonLocation getCurrentLocation() {
        return this.parser.getCurrentLocation();
    }

    public String getText() throws IOException, JsonParseException {
        return this.parser.getText();
    }

    public char[] getTextCharacters() throws IOException, JsonParseException {
        return this.parser.getTextCharacters();
    }

    public int getTextLength() throws IOException, JsonParseException {
        return this.parser.getTextLength();
    }

    public int getTextOffset() throws IOException, JsonParseException {
        return this.parser.getTextOffset();
    }

    public Number getNumberValue() throws IOException, JsonParseException {
        return this.parser.getNumberValue();
    }

    public JsonParser.NumberType getNumberType() throws IOException, JsonParseException {
        return this.parser.getNumberType();
    }

    public int getIntValue() throws IOException, JsonParseException {
        return this.parser.getIntValue();
    }

    public long getLongValue() throws IOException, JsonParseException {
        return this.parser.getLongValue();
    }

    public double getDoubleValue() throws IOException, JsonParseException {
        return this.parser.getDoubleValue();
    }

    public BigDecimal getDecimalValue() throws IOException, JsonParseException {
        return this.parser.getDecimalValue();
    }

    public ObjectCodec getCodec() {
        return this.parser.getCodec();
    }

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

