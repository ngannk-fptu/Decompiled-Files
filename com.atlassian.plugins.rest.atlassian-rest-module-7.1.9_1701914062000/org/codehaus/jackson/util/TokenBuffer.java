/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.SerializableString;
import org.codehaus.jackson.impl.JsonParserMinimalBase;
import org.codehaus.jackson.impl.JsonReadContext;
import org.codehaus.jackson.impl.JsonWriteContext;
import org.codehaus.jackson.io.SerializedString;
import org.codehaus.jackson.util.ByteArrayBuilder;

public class TokenBuffer
extends JsonGenerator {
    protected static final int DEFAULT_PARSER_FEATURES = JsonParser.Feature.collectDefaults();
    protected ObjectCodec _objectCodec;
    protected int _generatorFeatures;
    protected boolean _closed;
    protected Segment _first;
    protected Segment _last;
    protected int _appendOffset;
    protected JsonWriteContext _writeContext;

    public TokenBuffer(ObjectCodec codec) {
        this._objectCodec = codec;
        this._generatorFeatures = DEFAULT_PARSER_FEATURES;
        this._writeContext = JsonWriteContext.createRootContext();
        this._first = this._last = new Segment();
        this._appendOffset = 0;
    }

    public JsonParser asParser() {
        return this.asParser(this._objectCodec);
    }

    public JsonParser asParser(ObjectCodec codec) {
        return new Parser(this._first, codec);
    }

    public JsonParser asParser(JsonParser src) {
        Parser p = new Parser(this._first, src.getCodec());
        p.setLocation(src.getTokenLocation());
        return p;
    }

    public void serialize(JsonGenerator jgen) throws IOException, JsonGenerationException {
        Segment segment = this._first;
        int ptr = -1;
        while (true) {
            JsonToken t;
            if (++ptr >= 16) {
                ptr = 0;
                if ((segment = segment.next()) == null) break;
            }
            if ((t = segment.type(ptr)) == null) break;
            switch (t) {
                case START_OBJECT: {
                    jgen.writeStartObject();
                    break;
                }
                case END_OBJECT: {
                    jgen.writeEndObject();
                    break;
                }
                case START_ARRAY: {
                    jgen.writeStartArray();
                    break;
                }
                case END_ARRAY: {
                    jgen.writeEndArray();
                    break;
                }
                case FIELD_NAME: {
                    Object ob = segment.get(ptr);
                    if (ob instanceof SerializableString) {
                        jgen.writeFieldName((SerializableString)ob);
                        break;
                    }
                    jgen.writeFieldName((String)ob);
                    break;
                }
                case VALUE_STRING: {
                    Object ob = segment.get(ptr);
                    if (ob instanceof SerializableString) {
                        jgen.writeString((SerializableString)ob);
                        break;
                    }
                    jgen.writeString((String)ob);
                    break;
                }
                case VALUE_NUMBER_INT: {
                    Object n = (Number)segment.get(ptr);
                    if (n instanceof BigInteger) {
                        jgen.writeNumber((BigInteger)n);
                        break;
                    }
                    if (n instanceof Long) {
                        jgen.writeNumber(((Number)n).longValue());
                        break;
                    }
                    jgen.writeNumber(((Number)n).intValue());
                    break;
                }
                case VALUE_NUMBER_FLOAT: {
                    Object n = segment.get(ptr);
                    if (n instanceof BigDecimal) {
                        jgen.writeNumber((BigDecimal)n);
                        break;
                    }
                    if (n instanceof Float) {
                        jgen.writeNumber(((Float)n).floatValue());
                        break;
                    }
                    if (n instanceof Double) {
                        jgen.writeNumber((Double)n);
                        break;
                    }
                    if (n == null) {
                        jgen.writeNull();
                        break;
                    }
                    if (n instanceof String) {
                        jgen.writeNumber((String)n);
                        break;
                    }
                    throw new JsonGenerationException("Unrecognized value type for VALUE_NUMBER_FLOAT: " + n.getClass().getName() + ", can not serialize");
                }
                case VALUE_TRUE: {
                    jgen.writeBoolean(true);
                    break;
                }
                case VALUE_FALSE: {
                    jgen.writeBoolean(false);
                    break;
                }
                case VALUE_NULL: {
                    jgen.writeNull();
                    break;
                }
                case VALUE_EMBEDDED_OBJECT: {
                    jgen.writeObject(segment.get(ptr));
                    break;
                }
                default: {
                    throw new RuntimeException("Internal error: should never end up through this code path");
                }
            }
        }
    }

    public String toString() {
        int MAX_COUNT = 100;
        StringBuilder sb = new StringBuilder();
        sb.append("[TokenBuffer: ");
        JsonParser jp = this.asParser();
        int count = 0;
        while (true) {
            JsonToken t;
            try {
                t = jp.nextToken();
            }
            catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
            if (t == null) break;
            if (count < 100) {
                if (count > 0) {
                    sb.append(", ");
                }
                sb.append(t.toString());
            }
            ++count;
        }
        if (count >= 100) {
            sb.append(" ... (truncated ").append(count - 100).append(" entries)");
        }
        sb.append(']');
        return sb.toString();
    }

    public JsonGenerator enable(JsonGenerator.Feature f) {
        this._generatorFeatures |= f.getMask();
        return this;
    }

    public JsonGenerator disable(JsonGenerator.Feature f) {
        this._generatorFeatures &= ~f.getMask();
        return this;
    }

    public boolean isEnabled(JsonGenerator.Feature f) {
        return (this._generatorFeatures & f.getMask()) != 0;
    }

    public JsonGenerator useDefaultPrettyPrinter() {
        return this;
    }

    public JsonGenerator setCodec(ObjectCodec oc) {
        this._objectCodec = oc;
        return this;
    }

    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    public final JsonWriteContext getOutputContext() {
        return this._writeContext;
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
        this._closed = true;
    }

    public boolean isClosed() {
        return this._closed;
    }

    public final void writeStartArray() throws IOException, JsonGenerationException {
        this._append(JsonToken.START_ARRAY);
        this._writeContext = this._writeContext.createChildArrayContext();
    }

    public final void writeEndArray() throws IOException, JsonGenerationException {
        this._append(JsonToken.END_ARRAY);
        JsonWriteContext c = this._writeContext.getParent();
        if (c != null) {
            this._writeContext = c;
        }
    }

    public final void writeStartObject() throws IOException, JsonGenerationException {
        this._append(JsonToken.START_OBJECT);
        this._writeContext = this._writeContext.createChildObjectContext();
    }

    public final void writeEndObject() throws IOException, JsonGenerationException {
        this._append(JsonToken.END_OBJECT);
        JsonWriteContext c = this._writeContext.getParent();
        if (c != null) {
            this._writeContext = c;
        }
    }

    public final void writeFieldName(String name) throws IOException, JsonGenerationException {
        this._append(JsonToken.FIELD_NAME, name);
        this._writeContext.writeFieldName(name);
    }

    public void writeFieldName(SerializableString name) throws IOException, JsonGenerationException {
        this._append(JsonToken.FIELD_NAME, name);
        this._writeContext.writeFieldName(name.getValue());
    }

    public void writeFieldName(SerializedString name) throws IOException, JsonGenerationException {
        this._append(JsonToken.FIELD_NAME, name);
        this._writeContext.writeFieldName(name.getValue());
    }

    public void writeString(String text) throws IOException, JsonGenerationException {
        if (text == null) {
            this.writeNull();
        } else {
            this._append(JsonToken.VALUE_STRING, text);
        }
    }

    public void writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        this.writeString(new String(text, offset, len));
    }

    public void writeString(SerializableString text) throws IOException, JsonGenerationException {
        if (text == null) {
            this.writeNull();
        } else {
            this._append(JsonToken.VALUE_STRING, text);
        }
    }

    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeRaw(String text) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeRaw(String text, int offset, int len) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeRaw(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeRaw(char c) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeRawValue(String text) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeRawValue(String text, int offset, int len) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeRawValue(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeNumber(int i) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_INT, i);
    }

    public void writeNumber(long l) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_INT, l);
    }

    public void writeNumber(double d) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_FLOAT, d);
    }

    public void writeNumber(float f) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_FLOAT, Float.valueOf(f));
    }

    public void writeNumber(BigDecimal dec) throws IOException, JsonGenerationException {
        if (dec == null) {
            this.writeNull();
        } else {
            this._append(JsonToken.VALUE_NUMBER_FLOAT, dec);
        }
    }

    public void writeNumber(BigInteger v) throws IOException, JsonGenerationException {
        if (v == null) {
            this.writeNull();
        } else {
            this._append(JsonToken.VALUE_NUMBER_INT, v);
        }
    }

    public void writeNumber(String encodedValue) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
    }

    public void writeBoolean(boolean state) throws IOException, JsonGenerationException {
        this._append(state ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE);
    }

    public void writeNull() throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NULL);
    }

    public void writeObject(Object value) throws IOException, JsonProcessingException {
        this._append(JsonToken.VALUE_EMBEDDED_OBJECT, value);
    }

    public void writeTree(JsonNode rootNode) throws IOException, JsonProcessingException {
        this._append(JsonToken.VALUE_EMBEDDED_OBJECT, rootNode);
    }

    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
        byte[] copy = new byte[len];
        System.arraycopy(data, offset, copy, 0, len);
        this.writeObject(copy);
    }

    public void copyCurrentEvent(JsonParser jp) throws IOException, JsonProcessingException {
        block0 : switch (jp.getCurrentToken()) {
            case START_OBJECT: {
                this.writeStartObject();
                break;
            }
            case END_OBJECT: {
                this.writeEndObject();
                break;
            }
            case START_ARRAY: {
                this.writeStartArray();
                break;
            }
            case END_ARRAY: {
                this.writeEndArray();
                break;
            }
            case FIELD_NAME: {
                this.writeFieldName(jp.getCurrentName());
                break;
            }
            case VALUE_STRING: {
                if (jp.hasTextCharacters()) {
                    this.writeString(jp.getTextCharacters(), jp.getTextOffset(), jp.getTextLength());
                    break;
                }
                this.writeString(jp.getText());
                break;
            }
            case VALUE_NUMBER_INT: {
                switch (jp.getNumberType()) {
                    case INT: {
                        this.writeNumber(jp.getIntValue());
                        break block0;
                    }
                    case BIG_INTEGER: {
                        this.writeNumber(jp.getBigIntegerValue());
                        break block0;
                    }
                }
                this.writeNumber(jp.getLongValue());
                break;
            }
            case VALUE_NUMBER_FLOAT: {
                switch (jp.getNumberType()) {
                    case BIG_DECIMAL: {
                        this.writeNumber(jp.getDecimalValue());
                        break block0;
                    }
                    case FLOAT: {
                        this.writeNumber(jp.getFloatValue());
                        break block0;
                    }
                }
                this.writeNumber(jp.getDoubleValue());
                break;
            }
            case VALUE_TRUE: {
                this.writeBoolean(true);
                break;
            }
            case VALUE_FALSE: {
                this.writeBoolean(false);
                break;
            }
            case VALUE_NULL: {
                this.writeNull();
                break;
            }
            case VALUE_EMBEDDED_OBJECT: {
                this.writeObject(jp.getEmbeddedObject());
                break;
            }
            default: {
                throw new RuntimeException("Internal error: should never end up through this code path");
            }
        }
    }

    public void copyCurrentStructure(JsonParser jp) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.FIELD_NAME) {
            this.writeFieldName(jp.getCurrentName());
            t = jp.nextToken();
        }
        switch (t) {
            case START_ARRAY: {
                this.writeStartArray();
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    this.copyCurrentStructure(jp);
                }
                this.writeEndArray();
                break;
            }
            case START_OBJECT: {
                this.writeStartObject();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    this.copyCurrentStructure(jp);
                }
                this.writeEndObject();
                break;
            }
            default: {
                this.copyCurrentEvent(jp);
            }
        }
    }

    protected final void _append(JsonToken type) {
        Segment next = this._last.append(this._appendOffset, type);
        if (next == null) {
            ++this._appendOffset;
        } else {
            this._last = next;
            this._appendOffset = 1;
        }
    }

    protected final void _append(JsonToken type, Object value) {
        Segment next = this._last.append(this._appendOffset, type, value);
        if (next == null) {
            ++this._appendOffset;
        } else {
            this._last = next;
            this._appendOffset = 1;
        }
    }

    protected void _reportUnsupportedOperation() {
        throw new UnsupportedOperationException("Called operation not supported for TokenBuffer");
    }

    protected static final class Segment {
        public static final int TOKENS_PER_SEGMENT = 16;
        private static final JsonToken[] TOKEN_TYPES_BY_INDEX = new JsonToken[16];
        protected Segment _next;
        protected long _tokenTypes;
        protected final Object[] _tokens = new Object[16];

        public JsonToken type(int index) {
            long l = this._tokenTypes;
            if (index > 0) {
                l >>= index << 2;
            }
            int ix = (int)l & 0xF;
            return TOKEN_TYPES_BY_INDEX[ix];
        }

        public Object get(int index) {
            return this._tokens[index];
        }

        public Segment next() {
            return this._next;
        }

        public Segment append(int index, JsonToken tokenType) {
            if (index < 16) {
                this.set(index, tokenType);
                return null;
            }
            this._next = new Segment();
            this._next.set(0, tokenType);
            return this._next;
        }

        public Segment append(int index, JsonToken tokenType, Object value) {
            if (index < 16) {
                this.set(index, tokenType, value);
                return null;
            }
            this._next = new Segment();
            this._next.set(0, tokenType, value);
            return this._next;
        }

        public void set(int index, JsonToken tokenType) {
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }

        public void set(int index, JsonToken tokenType, Object value) {
            this._tokens[index] = value;
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }

        static {
            JsonToken[] t = JsonToken.values();
            System.arraycopy(t, 1, TOKEN_TYPES_BY_INDEX, 1, Math.min(15, t.length - 1));
        }
    }

    protected static final class Parser
    extends JsonParserMinimalBase {
        protected ObjectCodec _codec;
        protected Segment _segment;
        protected int _segmentPtr;
        protected JsonReadContext _parsingContext;
        protected boolean _closed;
        protected transient ByteArrayBuilder _byteBuilder;
        protected JsonLocation _location = null;

        public Parser(Segment firstSeg, ObjectCodec codec) {
            super(0);
            this._segment = firstSeg;
            this._segmentPtr = -1;
            this._codec = codec;
            this._parsingContext = JsonReadContext.createRootContext(-1, -1);
        }

        public void setLocation(JsonLocation l) {
            this._location = l;
        }

        public ObjectCodec getCodec() {
            return this._codec;
        }

        public void setCodec(ObjectCodec c) {
            this._codec = c;
        }

        public JsonToken peekNextToken() throws IOException, JsonParseException {
            if (this._closed) {
                return null;
            }
            Segment seg = this._segment;
            int ptr = this._segmentPtr + 1;
            if (ptr >= 16) {
                ptr = 0;
                seg = seg == null ? null : seg.next();
            }
            return seg == null ? null : seg.type(ptr);
        }

        public void close() throws IOException {
            if (!this._closed) {
                this._closed = true;
            }
        }

        public JsonToken nextToken() throws IOException, JsonParseException {
            if (this._closed || this._segment == null) {
                return null;
            }
            if (++this._segmentPtr >= 16) {
                this._segmentPtr = 0;
                this._segment = this._segment.next();
                if (this._segment == null) {
                    return null;
                }
            }
            this._currToken = this._segment.type(this._segmentPtr);
            if (this._currToken == JsonToken.FIELD_NAME) {
                Object ob = this._currentObject();
                String name = ob instanceof String ? (String)ob : ob.toString();
                this._parsingContext.setCurrentName(name);
            } else if (this._currToken == JsonToken.START_OBJECT) {
                this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
            } else if (this._currToken == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(-1, -1);
            } else if (this._currToken == JsonToken.END_OBJECT || this._currToken == JsonToken.END_ARRAY) {
                this._parsingContext = this._parsingContext.getParent();
                if (this._parsingContext == null) {
                    this._parsingContext = JsonReadContext.createRootContext(-1, -1);
                }
            }
            return this._currToken;
        }

        public boolean isClosed() {
            return this._closed;
        }

        public JsonStreamContext getParsingContext() {
            return this._parsingContext;
        }

        public JsonLocation getTokenLocation() {
            return this.getCurrentLocation();
        }

        public JsonLocation getCurrentLocation() {
            return this._location == null ? JsonLocation.NA : this._location;
        }

        public String getCurrentName() {
            return this._parsingContext.getCurrentName();
        }

        public String getText() {
            if (this._currToken == JsonToken.VALUE_STRING || this._currToken == JsonToken.FIELD_NAME) {
                Object ob = this._currentObject();
                if (ob instanceof String) {
                    return (String)ob;
                }
                return ob == null ? null : ob.toString();
            }
            if (this._currToken == null) {
                return null;
            }
            switch (this._currToken) {
                case VALUE_NUMBER_INT: 
                case VALUE_NUMBER_FLOAT: {
                    Object ob = this._currentObject();
                    return ob == null ? null : ob.toString();
                }
            }
            return this._currToken.asString();
        }

        public char[] getTextCharacters() {
            String str = this.getText();
            return str == null ? null : str.toCharArray();
        }

        public int getTextLength() {
            String str = this.getText();
            return str == null ? 0 : str.length();
        }

        public int getTextOffset() {
            return 0;
        }

        public boolean hasTextCharacters() {
            return false;
        }

        public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
            Number n = this.getNumberValue();
            if (n instanceof BigInteger) {
                return (BigInteger)n;
            }
            switch (this.getNumberType()) {
                case BIG_DECIMAL: {
                    return ((BigDecimal)n).toBigInteger();
                }
            }
            return BigInteger.valueOf(n.longValue());
        }

        public BigDecimal getDecimalValue() throws IOException, JsonParseException {
            Number n = this.getNumberValue();
            if (n instanceof BigDecimal) {
                return (BigDecimal)n;
            }
            switch (this.getNumberType()) {
                case INT: 
                case LONG: {
                    return BigDecimal.valueOf(n.longValue());
                }
                case BIG_INTEGER: {
                    return new BigDecimal((BigInteger)n);
                }
            }
            return BigDecimal.valueOf(n.doubleValue());
        }

        public double getDoubleValue() throws IOException, JsonParseException {
            return this.getNumberValue().doubleValue();
        }

        public float getFloatValue() throws IOException, JsonParseException {
            return this.getNumberValue().floatValue();
        }

        public int getIntValue() throws IOException, JsonParseException {
            if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
                return ((Number)this._currentObject()).intValue();
            }
            return this.getNumberValue().intValue();
        }

        public long getLongValue() throws IOException, JsonParseException {
            return this.getNumberValue().longValue();
        }

        public JsonParser.NumberType getNumberType() throws IOException, JsonParseException {
            Number n = this.getNumberValue();
            if (n instanceof Integer) {
                return JsonParser.NumberType.INT;
            }
            if (n instanceof Long) {
                return JsonParser.NumberType.LONG;
            }
            if (n instanceof Double) {
                return JsonParser.NumberType.DOUBLE;
            }
            if (n instanceof BigDecimal) {
                return JsonParser.NumberType.BIG_DECIMAL;
            }
            if (n instanceof Float) {
                return JsonParser.NumberType.FLOAT;
            }
            if (n instanceof BigInteger) {
                return JsonParser.NumberType.BIG_INTEGER;
            }
            return null;
        }

        public final Number getNumberValue() throws IOException, JsonParseException {
            this._checkIsNumber();
            return (Number)this._currentObject();
        }

        public Object getEmbeddedObject() {
            if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                return this._currentObject();
            }
            return null;
        }

        public byte[] getBinaryValue(Base64Variant b64variant) throws IOException, JsonParseException {
            Object ob;
            if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT && (ob = this._currentObject()) instanceof byte[]) {
                return (byte[])ob;
            }
            if (this._currToken != JsonToken.VALUE_STRING) {
                throw this._constructError("Current token (" + (Object)((Object)this._currToken) + ") not VALUE_STRING (or VALUE_EMBEDDED_OBJECT with byte[]), can not access as binary");
            }
            String str = this.getText();
            if (str == null) {
                return null;
            }
            ByteArrayBuilder builder = this._byteBuilder;
            if (builder == null) {
                this._byteBuilder = builder = new ByteArrayBuilder(100);
            } else {
                this._byteBuilder.reset();
            }
            this._decodeBase64(str, builder, b64variant);
            return builder.toByteArray();
        }

        protected final Object _currentObject() {
            return this._segment.get(this._segmentPtr);
        }

        protected final void _checkIsNumber() throws JsonParseException {
            if (this._currToken == null || !this._currToken.isNumeric()) {
                throw this._constructError("Current token (" + (Object)((Object)this._currToken) + ") not numeric, can not use numeric value accessors");
            }
        }

        protected void _handleEOF() throws JsonParseException {
            this._throwInternal();
        }
    }
}

