/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.Base64Variant
 *  com.fasterxml.jackson.core.JacksonException
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.core.JsonGenerator$Feature
 *  com.fasterxml.jackson.core.JsonLocation
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonParser$NumberType
 *  com.fasterxml.jackson.core.JsonStreamContext
 *  com.fasterxml.jackson.core.JsonToken
 *  com.fasterxml.jackson.core.ObjectCodec
 *  com.fasterxml.jackson.core.SerializableString
 *  com.fasterxml.jackson.core.StreamReadCapability
 *  com.fasterxml.jackson.core.StreamReadConstraints
 *  com.fasterxml.jackson.core.StreamReadFeature
 *  com.fasterxml.jackson.core.StreamWriteCapability
 *  com.fasterxml.jackson.core.TreeNode
 *  com.fasterxml.jackson.core.Version
 *  com.fasterxml.jackson.core.base.ParserMinimalBase
 *  com.fasterxml.jackson.core.io.NumberInput
 *  com.fasterxml.jackson.core.json.JsonWriteContext
 *  com.fasterxml.jackson.core.util.ByteArrayBuilder
 *  com.fasterxml.jackson.core.util.JacksonFeatureSet
 */
package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteCapability;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.RawValue;
import com.fasterxml.jackson.databind.util.TokenBufferReadContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TreeMap;

public class TokenBuffer
extends JsonGenerator {
    protected static final int DEFAULT_GENERATOR_FEATURES = JsonGenerator.Feature.collectDefaults();
    protected ObjectCodec _objectCodec;
    protected JsonStreamContext _parentContext;
    protected int _generatorFeatures;
    protected StreamReadConstraints _streamReadConstraints = StreamReadConstraints.defaults();
    protected boolean _closed;
    protected boolean _hasNativeTypeIds;
    protected boolean _hasNativeObjectIds;
    protected boolean _mayHaveNativeIds;
    protected boolean _forceBigDecimal;
    protected Segment _first;
    protected Segment _last;
    protected int _appendAt;
    protected Object _typeId;
    protected Object _objectId;
    protected boolean _hasNativeId = false;
    protected JsonWriteContext _writeContext;

    public TokenBuffer(ObjectCodec codec, boolean hasNativeIds) {
        this._objectCodec = codec;
        this._generatorFeatures = DEFAULT_GENERATOR_FEATURES;
        this._writeContext = JsonWriteContext.createRootContext(null);
        this._first = this._last = new Segment();
        this._appendAt = 0;
        this._hasNativeTypeIds = hasNativeIds;
        this._hasNativeObjectIds = hasNativeIds;
        this._mayHaveNativeIds = this._hasNativeTypeIds || this._hasNativeObjectIds;
    }

    public TokenBuffer(JsonParser p) {
        this(p, null);
    }

    public TokenBuffer(JsonParser p, DeserializationContext ctxt) {
        this._objectCodec = p.getCodec();
        this._streamReadConstraints = p.streamReadConstraints();
        this._parentContext = p.getParsingContext();
        this._generatorFeatures = DEFAULT_GENERATOR_FEATURES;
        this._writeContext = JsonWriteContext.createRootContext(null);
        this._first = this._last = new Segment();
        this._appendAt = 0;
        this._hasNativeTypeIds = p.canReadTypeId();
        this._hasNativeObjectIds = p.canReadObjectId();
        this._mayHaveNativeIds = this._hasNativeTypeIds || this._hasNativeObjectIds;
        this._forceBigDecimal = ctxt == null ? false : ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }

    @Deprecated
    public static TokenBuffer asCopyOfValue(JsonParser p) throws IOException {
        TokenBuffer b = new TokenBuffer(p);
        b.copyCurrentStructure(p);
        return b;
    }

    public TokenBuffer overrideParentContext(JsonStreamContext ctxt) {
        this._parentContext = ctxt;
        return this;
    }

    public TokenBuffer forceUseOfBigDecimal(boolean b) {
        this._forceBigDecimal = b;
        return this;
    }

    public Version version() {
        return PackageVersion.VERSION;
    }

    public JsonParser asParser() {
        return this.asParser(this._objectCodec);
    }

    public JsonParser asParserOnFirstToken() throws IOException {
        JsonParser p = this.asParser(this._objectCodec);
        p.nextToken();
        return p;
    }

    public JsonParser asParser(ObjectCodec codec) {
        return new Parser(this._first, codec, this._hasNativeTypeIds, this._hasNativeObjectIds, this._parentContext, this._streamReadConstraints);
    }

    public JsonParser asParser(StreamReadConstraints streamReadConstraints) {
        return new Parser(this._first, this._objectCodec, this._hasNativeTypeIds, this._hasNativeObjectIds, this._parentContext, streamReadConstraints);
    }

    public JsonParser asParser(JsonParser src) {
        Parser p = new Parser(this._first, src.getCodec(), this._hasNativeTypeIds, this._hasNativeObjectIds, this._parentContext, src.streamReadConstraints());
        p.setLocation(src.getTokenLocation());
        return p;
    }

    public JsonToken firstToken() {
        return this._first.type(0);
    }

    public boolean isEmpty() {
        return this._appendAt == 0 && this._first == this._last;
    }

    public TokenBuffer append(TokenBuffer other) throws IOException {
        if (!this._hasNativeTypeIds) {
            this._hasNativeTypeIds = other.canWriteTypeId();
        }
        if (!this._hasNativeObjectIds) {
            this._hasNativeObjectIds = other.canWriteObjectId();
        }
        this._mayHaveNativeIds = this._hasNativeTypeIds || this._hasNativeObjectIds;
        JsonParser p = other.asParser();
        while (p.nextToken() != null) {
            this.copyCurrentStructure(p);
        }
        return this;
    }

    public void serialize(JsonGenerator gen) throws IOException {
        boolean hasIds;
        Segment segment = this._first;
        int ptr = -1;
        boolean checkIds = this._mayHaveNativeIds;
        boolean bl = hasIds = checkIds && segment.hasIds();
        while (true) {
            JsonToken t;
            if (++ptr >= 16) {
                ptr = 0;
                if ((segment = segment.next()) == null) break;
                boolean bl2 = hasIds = checkIds && segment.hasIds();
            }
            if ((t = segment.type(ptr)) == null) break;
            if (hasIds) {
                Object id = segment.findObjectId(ptr);
                if (id != null) {
                    gen.writeObjectId(id);
                }
                if ((id = segment.findTypeId(ptr)) != null) {
                    gen.writeTypeId(id);
                }
            }
            switch (t) {
                case START_OBJECT: {
                    gen.writeStartObject();
                    break;
                }
                case END_OBJECT: {
                    gen.writeEndObject();
                    break;
                }
                case START_ARRAY: {
                    gen.writeStartArray();
                    break;
                }
                case END_ARRAY: {
                    gen.writeEndArray();
                    break;
                }
                case FIELD_NAME: {
                    Object ob = segment.get(ptr);
                    if (ob instanceof SerializableString) {
                        gen.writeFieldName((SerializableString)ob);
                        break;
                    }
                    gen.writeFieldName((String)ob);
                    break;
                }
                case VALUE_STRING: {
                    Object ob = segment.get(ptr);
                    if (ob instanceof SerializableString) {
                        gen.writeString((SerializableString)ob);
                        break;
                    }
                    gen.writeString((String)ob);
                    break;
                }
                case VALUE_NUMBER_INT: {
                    Object n = segment.get(ptr);
                    if (n instanceof Integer) {
                        gen.writeNumber(((Integer)n).intValue());
                        break;
                    }
                    if (n instanceof BigInteger) {
                        gen.writeNumber((BigInteger)n);
                        break;
                    }
                    if (n instanceof Long) {
                        gen.writeNumber(((Long)n).longValue());
                        break;
                    }
                    if (n instanceof Short) {
                        gen.writeNumber(((Short)n).shortValue());
                        break;
                    }
                    gen.writeNumber(((Number)n).intValue());
                    break;
                }
                case VALUE_NUMBER_FLOAT: {
                    Object n = segment.get(ptr);
                    if (n instanceof Double) {
                        gen.writeNumber(((Double)n).doubleValue());
                        break;
                    }
                    if (n instanceof BigDecimal) {
                        gen.writeNumber((BigDecimal)n);
                        break;
                    }
                    if (n instanceof Float) {
                        gen.writeNumber(((Float)n).floatValue());
                        break;
                    }
                    if (n == null) {
                        gen.writeNull();
                        break;
                    }
                    if (n instanceof String) {
                        gen.writeNumber((String)n);
                        break;
                    }
                    this._reportError(String.format("Unrecognized value type for VALUE_NUMBER_FLOAT: %s, cannot serialize", n.getClass().getName()));
                    break;
                }
                case VALUE_TRUE: {
                    gen.writeBoolean(true);
                    break;
                }
                case VALUE_FALSE: {
                    gen.writeBoolean(false);
                    break;
                }
                case VALUE_NULL: {
                    gen.writeNull();
                    break;
                }
                case VALUE_EMBEDDED_OBJECT: {
                    Object value = segment.get(ptr);
                    if (value instanceof RawValue) {
                        ((RawValue)value).serialize(gen);
                        break;
                    }
                    if (value instanceof JsonSerializable) {
                        gen.writeObject(value);
                        break;
                    }
                    gen.writeEmbeddedObject(value);
                    break;
                }
                default: {
                    throw new RuntimeException("Internal error: should never end up through this code path");
                }
            }
        }
    }

    public TokenBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t;
        if (!p.hasToken(JsonToken.FIELD_NAME)) {
            this.copyCurrentStructure(p);
            return this;
        }
        this.writeStartObject();
        do {
            this.copyCurrentStructure(p);
        } while ((t = p.nextToken()) == JsonToken.FIELD_NAME);
        if (t != JsonToken.END_OBJECT) {
            ctxt.reportWrongTokenException(TokenBuffer.class, JsonToken.END_OBJECT, "Expected END_OBJECT after copying contents of a JsonParser into TokenBuffer, got " + t, new Object[0]);
        }
        this.writeEndObject();
        return this;
    }

    public String toString() {
        int MAX_COUNT = 100;
        StringBuilder sb = new StringBuilder();
        sb.append("[TokenBuffer: ");
        JsonParser jp = this.asParser();
        int count = 0;
        boolean hasNativeIds = this._hasNativeTypeIds || this._hasNativeObjectIds;
        while (true) {
            try {
                JsonToken t = jp.nextToken();
                if (t == null) break;
                if (hasNativeIds) {
                    this._appendNativeIds(sb);
                }
                if (count < 100) {
                    if (count > 0) {
                        sb.append(", ");
                    }
                    sb.append(t.toString());
                    if (t == JsonToken.FIELD_NAME) {
                        sb.append('(');
                        sb.append(jp.currentName());
                        sb.append(')');
                    }
                }
            }
            catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
            ++count;
        }
        if (count >= 100) {
            sb.append(" ... (truncated ").append(count - 100).append(" entries)");
        }
        sb.append(']');
        return sb.toString();
    }

    private final void _appendNativeIds(StringBuilder sb) {
        Object typeId;
        Object objectId = this._last.findObjectId(this._appendAt - 1);
        if (objectId != null) {
            sb.append("[objectId=").append(String.valueOf(objectId)).append(']');
        }
        if ((typeId = this._last.findTypeId(this._appendAt - 1)) != null) {
            sb.append("[typeId=").append(String.valueOf(typeId)).append(']');
        }
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

    public int getFeatureMask() {
        return this._generatorFeatures;
    }

    @Deprecated
    public JsonGenerator setFeatureMask(int mask) {
        this._generatorFeatures = mask;
        return this;
    }

    public JsonGenerator overrideStdFeatures(int values, int mask) {
        int oldState = this.getFeatureMask();
        this._generatorFeatures = oldState & ~mask | values & mask;
        return this;
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

    public boolean canWriteBinaryNatively() {
        return true;
    }

    public JacksonFeatureSet<StreamWriteCapability> getWriteCapabilities() {
        return DEFAULT_WRITE_CAPABILITIES;
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
        this._closed = true;
    }

    public boolean isClosed() {
        return this._closed;
    }

    public final void writeStartArray() throws IOException {
        this._writeContext.writeValue();
        this._appendStartMarker(JsonToken.START_ARRAY);
        this._writeContext = this._writeContext.createChildArrayContext();
    }

    public void writeStartArray(Object forValue) throws IOException {
        this._writeContext.writeValue();
        this._appendStartMarker(JsonToken.START_ARRAY);
        this._writeContext = this._writeContext.createChildArrayContext(forValue);
    }

    public void writeStartArray(Object forValue, int size) throws IOException {
        this._writeContext.writeValue();
        this._appendStartMarker(JsonToken.START_ARRAY);
        this._writeContext = this._writeContext.createChildArrayContext(forValue);
    }

    public final void writeEndArray() throws IOException {
        this._appendEndMarker(JsonToken.END_ARRAY);
        JsonWriteContext c = this._writeContext.getParent();
        if (c != null) {
            this._writeContext = c;
        }
    }

    public final void writeStartObject() throws IOException {
        this._writeContext.writeValue();
        this._appendStartMarker(JsonToken.START_OBJECT);
        this._writeContext = this._writeContext.createChildObjectContext();
    }

    public void writeStartObject(Object forValue) throws IOException {
        this._writeContext.writeValue();
        this._appendStartMarker(JsonToken.START_OBJECT);
        this._writeContext = this._writeContext.createChildObjectContext(forValue);
    }

    public void writeStartObject(Object forValue, int size) throws IOException {
        this._writeContext.writeValue();
        this._appendStartMarker(JsonToken.START_OBJECT);
        this._writeContext = this._writeContext.createChildObjectContext(forValue);
    }

    public final void writeEndObject() throws IOException {
        this._appendEndMarker(JsonToken.END_OBJECT);
        JsonWriteContext c = this._writeContext.getParent();
        if (c != null) {
            this._writeContext = c;
        }
    }

    public final void writeFieldName(String name) throws IOException {
        this._writeContext.writeFieldName(name);
        this._appendFieldName(name);
    }

    public void writeFieldName(SerializableString name) throws IOException {
        this._writeContext.writeFieldName(name.getValue());
        this._appendFieldName(name);
    }

    public void writeString(String text) throws IOException {
        if (text == null) {
            this.writeNull();
        } else {
            this._appendValue(JsonToken.VALUE_STRING, text);
        }
    }

    public void writeString(char[] text, int offset, int len) throws IOException {
        this.writeString(new String(text, offset, len));
    }

    public void writeString(SerializableString text) throws IOException {
        if (text == null) {
            this.writeNull();
        } else {
            this._appendValue(JsonToken.VALUE_STRING, text);
        }
    }

    public void writeString(Reader reader, int len) throws IOException {
        int toReadNow;
        int toRead;
        int numRead;
        if (reader == null) {
            this._reportError("null reader");
        }
        char[] buf = new char[1000];
        StringBuilder sb = new StringBuilder(1000);
        for (toRead = len >= 0 ? len : Integer.MAX_VALUE; toRead > 0 && (numRead = reader.read(buf, 0, toReadNow = Math.min(toRead, buf.length))) > 0; toRead -= numRead) {
            sb.append(buf, 0, numRead);
        }
        if (toRead > 0 && len >= 0) {
            this._reportError("Was not able to read enough from reader");
        }
        this._appendValue(JsonToken.VALUE_STRING, sb.toString());
    }

    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
        this._reportUnsupportedOperation();
    }

    public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
        this._reportUnsupportedOperation();
    }

    public void writeRaw(String text) throws IOException {
        this._reportUnsupportedOperation();
    }

    public void writeRaw(String text, int offset, int len) throws IOException {
        this._reportUnsupportedOperation();
    }

    public void writeRaw(SerializableString text) throws IOException {
        this._reportUnsupportedOperation();
    }

    public void writeRaw(char[] text, int offset, int len) throws IOException {
        this._reportUnsupportedOperation();
    }

    public void writeRaw(char c) throws IOException {
        this._reportUnsupportedOperation();
    }

    public void writeRawValue(String text) throws IOException {
        this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
    }

    public void writeRawValue(String text, int offset, int len) throws IOException {
        if (offset > 0 || len != text.length()) {
            text = text.substring(offset, offset + len);
        }
        this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
    }

    public void writeRawValue(char[] text, int offset, int len) throws IOException {
        this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new String(text, offset, len));
    }

    public void writeNumber(short i) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_INT, i);
    }

    public void writeNumber(int i) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_INT, i);
    }

    public void writeNumber(long l) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_INT, l);
    }

    public void writeNumber(double d) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_FLOAT, d);
    }

    public void writeNumber(float f) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_FLOAT, Float.valueOf(f));
    }

    public void writeNumber(BigDecimal dec) throws IOException {
        if (dec == null) {
            this.writeNull();
        } else {
            this._appendValue(JsonToken.VALUE_NUMBER_FLOAT, dec);
        }
    }

    public void writeNumber(BigInteger v) throws IOException {
        if (v == null) {
            this.writeNull();
        } else {
            this._appendValue(JsonToken.VALUE_NUMBER_INT, v);
        }
    }

    public void writeNumber(String encodedValue) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
    }

    private void writeLazyInteger(Object encodedValue) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_INT, encodedValue);
    }

    private void writeLazyDecimal(Object encodedValue) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
    }

    public void writeBoolean(boolean state) throws IOException {
        this._appendValue(state ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE);
    }

    public void writeNull() throws IOException {
        this._appendValue(JsonToken.VALUE_NULL);
    }

    public void writeObject(Object value) throws IOException {
        if (value == null) {
            this.writeNull();
            return;
        }
        Class<?> raw = value.getClass();
        if (raw == byte[].class || value instanceof RawValue) {
            this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, value);
            return;
        }
        if (this._objectCodec == null) {
            this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, value);
        } else {
            this._objectCodec.writeValue((JsonGenerator)this, value);
        }
    }

    public void writeTree(TreeNode node) throws IOException {
        if (node == null) {
            this.writeNull();
            return;
        }
        if (this._objectCodec == null) {
            this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, node);
        } else {
            this._objectCodec.writeTree((JsonGenerator)this, node);
        }
    }

    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
        byte[] copy = new byte[len];
        System.arraycopy(data, offset, copy, 0, len);
        this.writeObject(copy);
    }

    public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) {
        throw new UnsupportedOperationException();
    }

    public boolean canWriteTypeId() {
        return this._hasNativeTypeIds;
    }

    public boolean canWriteObjectId() {
        return this._hasNativeObjectIds;
    }

    public void writeTypeId(Object id) {
        this._typeId = id;
        this._hasNativeId = true;
    }

    public void writeObjectId(Object id) {
        this._objectId = id;
        this._hasNativeId = true;
    }

    public void writeEmbeddedObject(Object object) throws IOException {
        this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, object);
    }

    public void copyCurrentEvent(JsonParser p) throws IOException {
        if (this._mayHaveNativeIds) {
            this._checkNativeIds(p);
        }
        block0 : switch (p.currentToken()) {
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
                this.writeFieldName(p.currentName());
                break;
            }
            case VALUE_STRING: {
                if (p.hasTextCharacters()) {
                    this.writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
                    break;
                }
                this.writeString(p.getText());
                break;
            }
            case VALUE_NUMBER_INT: {
                switch (p.getNumberType()) {
                    case INT: {
                        this.writeNumber(p.getIntValue());
                        break block0;
                    }
                    case BIG_INTEGER: {
                        this.writeLazyInteger(p.getNumberValueDeferred());
                        break block0;
                    }
                }
                this.writeNumber(p.getLongValue());
                break;
            }
            case VALUE_NUMBER_FLOAT: {
                this.writeLazyDecimal(p.getNumberValueDeferred());
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
                this.writeObject(p.getEmbeddedObject());
                break;
            }
            default: {
                throw new RuntimeException("Internal error: unexpected token: " + p.currentToken());
            }
        }
    }

    public void copyCurrentStructure(JsonParser p) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.FIELD_NAME) {
            if (this._mayHaveNativeIds) {
                this._checkNativeIds(p);
            }
            this.writeFieldName(p.currentName());
            t = p.nextToken();
        } else if (t == null) {
            throw new IllegalStateException("No token available from argument `JsonParser`");
        }
        switch (t) {
            case START_ARRAY: {
                if (this._mayHaveNativeIds) {
                    this._checkNativeIds(p);
                }
                this.writeStartArray();
                this._copyBufferContents(p);
                break;
            }
            case START_OBJECT: {
                if (this._mayHaveNativeIds) {
                    this._checkNativeIds(p);
                }
                this.writeStartObject();
                this._copyBufferContents(p);
                break;
            }
            case END_ARRAY: {
                this.writeEndArray();
                break;
            }
            case END_OBJECT: {
                this.writeEndObject();
                break;
            }
            default: {
                this._copyBufferValue(p, t);
            }
        }
    }

    protected void _copyBufferContents(JsonParser p) throws IOException {
        JsonToken t;
        int depth = 1;
        block7: while ((t = p.nextToken()) != null) {
            switch (t) {
                case FIELD_NAME: {
                    if (this._mayHaveNativeIds) {
                        this._checkNativeIds(p);
                    }
                    this.writeFieldName(p.currentName());
                    continue block7;
                }
                case START_ARRAY: {
                    if (this._mayHaveNativeIds) {
                        this._checkNativeIds(p);
                    }
                    this.writeStartArray();
                    ++depth;
                    continue block7;
                }
                case START_OBJECT: {
                    if (this._mayHaveNativeIds) {
                        this._checkNativeIds(p);
                    }
                    this.writeStartObject();
                    ++depth;
                    continue block7;
                }
                case END_ARRAY: {
                    this.writeEndArray();
                    if (--depth != 0) continue block7;
                    return;
                }
                case END_OBJECT: {
                    this.writeEndObject();
                    if (--depth != 0) continue block7;
                    return;
                }
            }
            this._copyBufferValue(p, t);
        }
    }

    private void _copyBufferValue(JsonParser p, JsonToken t) throws IOException {
        if (this._mayHaveNativeIds) {
            this._checkNativeIds(p);
        }
        block0 : switch (t) {
            case VALUE_STRING: {
                if (p.hasTextCharacters()) {
                    this.writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
                    break;
                }
                this.writeString(p.getText());
                break;
            }
            case VALUE_NUMBER_INT: {
                switch (p.getNumberType()) {
                    case INT: {
                        this.writeNumber(p.getIntValue());
                        break block0;
                    }
                    case BIG_INTEGER: {
                        this.writeLazyInteger(p.getNumberValueDeferred());
                        break block0;
                    }
                }
                this.writeNumber(p.getLongValue());
                break;
            }
            case VALUE_NUMBER_FLOAT: {
                this.writeLazyDecimal(p.getNumberValueDeferred());
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
                this.writeObject(p.getEmbeddedObject());
                break;
            }
            default: {
                throw new RuntimeException("Internal error: unexpected token: " + t);
            }
        }
    }

    private final void _checkNativeIds(JsonParser p) throws IOException {
        this._typeId = p.getTypeId();
        if (this._typeId != null) {
            this._hasNativeId = true;
        }
        if ((this._objectId = p.getObjectId()) != null) {
            this._hasNativeId = true;
        }
    }

    protected final void _appendValue(JsonToken type) {
        this._writeContext.writeValue();
        Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, this._objectId, this._typeId) : this._last.append(this._appendAt, type);
        if (next == null) {
            ++this._appendAt;
        } else {
            this._last = next;
            this._appendAt = 1;
        }
    }

    protected final void _appendValue(JsonToken type, Object value) {
        this._writeContext.writeValue();
        Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, value, this._objectId, this._typeId) : this._last.append(this._appendAt, type, value);
        if (next == null) {
            ++this._appendAt;
        } else {
            this._last = next;
            this._appendAt = 1;
        }
    }

    protected final void _appendFieldName(Object value) {
        Segment next = this._hasNativeId ? this._last.append(this._appendAt, JsonToken.FIELD_NAME, value, this._objectId, this._typeId) : this._last.append(this._appendAt, JsonToken.FIELD_NAME, value);
        if (next == null) {
            ++this._appendAt;
        } else {
            this._last = next;
            this._appendAt = 1;
        }
    }

    protected final void _appendStartMarker(JsonToken type) {
        Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, this._objectId, this._typeId) : this._last.append(this._appendAt, type);
        if (next == null) {
            ++this._appendAt;
        } else {
            this._last = next;
            this._appendAt = 1;
        }
    }

    protected final void _appendEndMarker(JsonToken type) {
        Segment next = this._last.append(this._appendAt, type);
        if (next == null) {
            ++this._appendAt;
        } else {
            this._last = next;
            this._appendAt = 1;
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
        protected TreeMap<Integer, Object> _nativeIds;

        public JsonToken type(int index) {
            long l = this._tokenTypes;
            if (index > 0) {
                l >>= index << 2;
            }
            int ix = (int)l & 0xF;
            return TOKEN_TYPES_BY_INDEX[ix];
        }

        public int rawType(int index) {
            long l = this._tokenTypes;
            if (index > 0) {
                l >>= index << 2;
            }
            return (int)l & 0xF;
        }

        public Object get(int index) {
            return this._tokens[index];
        }

        public Segment next() {
            return this._next;
        }

        public boolean hasIds() {
            return this._nativeIds != null;
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

        public Segment append(int index, JsonToken tokenType, Object objectId, Object typeId) {
            if (index < 16) {
                this.set(index, tokenType, objectId, typeId);
                return null;
            }
            this._next = new Segment();
            this._next.set(0, tokenType, objectId, typeId);
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

        public Segment append(int index, JsonToken tokenType, Object value, Object objectId, Object typeId) {
            if (index < 16) {
                this.set(index, tokenType, value, objectId, typeId);
                return null;
            }
            this._next = new Segment();
            this._next.set(0, tokenType, value, objectId, typeId);
            return this._next;
        }

        private void set(int index, JsonToken tokenType) {
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }

        private void set(int index, JsonToken tokenType, Object objectId, Object typeId) {
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
            this.assignNativeIds(index, objectId, typeId);
        }

        private void set(int index, JsonToken tokenType, Object value) {
            this._tokens[index] = value;
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }

        private void set(int index, JsonToken tokenType, Object value, Object objectId, Object typeId) {
            this._tokens[index] = value;
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
            this.assignNativeIds(index, objectId, typeId);
        }

        private final void assignNativeIds(int index, Object objectId, Object typeId) {
            if (this._nativeIds == null) {
                this._nativeIds = new TreeMap();
            }
            if (objectId != null) {
                this._nativeIds.put(this._objectIdIndex(index), objectId);
            }
            if (typeId != null) {
                this._nativeIds.put(this._typeIdIndex(index), typeId);
            }
        }

        Object findObjectId(int index) {
            return this._nativeIds == null ? null : this._nativeIds.get(this._objectIdIndex(index));
        }

        Object findTypeId(int index) {
            return this._nativeIds == null ? null : this._nativeIds.get(this._typeIdIndex(index));
        }

        private final int _typeIdIndex(int i) {
            return i + i;
        }

        private final int _objectIdIndex(int i) {
            return i + i + 1;
        }

        static {
            JsonToken[] t = JsonToken.values();
            System.arraycopy(t, 1, TOKEN_TYPES_BY_INDEX, 1, Math.min(15, t.length - 1));
        }
    }

    protected static final class Parser
    extends ParserMinimalBase {
        protected ObjectCodec _codec;
        protected StreamReadConstraints _streamReadConstraints;
        protected final boolean _hasNativeTypeIds;
        protected final boolean _hasNativeObjectIds;
        protected final boolean _hasNativeIds;
        protected Segment _segment;
        protected int _segmentPtr;
        protected TokenBufferReadContext _parsingContext;
        protected boolean _closed;
        protected transient ByteArrayBuilder _byteBuilder;
        protected JsonLocation _location = null;

        @Deprecated
        public Parser(Segment firstSeg, ObjectCodec codec, boolean hasNativeTypeIds, boolean hasNativeObjectIds) {
            this(firstSeg, codec, hasNativeTypeIds, hasNativeObjectIds, null);
        }

        @Deprecated
        public Parser(Segment firstSeg, ObjectCodec codec, boolean hasNativeTypeIds, boolean hasNativeObjectIds, JsonStreamContext parentContext) {
            this(firstSeg, codec, hasNativeTypeIds, hasNativeObjectIds, parentContext, StreamReadConstraints.defaults());
        }

        public Parser(Segment firstSeg, ObjectCodec codec, boolean hasNativeTypeIds, boolean hasNativeObjectIds, JsonStreamContext parentContext, StreamReadConstraints streamReadConstraints) {
            this._segment = firstSeg;
            this._segmentPtr = -1;
            this._codec = codec;
            this._streamReadConstraints = streamReadConstraints;
            this._parsingContext = TokenBufferReadContext.createRootContext(parentContext);
            this._hasNativeTypeIds = hasNativeTypeIds;
            this._hasNativeObjectIds = hasNativeObjectIds;
            this._hasNativeIds = hasNativeTypeIds || hasNativeObjectIds;
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

        public Version version() {
            return PackageVersion.VERSION;
        }

        public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
            return DEFAULT_READ_CAPABILITIES;
        }

        public StreamReadConstraints streamReadConstraints() {
            return this._streamReadConstraints;
        }

        public JsonToken peekNextToken() throws IOException {
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

        public JsonToken nextToken() throws IOException {
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
                this._parsingContext = this._parsingContext.createChildObjectContext();
            } else if (this._currToken == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext();
            } else if (this._currToken == JsonToken.END_OBJECT || this._currToken == JsonToken.END_ARRAY) {
                this._parsingContext = this._parsingContext.parentOrCopy();
            } else {
                this._parsingContext.updateForValue();
            }
            return this._currToken;
        }

        public String nextFieldName() throws IOException {
            if (this._closed || this._segment == null) {
                return null;
            }
            int ptr = this._segmentPtr + 1;
            if (ptr < 16 && this._segment.type(ptr) == JsonToken.FIELD_NAME) {
                this._segmentPtr = ptr;
                this._currToken = JsonToken.FIELD_NAME;
                Object ob = this._segment.get(ptr);
                String name = ob instanceof String ? (String)ob : ob.toString();
                this._parsingContext.setCurrentName(name);
                return name;
            }
            return this.nextToken() == JsonToken.FIELD_NAME ? this.currentName() : null;
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

        public String currentName() {
            if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
                JsonStreamContext parent = this._parsingContext.getParent();
                return parent.getCurrentName();
            }
            return this._parsingContext.getCurrentName();
        }

        public String getCurrentName() {
            return this.currentName();
        }

        public void overrideCurrentName(String name) {
            TokenBufferReadContext ctxt = this._parsingContext;
            if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
                ctxt = ctxt.getParent();
            }
            if (ctxt instanceof TokenBufferReadContext) {
                try {
                    ctxt.setCurrentName(name);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public String getText() {
            if (this._currToken == JsonToken.VALUE_STRING || this._currToken == JsonToken.FIELD_NAME) {
                Object ob = this._currentObject();
                if (ob instanceof String) {
                    return (String)ob;
                }
                return ClassUtil.nullOrToString(ob);
            }
            if (this._currToken == null) {
                return null;
            }
            switch (this._currToken) {
                case VALUE_NUMBER_INT: 
                case VALUE_NUMBER_FLOAT: {
                    return ClassUtil.nullOrToString(this._currentObject());
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

        public boolean isNaN() {
            if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
                Object value = this._currentObject();
                if (value instanceof Double) {
                    Double v = (Double)value;
                    return v.isNaN() || v.isInfinite();
                }
                if (value instanceof Float) {
                    Float v = (Float)value;
                    return v.isNaN() || v.isInfinite();
                }
            }
            return false;
        }

        public BigInteger getBigIntegerValue() throws IOException {
            Number n = this.getNumberValue(true);
            if (n instanceof BigInteger) {
                return (BigInteger)n;
            }
            if (n instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal)n;
                this.streamReadConstraints().validateBigIntegerScale(bd.scale());
                return bd.toBigInteger();
            }
            return BigInteger.valueOf(n.longValue());
        }

        public BigDecimal getDecimalValue() throws IOException {
            Number n = this.getNumberValue(true);
            if (n instanceof BigDecimal) {
                return (BigDecimal)n;
            }
            if (n instanceof Integer) {
                return BigDecimal.valueOf(n.intValue());
            }
            if (n instanceof Long) {
                return BigDecimal.valueOf(n.longValue());
            }
            if (n instanceof BigInteger) {
                return new BigDecimal((BigInteger)n);
            }
            return BigDecimal.valueOf(n.doubleValue());
        }

        public double getDoubleValue() throws IOException {
            return this.getNumberValue().doubleValue();
        }

        public float getFloatValue() throws IOException {
            return this.getNumberValue().floatValue();
        }

        public int getIntValue() throws IOException {
            Number n;
            Number number = n = this._currToken == JsonToken.VALUE_NUMBER_INT ? (Number)((Number)this._currentObject()) : (Number)this.getNumberValue();
            if (n instanceof Integer || this._smallerThanInt(n)) {
                return n.intValue();
            }
            return this._convertNumberToInt(n);
        }

        public long getLongValue() throws IOException {
            Number n;
            Number number = n = this._currToken == JsonToken.VALUE_NUMBER_INT ? (Number)((Number)this._currentObject()) : (Number)this.getNumberValue();
            if (n instanceof Long || this._smallerThanLong(n)) {
                return n.longValue();
            }
            return this._convertNumberToLong(n);
        }

        public JsonParser.NumberType getNumberType() throws IOException {
            Object n = this.getNumberValueDeferred();
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
            if (n instanceof BigInteger) {
                return JsonParser.NumberType.BIG_INTEGER;
            }
            if (n instanceof Float) {
                return JsonParser.NumberType.FLOAT;
            }
            if (n instanceof Short) {
                return JsonParser.NumberType.INT;
            }
            if (n instanceof String) {
                return this._currToken == JsonToken.VALUE_NUMBER_FLOAT ? JsonParser.NumberType.BIG_DECIMAL : JsonParser.NumberType.BIG_INTEGER;
            }
            return null;
        }

        public final Number getNumberValue() throws IOException {
            return this.getNumberValue(false);
        }

        public Object getNumberValueDeferred() throws IOException {
            this._checkIsNumber();
            return this._currentObject();
        }

        private Number getNumberValue(boolean preferBigNumbers) throws IOException {
            this._checkIsNumber();
            Object value = this._currentObject();
            if (value instanceof Number) {
                return (Number)value;
            }
            if (value instanceof String) {
                String str = (String)value;
                int len = str.length();
                if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
                    if (preferBigNumbers || len >= 19) {
                        return NumberInput.parseBigInteger((String)str, (boolean)this.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
                    }
                    if (len >= 10) {
                        return NumberInput.parseLong((String)str);
                    }
                    return NumberInput.parseInt((String)str);
                }
                if (preferBigNumbers) {
                    BigDecimal dec = NumberInput.parseBigDecimal((String)str, (boolean)this.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
                    if (dec == null) {
                        throw new IllegalStateException("Internal error: failed to parse number '" + str + "'");
                    }
                    return dec;
                }
                return NumberInput.parseDouble((String)str, (boolean)this.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
            }
            throw new IllegalStateException("Internal error: entry should be a Number, but is of type " + ClassUtil.classNameOf(value));
        }

        private final boolean _smallerThanInt(Number n) {
            return n instanceof Short || n instanceof Byte;
        }

        private final boolean _smallerThanLong(Number n) {
            return n instanceof Integer || n instanceof Short || n instanceof Byte;
        }

        protected int _convertNumberToInt(Number n) throws IOException {
            if (n instanceof Long) {
                long l = n.longValue();
                int result = (int)l;
                if ((long)result != l) {
                    this.reportOverflowInt();
                }
                return result;
            }
            if (n instanceof BigInteger) {
                BigInteger big = (BigInteger)n;
                if (BI_MIN_INT.compareTo(big) > 0 || BI_MAX_INT.compareTo(big) < 0) {
                    this.reportOverflowInt();
                }
            } else {
                if (n instanceof Double || n instanceof Float) {
                    double d = n.doubleValue();
                    if (d < -2.147483648E9 || d > 2.147483647E9) {
                        this.reportOverflowInt();
                    }
                    return (int)d;
                }
                if (n instanceof BigDecimal) {
                    BigDecimal big = (BigDecimal)n;
                    if (BD_MIN_INT.compareTo(big) > 0 || BD_MAX_INT.compareTo(big) < 0) {
                        this.reportOverflowInt();
                    }
                } else {
                    this._throwInternal();
                }
            }
            return n.intValue();
        }

        protected long _convertNumberToLong(Number n) throws IOException {
            if (n instanceof BigInteger) {
                BigInteger big = (BigInteger)n;
                if (BI_MIN_LONG.compareTo(big) > 0 || BI_MAX_LONG.compareTo(big) < 0) {
                    this.reportOverflowLong();
                }
            } else {
                if (n instanceof Double || n instanceof Float) {
                    double d = n.doubleValue();
                    if (d < -9.223372036854776E18 || d > 9.223372036854776E18) {
                        this.reportOverflowLong();
                    }
                    return (long)d;
                }
                if (n instanceof BigDecimal) {
                    BigDecimal big = (BigDecimal)n;
                    if (BD_MIN_LONG.compareTo(big) > 0 || BD_MAX_LONG.compareTo(big) < 0) {
                        this.reportOverflowLong();
                    }
                } else {
                    this._throwInternal();
                }
            }
            return n.longValue();
        }

        public Object getEmbeddedObject() {
            if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                return this._currentObject();
            }
            return null;
        }

        public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
            Object ob;
            if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT && (ob = this._currentObject()) instanceof byte[]) {
                return (byte[])ob;
            }
            if (this._currToken != JsonToken.VALUE_STRING) {
                throw this._constructError("Current token (" + this._currToken + ") not VALUE_STRING (or VALUE_EMBEDDED_OBJECT with byte[]), cannot access as binary");
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

        public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
            byte[] data = this.getBinaryValue(b64variant);
            if (data != null) {
                out.write(data, 0, data.length);
                return data.length;
            }
            return 0;
        }

        public boolean canReadObjectId() {
            return this._hasNativeObjectIds;
        }

        public boolean canReadTypeId() {
            return this._hasNativeTypeIds;
        }

        public Object getTypeId() {
            return this._segment.findTypeId(this._segmentPtr);
        }

        public Object getObjectId() {
            return this._segment.findObjectId(this._segmentPtr);
        }

        protected final Object _currentObject() {
            return this._segment.get(this._segmentPtr);
        }

        protected final void _checkIsNumber() throws JacksonException {
            if (this._currToken == null || !this._currToken.isNumeric()) {
                throw this._constructError("Current token (" + this._currToken + ") not numeric, cannot use numeric value accessors");
            }
        }

        protected void _handleEOF() {
            this._throwInternal();
        }
    }
}

