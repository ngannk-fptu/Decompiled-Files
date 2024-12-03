/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.dataformat.cbor;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.StreamWriteCapability;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;
import com.fasterxml.jackson.dataformat.cbor.CBORWriteContext;
import com.fasterxml.jackson.dataformat.cbor.PackageVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

public class CBORGenerator
extends GeneratorBase {
    private static final int[] NO_INTS = new int[0];
    protected static final int BYTE_BUFFER_FOR_OUTPUT = 16000;
    protected static final int REPLACEMENT_CHAR = 65533;
    private static final int MAX_LONG_STRING_CHARS = 3996;
    private static final int MAX_LONG_STRING_BYTES = 11991;
    private static final int MIN_BUFFER_LENGTH = 770;
    private static final int INDEFINITE_LENGTH = -2;
    protected final IOContext _ioContext;
    protected final OutputStream _out;
    protected int _formatFeatures;
    protected boolean _cfgMinimalInts;
    protected CBORWriteContext _streamWriteContext;
    protected byte[] _outputBuffer;
    protected int _outputTail = 0;
    protected final int _outputEnd;
    protected char[] _charBuffer;
    protected final int _charBufferLength;
    protected int _bytesWritten;
    protected int[] _elementCounts = NO_INTS;
    protected int _elementCountsPtr;
    protected int _currentRemainingElements = -2;
    protected boolean _bufferRecyclable;
    private static final int MAX_SHORT_STRING_CHARS = 23;
    private static final int MAX_SHORT_STRING_BYTES = 71;
    private static final int MAX_MEDIUM_STRING_CHARS = 255;
    private static final int MAX_MEDIUM_STRING_BYTES = 768;

    public CBORGenerator(IOContext ctxt, int stdFeatures, int formatFeatures, ObjectCodec codec, OutputStream out) {
        super(stdFeatures, codec, null);
        DupDetector dups = JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(stdFeatures) ? DupDetector.rootDetector(this) : null;
        this._streamWriteContext = CBORWriteContext.createRootContext(dups);
        this._formatFeatures = formatFeatures;
        this._cfgMinimalInts = Feature.WRITE_MINIMAL_INTS.enabledIn(formatFeatures);
        this._ioContext = ctxt;
        this._out = out;
        this._bufferRecyclable = true;
        this._outputBuffer = ctxt.allocWriteEncodingBuffer(16000);
        this._outputEnd = this._outputBuffer.length;
        this._charBuffer = ctxt.allocConcatBuffer();
        this._charBufferLength = this._charBuffer.length;
        if (this._outputEnd < 770) {
            throw new IllegalStateException("Internal encoding buffer length (" + this._outputEnd + ") too short, must be at least " + 770);
        }
    }

    public CBORGenerator(IOContext ctxt, int stdFeatures, int formatFeatures, ObjectCodec codec, OutputStream out, byte[] outputBuffer, int offset, boolean bufferRecyclable) {
        super(stdFeatures, codec, null);
        DupDetector dups = JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(stdFeatures) ? DupDetector.rootDetector(this) : null;
        this._streamWriteContext = CBORWriteContext.createRootContext(dups);
        this._formatFeatures = formatFeatures;
        this._cfgMinimalInts = Feature.WRITE_MINIMAL_INTS.enabledIn(formatFeatures);
        this._ioContext = ctxt;
        this._out = out;
        this._bufferRecyclable = bufferRecyclable;
        this._outputTail = offset;
        this._outputBuffer = outputBuffer;
        this._outputEnd = this._outputBuffer.length;
        this._charBuffer = ctxt.allocConcatBuffer();
        this._charBufferLength = this._charBuffer.length;
        if (this._outputEnd < 770) {
            throw new IllegalStateException("Internal encoding buffer length (" + this._outputEnd + ") too short, must be at least " + 770);
        }
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public boolean canWriteBinaryNatively() {
        return true;
    }

    @Override
    public JacksonFeatureSet<StreamWriteCapability> getWriteCapabilities() {
        return DEFAULT_BINARY_WRITE_CAPABILITIES;
    }

    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        return this;
    }

    @Override
    public JsonGenerator setPrettyPrinter(PrettyPrinter pp) {
        return this;
    }

    @Override
    public Object getOutputTarget() {
        return this._out;
    }

    @Override
    public int getOutputBuffered() {
        return this._outputTail;
    }

    @Override
    public int getFormatFeatures() {
        return this._formatFeatures;
    }

    @Override
    public JsonGenerator overrideStdFeatures(int values, int mask) {
        int oldState = this._features;
        int newState = oldState & ~mask | values & mask;
        if (oldState != newState) {
            this._features = newState;
        }
        return this;
    }

    @Override
    public JsonGenerator overrideFormatFeatures(int values, int mask) {
        int oldState = this._formatFeatures;
        int newState = this._formatFeatures & ~mask | values & mask;
        if (oldState != newState) {
            this._formatFeatures = newState;
            this._cfgMinimalInts = Feature.WRITE_MINIMAL_INTS.enabledIn(newState);
        }
        return this;
    }

    @Override
    public Object currentValue() {
        return this._streamWriteContext.getCurrentValue();
    }

    @Override
    public Object getCurrentValue() {
        return this._streamWriteContext.getCurrentValue();
    }

    @Override
    public void assignCurrentValue(Object v) {
        this._streamWriteContext.setCurrentValue(v);
    }

    @Override
    public void setCurrentValue(Object v) {
        this._streamWriteContext.setCurrentValue(v);
    }

    @Override
    public JsonStreamContext getOutputContext() {
        return this._streamWriteContext;
    }

    public CBORGenerator enable(Feature f) {
        this._formatFeatures |= f.getMask();
        if (f == Feature.WRITE_MINIMAL_INTS) {
            this._cfgMinimalInts = true;
        }
        return this;
    }

    public CBORGenerator disable(Feature f) {
        this._formatFeatures &= ~f.getMask();
        if (f == Feature.WRITE_MINIMAL_INTS) {
            this._cfgMinimalInts = false;
        }
        return this;
    }

    public final boolean isEnabled(Feature f) {
        return (this._formatFeatures & f.getMask()) != 0;
    }

    public CBORGenerator configure(Feature f, boolean state) {
        if (state) {
            this.enable(f);
        } else {
            this.disable(f);
        }
        return this;
    }

    @Override
    public final void writeFieldName(String name) throws IOException {
        if (!this._streamWriteContext.writeFieldName(name)) {
            this._reportError("Can not write a field name, expecting a value");
        }
        this._writeString(name);
    }

    @Override
    public final void writeFieldName(SerializableString name) throws IOException {
        byte[] raw;
        int len;
        if (!this._streamWriteContext.writeFieldName(name.getValue())) {
            this._reportError("Can not write a field name, expecting a value");
        }
        if ((len = (raw = name.asUnquotedUTF8()).length) == 0) {
            this._writeByte((byte)96);
            return;
        }
        this._writeLengthMarker(96, len);
        this._writeBytes(raw, 0, len);
    }

    @Override
    public final void writeFieldId(long id) throws IOException {
        if (!this._streamWriteContext.writeFieldId(id)) {
            this._reportError("Can not write a field id, expecting a value");
        }
        this._writeLongNoCheck(id);
    }

    @Override
    public void copyCurrentEvent(JsonParser p) throws IOException {
        this.maybeCopyTag(p);
        super.copyCurrentEvent(p);
    }

    @Override
    public void copyCurrentStructure(JsonParser p) throws IOException {
        this.maybeCopyTag(p);
        super.copyCurrentStructure(p);
    }

    protected void maybeCopyTag(JsonParser p) throws IOException {
        int currentTag;
        if (p instanceof CBORParser && p.hasCurrentToken() && (currentTag = ((CBORParser)p).getCurrentTag()) != -1) {
            this.writeTag(currentTag);
        }
    }

    @Override
    public final void writeStartArray() throws IOException {
        this._verifyValueWrite("start an array");
        this._streamWriteContext = this._streamWriteContext.createChildArrayContext(null);
        if (this._elementCountsPtr > 0) {
            this._pushRemainingElements();
        }
        this._currentRemainingElements = -2;
        this._writeByte((byte)-97);
    }

    @Override
    public void writeStartArray(Object forValue) throws IOException {
        this._verifyValueWrite("start an array");
        this._streamWriteContext = this._streamWriteContext.createChildArrayContext(forValue);
        if (this._elementCountsPtr > 0) {
            this._pushRemainingElements();
        }
        this._currentRemainingElements = -2;
        this._writeByte((byte)-97);
    }

    @Override
    public void writeStartArray(Object forValue, int elementsToWrite) throws IOException {
        this._verifyValueWrite("start an array");
        this._streamWriteContext = this._streamWriteContext.createChildArrayContext(forValue);
        this._pushRemainingElements();
        this._currentRemainingElements = elementsToWrite;
        this._writeLengthMarker(128, elementsToWrite);
    }

    @Override
    @Deprecated
    public void writeStartArray(int elementsToWrite) throws IOException {
        this._verifyValueWrite("start an array");
        this._streamWriteContext = this._streamWriteContext.createChildArrayContext(null);
        this._pushRemainingElements();
        this._currentRemainingElements = elementsToWrite;
        this._writeLengthMarker(128, elementsToWrite);
    }

    @Override
    public final void writeEndArray() throws IOException {
        if (!this._streamWriteContext.inArray()) {
            this._reportError("Current context not Array but " + this._streamWriteContext.typeDesc());
        }
        this.closeComplexElement();
        this._streamWriteContext = this._streamWriteContext.getParent();
    }

    @Override
    public final void writeStartObject() throws IOException {
        this._verifyValueWrite("start an object");
        this._streamWriteContext = this._streamWriteContext.createChildObjectContext(null);
        if (this._elementCountsPtr > 0) {
            this._pushRemainingElements();
        }
        this._currentRemainingElements = -2;
        this._writeByte((byte)-65);
    }

    @Override
    public final void writeStartObject(Object forValue) throws IOException {
        CBORWriteContext ctxt;
        this._verifyValueWrite("start an object");
        this._streamWriteContext = ctxt = this._streamWriteContext.createChildObjectContext(forValue);
        if (this._elementCountsPtr > 0) {
            this._pushRemainingElements();
        }
        this._currentRemainingElements = -2;
        this._writeByte((byte)-65);
    }

    public final void writeStartObject(int elementsToWrite) throws IOException {
        this._verifyValueWrite("start an object");
        this._streamWriteContext = this._streamWriteContext.createChildObjectContext(null);
        this._pushRemainingElements();
        this._currentRemainingElements = elementsToWrite;
        this._writeLengthMarker(160, elementsToWrite);
    }

    @Override
    public final void writeEndObject() throws IOException {
        if (!this._streamWriteContext.inObject()) {
            this._reportError("Current context not Object but " + this._streamWriteContext.typeDesc());
        }
        this.closeComplexElement();
        this._streamWriteContext = this._streamWriteContext.getParent();
    }

    @Override
    public void writeArray(int[] array, int offset, int length) throws IOException {
        this._verifyOffsets(array.length, offset, length);
        this._verifyValueWrite("write int array");
        this._writeLengthMarker(128, length);
        if (this._cfgMinimalInts) {
            int end = offset + length;
            for (int i = offset; i < end; ++i) {
                int value = array[i];
                if (value < 0) {
                    this._writeIntMinimal(32, -value - 1);
                    continue;
                }
                this._writeIntMinimal(0, value);
            }
        } else {
            int end = offset + length;
            for (int i = offset; i < end; ++i) {
                int value = array[i];
                if (value < 0) {
                    this._writeIntFull(32, -value - 1);
                    continue;
                }
                this._writeIntFull(0, value);
            }
        }
    }

    @Override
    public void writeArray(long[] array, int offset, int length) throws IOException {
        this._verifyOffsets(array.length, offset, length);
        this._verifyValueWrite("write int array");
        this._writeLengthMarker(128, length);
        int end = offset + length;
        for (int i = offset; i < end; ++i) {
            this._writeLongNoCheck(array[i]);
        }
    }

    @Override
    public void writeArray(double[] array, int offset, int length) throws IOException {
        this._verifyOffsets(array.length, offset, length);
        this._verifyValueWrite("write int array");
        this._writeLengthMarker(128, length);
        int end = offset + length;
        for (int i = offset; i < end; ++i) {
            this._writeDoubleNoCheck(array[i]);
        }
    }

    private final void _pushRemainingElements() {
        if (this._elementCounts.length == this._elementCountsPtr) {
            this._elementCounts = Arrays.copyOf(this._elementCounts, this._elementCounts.length + 10);
        }
        this._elementCounts[this._elementCountsPtr++] = this._currentRemainingElements;
    }

    private final void _writeIntMinimal(int markerBase, int i) throws IOException {
        byte b0;
        this._ensureRoomForOutput(5);
        if (i >= 0) {
            if (i < 24) {
                this._outputBuffer[this._outputTail++] = (byte)(markerBase + i);
                return;
            }
            if (i <= 255) {
                this._outputBuffer[this._outputTail++] = (byte)(markerBase + 24);
                this._outputBuffer[this._outputTail++] = (byte)i;
                return;
            }
            b0 = (byte)i;
            if ((i >>= 8) <= 255) {
                this._outputBuffer[this._outputTail++] = (byte)(markerBase + 25);
                this._outputBuffer[this._outputTail++] = (byte)i;
                this._outputBuffer[this._outputTail++] = b0;
                return;
            }
        } else {
            b0 = (byte)i;
            i >>= 8;
        }
        this._outputBuffer[this._outputTail++] = (byte)(markerBase + 26);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
        this._outputBuffer[this._outputTail++] = b0;
    }

    private final void _writeIntFull(int markerBase, int i) throws IOException {
        this._ensureRoomForOutput(5);
        this._outputBuffer[this._outputTail++] = (byte)(markerBase + 26);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
    }

    private final void _writeLongNoCheck(long l) throws IOException {
        if (this._cfgMinimalInts) {
            if (l >= 0L) {
                if (l < 0x100000000L) {
                    this._writeIntMinimal(0, (int)l);
                    return;
                }
            } else if (l >= -4294967296L) {
                this._writeIntMinimal(32, (int)(-l - 1L));
                return;
            }
        }
        this._ensureRoomForOutput(9);
        if (l < 0L) {
            ++l;
            l = -l;
            this._outputBuffer[this._outputTail++] = 59;
        } else {
            this._outputBuffer[this._outputTail++] = 27;
        }
        int i = (int)(l >> 32);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
        i = (int)l;
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
    }

    private final void _writeDoubleNoCheck(double d) throws IOException {
        this._ensureRoomForOutput(11);
        long l = Double.doubleToRawLongBits(d);
        this._outputBuffer[this._outputTail++] = -5;
        int i = (int)(l >> 32);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
        i = (int)l;
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
    }

    @Override
    public void writeString(String text) throws IOException {
        if (text == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write String value");
        this._writeString(text);
    }

    @Override
    public final void writeString(SerializableString sstr) throws IOException {
        this._verifyValueWrite("write String value");
        byte[] raw = sstr.asUnquotedUTF8();
        int len = raw.length;
        if (len == 0) {
            this._writeByte((byte)96);
            return;
        }
        this._writeLengthMarker(96, len);
        this._writeBytes(raw, 0, len);
    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException {
        this._verifyValueWrite("write String value");
        if (len == 0) {
            this._writeByte((byte)96);
            return;
        }
        this._writeString(text, offset, len);
    }

    @Override
    public void writeRawUTF8String(byte[] raw, int offset, int len) throws IOException {
        this._verifyValueWrite("write String value");
        if (len == 0) {
            this._writeByte((byte)96);
            return;
        }
        this._writeLengthMarker(96, len);
        this._writeBytes(raw, 0, len);
    }

    @Override
    public final void writeUTF8String(byte[] text, int offset, int len) throws IOException {
        this.writeRawUTF8String(text, offset, len);
    }

    @Override
    public void writeRaw(String text) throws IOException {
        throw this._notSupported();
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException {
        throw this._notSupported();
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException {
        throw this._notSupported();
    }

    @Override
    public void writeRaw(char c) throws IOException {
        throw this._notSupported();
    }

    @Override
    public void writeRawValue(String text) throws IOException {
        throw this._notSupported();
    }

    @Override
    public void writeRawValue(String text, int offset, int len) throws IOException {
        throw this._notSupported();
    }

    @Override
    public void writeRawValue(char[] text, int offset, int len) throws IOException {
        throw this._notSupported();
    }

    @Override
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException {
        if (data == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write Binary value");
        this._writeLengthMarker(64, len);
        this._writeBytes(data, offset, len);
    }

    @Override
    public int writeBinary(InputStream data, int dataLength) throws IOException {
        if (dataLength < 0) {
            throw new UnsupportedOperationException("Must pass actual length for CBOR encoded data");
        }
        this._verifyValueWrite("write Binary value");
        this._writeLengthMarker(64, dataLength);
        int missing = this._writeBytes(data, dataLength);
        if (missing > 0) {
            this._reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")");
        }
        return dataLength;
    }

    @Override
    public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
        return this.writeBinary(data, dataLength);
    }

    @Override
    public void writeBoolean(boolean state) throws IOException {
        this._verifyValueWrite("write boolean value");
        if (state) {
            this._writeByte((byte)-11);
        } else {
            this._writeByte((byte)-12);
        }
    }

    @Override
    public void writeNull() throws IOException {
        this._verifyValueWrite("write null value");
        this._writeByte((byte)-10);
    }

    @Override
    public void writeNumber(int i) throws IOException {
        byte b0;
        int marker;
        this._verifyValueWrite("write number");
        if (i < 0) {
            i = -i - 1;
            marker = 32;
        } else {
            marker = 0;
        }
        this._ensureRoomForOutput(5);
        if (this._cfgMinimalInts) {
            if (i < 24) {
                this._outputBuffer[this._outputTail++] = (byte)(marker + i);
                return;
            }
            if (i <= 255) {
                this._outputBuffer[this._outputTail++] = (byte)(marker + 24);
                this._outputBuffer[this._outputTail++] = (byte)i;
                return;
            }
            b0 = (byte)i;
            if ((i >>= 8) <= 255) {
                this._outputBuffer[this._outputTail++] = (byte)(marker + 25);
                this._outputBuffer[this._outputTail++] = (byte)i;
                this._outputBuffer[this._outputTail++] = b0;
                return;
            }
        } else {
            b0 = (byte)i;
            i >>= 8;
        }
        this._outputBuffer[this._outputTail++] = (byte)(marker + 26);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
        this._outputBuffer[this._outputTail++] = b0;
    }

    @Override
    public void writeNumber(long l) throws IOException {
        this._verifyValueWrite("write number");
        if (this._cfgMinimalInts) {
            if (l >= 0L) {
                if (l < 0x100000000L) {
                    this._writeIntMinimal(0, (int)l);
                    return;
                }
            } else if (l >= -4294967296L) {
                this._writeIntMinimal(32, (int)(-l - 1L));
                return;
            }
        }
        this._ensureRoomForOutput(9);
        if (l < 0L) {
            ++l;
            l = -l;
            this._outputBuffer[this._outputTail++] = 59;
        } else {
            this._outputBuffer[this._outputTail++] = 27;
        }
        int i = (int)(l >> 32);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
        i = (int)l;
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
    }

    @Override
    public void writeNumber(BigInteger v) throws IOException {
        if (v == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write number");
        this._write(v);
    }

    protected void _write(BigInteger v) throws IOException {
        if (v.signum() < 0) {
            this._writeByte((byte)-61);
            v = v.negate();
        } else {
            this._writeByte((byte)-62);
        }
        byte[] data = v.toByteArray();
        int len = data.length;
        this._writeLengthMarker(64, len);
        this._writeBytes(data, 0, len);
    }

    @Override
    public void writeNumber(double d) throws IOException {
        this._verifyValueWrite("write number");
        this._ensureRoomForOutput(11);
        long l = Double.doubleToRawLongBits(d);
        this._outputBuffer[this._outputTail++] = -5;
        int i = (int)(l >> 32);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
        i = (int)l;
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
    }

    @Override
    public void writeNumber(float f) throws IOException {
        this._ensureRoomForOutput(6);
        this._verifyValueWrite("write number");
        int i = Float.floatToRawIntBits(f);
        this._outputBuffer[this._outputTail++] = -6;
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
    }

    @Override
    public void writeNumber(BigDecimal dec) throws IOException {
        if (dec == null) {
            this.writeNull();
            return;
        }
        this._verifyValueWrite("write number");
        this._writeByte((byte)-60);
        this._writeByte((byte)-126);
        int scale = dec.scale();
        this._writeIntValue(-scale);
        BigInteger unscaled = dec.unscaledValue();
        int bitLength = unscaled.bitLength();
        if (bitLength <= 31) {
            this._writeIntValue(unscaled.intValue());
        } else if (bitLength <= 63) {
            this._writeLongValue(unscaled.longValue());
        } else {
            this._write(unscaled);
        }
    }

    @Override
    public void writeNumber(String encodedValue) throws IOException, JsonGenerationException, UnsupportedOperationException {
        this.writeString(encodedValue);
    }

    @Override
    protected final void _verifyValueWrite(String typeMsg) throws IOException {
        int count;
        if (!this._streamWriteContext.writeValue()) {
            this._reportError("Can not " + typeMsg + ", expecting field name/id");
        }
        if ((count = this._currentRemainingElements) != -2) {
            if (--count < 0) {
                this._failSizedArrayOrObject();
                return;
            }
            this._currentRemainingElements = count;
        }
    }

    private void _failSizedArrayOrObject() throws IOException {
        this._reportError(String.format("%s size mismatch: number of element encoded is not equal to reported array/map size.", this._streamWriteContext.typeDesc()));
    }

    @Override
    public final void flush() throws IOException {
        this._flushBuffer();
        if (this.isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
            this._out.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (this._outputBuffer != null && this.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
            while (true) {
                JsonStreamContext ctxt;
                if ((ctxt = this.getOutputContext()).inArray()) {
                    this.writeEndArray();
                    continue;
                }
                if (!ctxt.inObject()) break;
                this.writeEndObject();
            }
        }
        super.close();
        this._flushBuffer();
        if (this._ioContext.isResourceManaged() || this.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
            this._out.close();
        } else if (this.isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
            this._out.flush();
        }
        this._releaseBuffers();
    }

    public void writeTag(int tagId) throws IOException {
        if (tagId < 0) {
            throw new IllegalArgumentException("Can not write negative tag ids (" + tagId + ")");
        }
        this._writeLengthMarker(192, tagId);
    }

    public void writeRaw(byte b) throws IOException {
        this._writeByte(b);
    }

    public void writeBytes(byte[] data, int offset, int len) throws IOException {
        this._writeBytes(data, offset, len);
    }

    protected final void _writeString(String name) throws IOException {
        int len = name.length();
        if (len == 0) {
            this._writeByte((byte)96);
            return;
        }
        if (len <= 23) {
            this._ensureSpace(71);
            int actual = this._encode(this._outputTail + 1, name, len);
            byte[] buf = this._outputBuffer;
            int ix = this._outputTail;
            if (actual <= 23) {
                buf[ix++] = (byte)(96 + actual);
                this._outputTail = ix + actual;
                return;
            }
            System.arraycopy(buf, ix + 1, buf, ix + 2, actual);
            buf[ix++] = 120;
            buf[ix++] = (byte)actual;
            this._outputTail = ix + actual;
            return;
        }
        char[] cbuf = this._charBuffer;
        if (len > cbuf.length) {
            this._charBuffer = cbuf = new char[Math.max(this._charBuffer.length + 32, len)];
        }
        name.getChars(0, len, cbuf, 0);
        this._writeString(cbuf, 0, len);
    }

    protected final void _ensureSpace(int needed) throws IOException {
        if (this._outputTail + needed + 3 > this._outputEnd) {
            this._flushBuffer();
        }
    }

    protected final void _writeString(char[] text, int offset, int len) throws IOException {
        if (len <= 23) {
            this._ensureSpace(71);
            int actual = this._encode(this._outputTail + 1, text, offset, offset + len);
            byte[] buf = this._outputBuffer;
            int ix = this._outputTail;
            if (actual <= 23) {
                buf[ix++] = (byte)(96 + actual);
                this._outputTail = ix + actual;
                return;
            }
            System.arraycopy(buf, ix + 1, buf, ix + 2, actual);
            buf[ix++] = 120;
            buf[ix++] = (byte)actual;
            this._outputTail = ix + actual;
            return;
        }
        if (len <= 255) {
            this._ensureSpace(768);
            int actual = this._encode(this._outputTail + 2, text, offset, offset + len);
            byte[] buf = this._outputBuffer;
            int ix = this._outputTail;
            if (actual <= 255) {
                buf[ix++] = 120;
                buf[ix++] = (byte)actual;
                this._outputTail = ix + actual;
                return;
            }
            System.arraycopy(buf, ix + 2, buf, ix + 3, actual);
            buf[ix++] = 121;
            buf[ix++] = (byte)(actual >> 8);
            buf[ix++] = (byte)actual;
            this._outputTail = ix + actual;
            return;
        }
        if (len <= 3996) {
            this._ensureSpace(11991);
            int ix = this._outputTail;
            int actual = this._encode(ix + 3, text, offset, offset + len);
            byte[] buf = this._outputBuffer;
            buf[ix++] = 121;
            buf[ix++] = (byte)(actual >> 8);
            buf[ix++] = (byte)actual;
            this._outputTail = ix + actual;
            return;
        }
        this._writeChunkedString(text, offset, len);
    }

    protected final void _writeChunkedString(char[] text, int offset, int len) throws IOException {
        this._writeByte((byte)127);
        while (len > 3996) {
            this._ensureSpace(11991);
            int ix = this._outputTail;
            int amount = 3996;
            int end = offset + amount;
            char c = text[end - 1];
            if (c >= '\ud800' && c <= '\udbff') {
                --end;
                --amount;
            }
            int actual = this._encode(this._outputTail + 3, text, offset, end);
            byte[] buf = this._outputBuffer;
            buf[ix++] = 121;
            buf[ix++] = (byte)(actual >> 8);
            buf[ix++] = (byte)actual;
            this._outputTail = ix + actual;
            offset += amount;
            len -= amount;
        }
        if (len > 0) {
            this._writeString(text, offset, len);
        }
        this._writeByte((byte)-1);
    }

    private final int _encode(int outputPtr, char[] str, int i, int end) throws IOException {
        byte[] outBuf = this._outputBuffer;
        int outputStart = outputPtr;
        do {
            char c;
            if ((c = str[i]) > '\u007f') {
                return this._shortUTF8Encode2(str, i, end, outputPtr, outputStart);
            }
            outBuf[outputPtr++] = (byte)c;
        } while (++i < end);
        return outputPtr - outputStart;
    }

    private final int _shortUTF8Encode2(char[] str, int i, int end, int outputPtr, int outputStart) throws IOException {
        byte[] outBuf = this._outputBuffer;
        while (i < end) {
            char c;
            if ((c = str[i++]) <= '\u007f') {
                outBuf[outputPtr++] = (byte)c;
                continue;
            }
            if (c < '\u0800') {
                outBuf[outputPtr++] = (byte)(0xC0 | c >> 6);
                outBuf[outputPtr++] = (byte)(0x80 | c & 0x3F);
                continue;
            }
            if (c < '\ud800' || c > '\udfff') {
                outBuf[outputPtr++] = (byte)(0xE0 | c >> 12);
                outBuf[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
                outBuf[outputPtr++] = (byte)(0x80 | c & 0x3F);
                continue;
            }
            if (c <= '\udbff' && i < end) {
                char d = str[i];
                if (d <= '\udfff' && d >= '\udc00') {
                    ++i;
                    outputPtr = this._decodeAndWriteSurrogate(c, d, outBuf, outputPtr);
                    continue;
                }
                outputPtr = this._invalidSurrogateEnd(c, d, outBuf, outputPtr);
                continue;
            }
            outputPtr = this._invalidSurrogateStart(c, outBuf, outputPtr);
        }
        return outputPtr - outputStart;
    }

    private final int _encode(int outputPtr, String str, int len) throws IOException {
        byte[] outBuf = this._outputBuffer;
        int outputStart = outputPtr;
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (c > '\u007f') {
                return this._encode2(i, outputPtr, str, len, outputStart);
            }
            outBuf[outputPtr++] = (byte)c;
        }
        return outputPtr - outputStart;
    }

    private final int _encode2(int i, int outputPtr, String str, int len, int outputStart) throws IOException {
        byte[] outBuf = this._outputBuffer;
        while (i < len) {
            char c;
            if ((c = str.charAt(i++)) <= '\u007f') {
                outBuf[outputPtr++] = (byte)c;
                continue;
            }
            if (c < '\u0800') {
                outBuf[outputPtr++] = (byte)(0xC0 | c >> 6);
                outBuf[outputPtr++] = (byte)(0x80 | c & 0x3F);
                continue;
            }
            if (c < '\ud800' || c > '\udfff') {
                outBuf[outputPtr++] = (byte)(0xE0 | c >> 12);
                outBuf[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
                outBuf[outputPtr++] = (byte)(0x80 | c & 0x3F);
                continue;
            }
            if (c <= '\udbff' && i < len) {
                char d = str.charAt(i);
                if (d <= '\udfff' && d >= '\udc00') {
                    ++i;
                    outputPtr = this._decodeAndWriteSurrogate(c, d, outBuf, outputPtr);
                    continue;
                }
                outputPtr = this._invalidSurrogateEnd(c, d, outBuf, outputPtr);
                continue;
            }
            outputPtr = this._invalidSurrogateStart(c, outBuf, outputPtr);
        }
        return outputPtr - outputStart;
    }

    private int _invalidSurrogateStart(int code, byte[] outBuf, int outputPtr) throws IOException {
        if (this.isEnabled(Feature.LENIENT_UTF_ENCODING)) {
            return this._appendReplacementChar(outBuf, outputPtr);
        }
        if (code <= 56319) {
            this._reportError(String.format("Unmatched surrogate pair, starts with valid high surrogate (0x%04X) but ends without low surrogate", code));
        }
        this._reportError(String.format("Invalid surrogate pair, starts with invalid high surrogate (0x%04X), not in valid range [0xD800, 0xDBFF]", code));
        return 0;
    }

    private int _invalidSurrogateEnd(int surr1, int surr2, byte[] outBuf, int outputPtr) throws IOException {
        if (this.isEnabled(Feature.LENIENT_UTF_ENCODING)) {
            return this._appendReplacementChar(outBuf, outputPtr);
        }
        this._reportError(String.format("Invalid surrogate pair, starts with valid high surrogate (0x%04X) but ends with invalid low surrogate (0x%04X), not in valid range [0xDC00, 0xDFFF]", surr1, surr2));
        return 0;
    }

    private int _appendReplacementChar(byte[] outBuf, int outputPtr) {
        outBuf[outputPtr++] = -17;
        outBuf[outputPtr++] = -65;
        outBuf[outputPtr++] = -67;
        return outputPtr;
    }

    private int _decodeAndWriteSurrogate(int surr1, int surr2, byte[] outBuf, int outputPtr) {
        int c = 65536 + (surr1 - 55296 << 10) + (surr2 - 56320);
        outBuf[outputPtr++] = (byte)(0xF0 | c >> 18);
        outBuf[outputPtr++] = (byte)(0x80 | c >> 12 & 0x3F);
        outBuf[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
        outBuf[outputPtr++] = (byte)(0x80 | c & 0x3F);
        return outputPtr;
    }

    private final void _ensureRoomForOutput(int needed) throws IOException {
        if (this._outputTail + needed >= this._outputEnd) {
            this._flushBuffer();
        }
    }

    private final void _writeIntValue(int i) throws IOException {
        int marker;
        if (i < 0) {
            i = -i - 1;
            marker = 32;
        } else {
            marker = 0;
        }
        this._writeLengthMarker(marker, i);
    }

    private final void _writeLongValue(long l) throws IOException {
        this._ensureRoomForOutput(9);
        if (l < 0L) {
            ++l;
            l = -l;
            this._outputBuffer[this._outputTail++] = 59;
        } else {
            this._outputBuffer[this._outputTail++] = 27;
        }
        int i = (int)(l >> 32);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
        i = (int)l;
        this._outputBuffer[this._outputTail++] = (byte)(i >> 24);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
    }

    private final void _writeLengthMarker(int majorType, int i) throws IOException {
        this._ensureRoomForOutput(5);
        if (i < 24) {
            this._outputBuffer[this._outputTail++] = (byte)(majorType + i);
            return;
        }
        if (i <= 255) {
            this._outputBuffer[this._outputTail++] = (byte)(majorType + 24);
            this._outputBuffer[this._outputTail++] = (byte)i;
            return;
        }
        byte b0 = (byte)i;
        if ((i >>= 8) <= 255) {
            this._outputBuffer[this._outputTail++] = (byte)(majorType + 25);
            this._outputBuffer[this._outputTail++] = (byte)i;
            this._outputBuffer[this._outputTail++] = b0;
            return;
        }
        this._outputBuffer[this._outputTail++] = (byte)(majorType + 26);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 16);
        this._outputBuffer[this._outputTail++] = (byte)(i >> 8);
        this._outputBuffer[this._outputTail++] = (byte)i;
        this._outputBuffer[this._outputTail++] = b0;
    }

    private final void _writeByte(byte b) throws IOException {
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = b;
    }

    private final void _writeBytes(byte[] data, int offset, int len) throws IOException {
        if (len == 0) {
            return;
        }
        if (this._outputTail + len >= this._outputEnd) {
            this._writeBytesLong(data, offset, len);
            return;
        }
        System.arraycopy(data, offset, this._outputBuffer, this._outputTail, len);
        this._outputTail += len;
    }

    private final int _writeBytes(InputStream in, int bytesLeft) throws IOException {
        while (bytesLeft > 0) {
            int count;
            int room = this._outputEnd - this._outputTail;
            if (room <= 0) {
                this._flushBuffer();
                room = this._outputEnd - this._outputTail;
            }
            if ((count = in.read(this._outputBuffer, this._outputTail, room)) < 0) break;
            this._outputTail += count;
            bytesLeft -= count;
        }
        return bytesLeft;
    }

    private final void _writeBytesLong(byte[] data, int offset, int len) throws IOException {
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        while (true) {
            int currLen = Math.min(len, this._outputEnd - this._outputTail);
            System.arraycopy(data, offset, this._outputBuffer, this._outputTail, currLen);
            this._outputTail += currLen;
            if ((len -= currLen) == 0) break;
            offset += currLen;
            this._flushBuffer();
        }
    }

    @Override
    protected void _releaseBuffers() {
        char[] cbuf;
        byte[] buf = this._outputBuffer;
        if (buf != null && this._bufferRecyclable) {
            this._outputBuffer = null;
            this._ioContext.releaseWriteEncodingBuffer(buf);
        }
        if ((cbuf = this._charBuffer) != null) {
            this._charBuffer = null;
            this._ioContext.releaseConcatBuffer(cbuf);
        }
    }

    protected final void _flushBuffer() throws IOException {
        if (this._outputTail > 0) {
            this._bytesWritten += this._outputTail;
            this._out.write(this._outputBuffer, 0, this._outputTail);
            this._outputTail = 0;
        }
    }

    private final void closeComplexElement() throws IOException {
        switch (this._currentRemainingElements) {
            case -2: {
                this._writeByte((byte)-1);
                break;
            }
            case 0: {
                break;
            }
            default: {
                this._reportError(String.format("%s size mismatch: expected %d more elements", this._streamWriteContext.typeDesc(), this._currentRemainingElements));
            }
        }
        this._currentRemainingElements = this._elementCountsPtr == 0 ? -2 : this._elementCounts[--this._elementCountsPtr];
    }

    protected UnsupportedOperationException _notSupported() {
        return new UnsupportedOperationException();
    }

    public static enum Feature implements FormatFeature
    {
        WRITE_MINIMAL_INTS(true),
        WRITE_TYPE_HEADER(false),
        LENIENT_UTF_ENCODING(false);

        protected final boolean _defaultState;
        protected final int _mask;

        public static int collectDefaults() {
            int flags = 0;
            for (Feature f : Feature.values()) {
                if (!f.enabledByDefault()) continue;
                flags |= f.getMask();
            }
            return flags;
        }

        private Feature(boolean defaultState) {
            this._defaultState = defaultState;
            this._mask = 1 << this.ordinal();
        }

        @Override
        public boolean enabledByDefault() {
            return this._defaultState;
        }

        @Override
        public boolean enabledIn(int flags) {
            return (flags & this.getMask()) != 0;
        }

        @Override
        public int getMask() {
            return this._mask;
        }
    }
}

