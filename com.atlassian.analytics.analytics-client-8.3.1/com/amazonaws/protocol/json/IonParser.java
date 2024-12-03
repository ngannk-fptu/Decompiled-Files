/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.SdkClientException;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonType;

final class IonParser
extends JsonParser {
    private final IonReader reader;
    private final boolean shouldCloseReader;
    private State state = State.BEFORE_VALUE;
    private JsonToken currentToken;
    private JsonToken lastClearedToken;
    private boolean shouldSkipContainer;
    private boolean closed;

    public IonParser(IonReader reader, boolean shouldCloseReader) {
        super(JsonParser.Feature.collectDefaults());
        this.reader = reader;
        this.shouldCloseReader = shouldCloseReader;
    }

    @Override
    public ObjectCodec getCodec() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCodec(ObjectCodec c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Version version() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        if (this.shouldCloseReader) {
            this.reader.close();
        } else if (JsonParser.Feature.AUTO_CLOSE_SOURCE.enabledIn(this._features)) {
            this.reader.close();
        }
        this.closed = true;
    }

    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        this.currentToken = this.doNextToken();
        return this.currentToken;
    }

    private JsonToken doNextToken() {
        block7: while (true) {
            switch (this.state) {
                case BEFORE_VALUE: {
                    IonType currentType = this.reader.next();
                    if (currentType == null) {
                        boolean topLevel;
                        boolean bl = topLevel = this.reader.getDepth() == 0;
                        if (topLevel) {
                            this.state = State.EOF;
                            continue block7;
                        }
                        this.state = State.END_OF_CONTAINER;
                        return this.reader.isInStruct() ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
                    }
                    if (this.reader.isInStruct()) {
                        this.state = State.FIELD_NAME;
                        return JsonToken.FIELD_NAME;
                    }
                    this.state = State.VALUE;
                    return this.getJsonToken();
                }
                case END_OF_CONTAINER: {
                    this.reader.stepOut();
                    this.state = State.BEFORE_VALUE;
                    continue block7;
                }
                case EOF: {
                    return null;
                }
                case FIELD_NAME: {
                    this.state = State.VALUE;
                    return this.getJsonToken();
                }
                case VALUE: {
                    this.state = State.BEFORE_VALUE;
                    if (IonType.isContainer(this.reader.getType()) && !this.reader.isNullValue() && !this.shouldSkipContainer) {
                        this.reader.stepIn();
                    }
                    this.shouldSkipContainer = false;
                    continue block7;
                }
            }
        }
    }

    @Override
    public JsonToken nextValue() throws IOException, JsonParseException {
        JsonToken token = this.nextToken();
        return token == JsonToken.FIELD_NAME ? this.nextToken() : token;
    }

    @Override
    public JsonParser skipChildren() throws IOException, JsonParseException {
        IonType currentType = this.reader.getType();
        if (IonType.isContainer(currentType)) {
            this.shouldSkipContainer = true;
            this.currentToken = currentType == IonType.STRUCT ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
        }
        return this;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public JsonToken getCurrentToken() {
        return this.currentToken;
    }

    @Override
    public int getCurrentTokenId() {
        return this.currentToken == null ? 0 : this.currentToken.id();
    }

    @Override
    public boolean hasCurrentToken() {
        return this.currentToken != null;
    }

    @Override
    public boolean hasTokenId(int id) {
        return this.getCurrentTokenId() == id;
    }

    @Override
    public boolean hasToken(JsonToken t) {
        return this.currentToken == t;
    }

    @Override
    public String getCurrentName() throws IOException {
        return this.reader.getFieldName();
    }

    @Override
    public JsonStreamContext getParsingContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonLocation getTokenLocation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonLocation getCurrentLocation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearCurrentToken() {
        this.lastClearedToken = this.currentToken;
        this.currentToken = null;
    }

    @Override
    public JsonToken getLastClearedToken() {
        return this.lastClearedToken;
    }

    @Override
    public void overrideCurrentName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getText() throws IOException {
        if (this.state == State.FIELD_NAME) {
            return this.reader.getFieldName();
        }
        if (IonType.isText(this.reader.getType())) {
            return this.reader.stringValue();
        }
        if (this.currentToken == null) {
            return null;
        }
        if (this.currentToken.isNumeric()) {
            return this.getNumberValue().toString();
        }
        return this.currentToken.asString();
    }

    @Override
    public char[] getTextCharacters() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTextLength() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTextOffset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasTextCharacters() {
        return false;
    }

    @Override
    public Number getNumberValue() throws IOException {
        JsonParser.NumberType numberType = this.getNumberType();
        if (numberType == null) {
            throw new SdkClientException(String.format("Unable to get number value for non-numeric token %s", new Object[]{this.reader.getType()}));
        }
        switch (numberType) {
            case BIG_DECIMAL: {
                return this.reader.bigDecimalValue();
            }
            case BIG_INTEGER: {
                return this.reader.bigIntegerValue();
            }
            case DOUBLE: {
                return this.reader.doubleValue();
            }
        }
        throw new SdkClientException(String.format("Unable to get number value for number type %s", new Object[]{numberType}));
    }

    @Override
    public JsonParser.NumberType getNumberType() throws IOException {
        switch (this.reader.getType()) {
            case DECIMAL: {
                return JsonParser.NumberType.BIG_DECIMAL;
            }
            case FLOAT: {
                return JsonParser.NumberType.DOUBLE;
            }
            case INT: {
                return JsonParser.NumberType.BIG_INTEGER;
            }
        }
        return null;
    }

    @Override
    public int getIntValue() throws IOException {
        return this.reader.intValue();
    }

    @Override
    public long getLongValue() throws IOException {
        return this.reader.longValue();
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException {
        return this.reader.bigIntegerValue();
    }

    @Override
    public float getFloatValue() throws IOException {
        return (float)this.reader.doubleValue();
    }

    @Override
    public double getDoubleValue() throws IOException {
        return this.reader.doubleValue();
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException {
        return this.reader.decimalValue();
    }

    @Override
    public Object getEmbeddedObject() throws IOException {
        if (this.currentToken != JsonToken.VALUE_EMBEDDED_OBJECT) {
            return null;
        }
        IonType currentType = this.reader.getType();
        switch (currentType) {
            case BLOB: 
            case CLOB: {
                return ByteBuffer.wrap(this.reader.newBytes());
            }
            case TIMESTAMP: {
                return this.reader.timestampValue().dateValue();
            }
        }
        throw new SdkClientException(String.format("Cannot return embedded object for Ion type %s", new Object[]{currentType}));
    }

    @Override
    public byte[] getBinaryValue(Base64Variant bv) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getValueAsString(String defaultValue) throws IOException {
        if (!(this.currentToken == JsonToken.VALUE_STRING || this.currentToken != null && this.currentToken != JsonToken.VALUE_NULL && this.currentToken.isScalarValue())) {
            return defaultValue;
        }
        return this.getText();
    }

    private JsonToken getJsonToken() {
        if (this.reader.isNullValue()) {
            return JsonToken.VALUE_NULL;
        }
        IonType currentType = this.reader.getType();
        switch (currentType) {
            case BLOB: 
            case CLOB: {
                return JsonToken.VALUE_EMBEDDED_OBJECT;
            }
            case BOOL: {
                return this.reader.booleanValue() ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
            }
            case DECIMAL: {
                return JsonToken.VALUE_NUMBER_FLOAT;
            }
            case FLOAT: {
                return JsonToken.VALUE_NUMBER_FLOAT;
            }
            case INT: {
                return JsonToken.VALUE_NUMBER_INT;
            }
            case LIST: {
                return JsonToken.START_ARRAY;
            }
            case SEXP: {
                return JsonToken.START_ARRAY;
            }
            case STRING: {
                return JsonToken.VALUE_STRING;
            }
            case STRUCT: {
                return JsonToken.START_OBJECT;
            }
            case SYMBOL: {
                return JsonToken.VALUE_STRING;
            }
            case TIMESTAMP: {
                return JsonToken.VALUE_EMBEDDED_OBJECT;
            }
        }
        throw new SdkClientException(String.format("Unhandled Ion type %s", new Object[]{currentType}));
    }

    private static enum State {
        BEFORE_VALUE,
        END_OF_CONTAINER,
        EOF,
        FIELD_NAME,
        VALUE;

    }
}

