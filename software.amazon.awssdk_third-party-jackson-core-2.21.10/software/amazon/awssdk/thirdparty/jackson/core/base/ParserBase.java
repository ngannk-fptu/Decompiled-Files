/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.base;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import software.amazon.awssdk.thirdparty.jackson.core.Base64Variant;
import software.amazon.awssdk.thirdparty.jackson.core.JsonLocation;
import software.amazon.awssdk.thirdparty.jackson.core.JsonParseException;
import software.amazon.awssdk.thirdparty.jackson.core.JsonParser;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;
import software.amazon.awssdk.thirdparty.jackson.core.JsonToken;
import software.amazon.awssdk.thirdparty.jackson.core.StreamReadCapability;
import software.amazon.awssdk.thirdparty.jackson.core.StreamReadConstraints;
import software.amazon.awssdk.thirdparty.jackson.core.StreamReadFeature;
import software.amazon.awssdk.thirdparty.jackson.core.Version;
import software.amazon.awssdk.thirdparty.jackson.core.base.ParserMinimalBase;
import software.amazon.awssdk.thirdparty.jackson.core.io.ContentReference;
import software.amazon.awssdk.thirdparty.jackson.core.io.IOContext;
import software.amazon.awssdk.thirdparty.jackson.core.io.NumberInput;
import software.amazon.awssdk.thirdparty.jackson.core.json.DupDetector;
import software.amazon.awssdk.thirdparty.jackson.core.json.JsonReadContext;
import software.amazon.awssdk.thirdparty.jackson.core.json.PackageVersion;
import software.amazon.awssdk.thirdparty.jackson.core.util.ByteArrayBuilder;
import software.amazon.awssdk.thirdparty.jackson.core.util.JacksonFeatureSet;
import software.amazon.awssdk.thirdparty.jackson.core.util.TextBuffer;

public abstract class ParserBase
extends ParserMinimalBase {
    protected static final JacksonFeatureSet<StreamReadCapability> JSON_READ_CAPABILITIES = DEFAULT_READ_CAPABILITIES;
    protected final IOContext _ioContext;
    protected final StreamReadConstraints _streamReadConstraints;
    protected boolean _closed;
    protected int _inputPtr;
    protected int _inputEnd;
    protected long _currInputProcessed;
    protected int _currInputRow = 1;
    protected int _currInputRowStart;
    protected long _tokenInputTotal;
    protected int _tokenInputRow = 1;
    protected int _tokenInputCol;
    protected JsonReadContext _parsingContext;
    protected JsonToken _nextToken;
    protected final TextBuffer _textBuffer;
    protected char[] _nameCopyBuffer;
    protected boolean _nameCopied;
    protected ByteArrayBuilder _byteArrayBuilder;
    protected byte[] _binaryValue;
    protected int _numTypesValid = 0;
    protected int _numberInt;
    protected long _numberLong;
    protected float _numberFloat;
    protected double _numberDouble;
    protected BigInteger _numberBigInt;
    protected BigDecimal _numberBigDecimal;
    protected String _numberString;
    protected boolean _numberNegative;
    protected int _intLength;
    protected int _fractLength;
    protected int _expLength;

    protected ParserBase(IOContext ctxt, int features) {
        super(features);
        this._ioContext = ctxt;
        StreamReadConstraints streamReadConstraints = ctxt.streamReadConstraints();
        this._streamReadConstraints = streamReadConstraints == null ? StreamReadConstraints.defaults() : streamReadConstraints;
        this._textBuffer = ctxt.constructReadConstrainedTextBuffer();
        DupDetector dups = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
        this._parsingContext = JsonReadContext.createRootContext(dups);
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public Object getCurrentValue() {
        return this._parsingContext.getCurrentValue();
    }

    @Override
    public void setCurrentValue(Object v) {
        this._parsingContext.setCurrentValue(v);
    }

    @Override
    public JsonParser enable(JsonParser.Feature f) {
        this._features |= f.getMask();
        if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION && this._parsingContext.getDupDetector() == null) {
            this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector(this));
        }
        return this;
    }

    @Override
    public JsonParser disable(JsonParser.Feature f) {
        this._features &= ~f.getMask();
        if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION) {
            this._parsingContext = this._parsingContext.withDupDetector(null);
        }
        return this;
    }

    @Override
    @Deprecated
    public JsonParser setFeatureMask(int newMask) {
        int changes = this._features ^ newMask;
        if (changes != 0) {
            this._features = newMask;
            this._checkStdFeatureChanges(newMask, changes);
        }
        return this;
    }

    @Override
    public JsonParser overrideStdFeatures(int values, int mask) {
        int oldState = this._features;
        int newState = oldState & ~mask | values & mask;
        int changed = oldState ^ newState;
        if (changed != 0) {
            this._features = newState;
            this._checkStdFeatureChanges(newState, changed);
        }
        return this;
    }

    protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
        int f = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.getMask();
        if ((changedFeatures & f) != 0 && (newFeatureFlags & f) != 0) {
            this._parsingContext = this._parsingContext.getDupDetector() == null ? this._parsingContext.withDupDetector(DupDetector.rootDetector(this)) : this._parsingContext.withDupDetector(null);
        }
    }

    @Override
    public String getCurrentName() throws IOException {
        JsonReadContext parent;
        if ((this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) && (parent = this._parsingContext.getParent()) != null) {
            return parent.getCurrentName();
        }
        return this._parsingContext.getCurrentName();
    }

    @Override
    public void overrideCurrentName(String name) {
        JsonReadContext ctxt = this._parsingContext;
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            ctxt = ctxt.getParent();
        }
        try {
            ctxt.setCurrentName(name);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (!this._closed) {
            this._inputPtr = Math.max(this._inputPtr, this._inputEnd);
            this._closed = true;
            try {
                this._closeInput();
            }
            finally {
                this._releaseBuffers();
            }
        }
    }

    @Override
    public boolean isClosed() {
        return this._closed;
    }

    @Override
    public JsonReadContext getParsingContext() {
        return this._parsingContext;
    }

    @Override
    public JsonLocation getTokenLocation() {
        return new JsonLocation(this._contentReference(), -1L, this.getTokenCharacterOffset(), this.getTokenLineNr(), this.getTokenColumnNr());
    }

    @Override
    public JsonLocation getCurrentLocation() {
        int col = this._inputPtr - this._currInputRowStart + 1;
        return new JsonLocation(this._contentReference(), -1L, this._currInputProcessed + (long)this._inputPtr, this._currInputRow, col);
    }

    @Override
    public boolean hasTextCharacters() {
        if (this._currToken == JsonToken.VALUE_STRING) {
            return true;
        }
        if (this._currToken == JsonToken.FIELD_NAME) {
            return this._nameCopied;
        }
        return false;
    }

    @Override
    public byte[] getBinaryValue(Base64Variant variant) throws IOException {
        if (this._binaryValue == null) {
            if (this._currToken != JsonToken.VALUE_STRING) {
                this._reportError("Current token (" + (Object)((Object)this._currToken) + ") not VALUE_STRING, can not access as binary");
            }
            ByteArrayBuilder builder = this._getByteArrayBuilder();
            this._decodeBase64(this.getText(), builder, variant);
            this._binaryValue = builder.toByteArray();
        }
        return this._binaryValue;
    }

    public long getTokenCharacterOffset() {
        return this._tokenInputTotal;
    }

    public int getTokenLineNr() {
        return this._tokenInputRow;
    }

    public int getTokenColumnNr() {
        int col = this._tokenInputCol;
        return col < 0 ? col : col + 1;
    }

    protected abstract void _closeInput() throws IOException;

    protected void _releaseBuffers() throws IOException {
        this._textBuffer.releaseBuffers();
        char[] buf = this._nameCopyBuffer;
        if (buf != null) {
            this._nameCopyBuffer = null;
            this._ioContext.releaseNameCopyBuffer(buf);
        }
    }

    @Override
    protected void _handleEOF() throws JsonParseException {
        if (!this._parsingContext.inRoot()) {
            String marker = this._parsingContext.inArray() ? "Array" : "Object";
            this._reportInvalidEOF(String.format(": expected close marker for %s (start marker at %s)", marker, this._parsingContext.startLocation(this._contentReference())), null);
        }
    }

    protected final int _eofAsNextChar() throws JsonParseException {
        this._handleEOF();
        return -1;
    }

    public ByteArrayBuilder _getByteArrayBuilder() {
        if (this._byteArrayBuilder == null) {
            this._byteArrayBuilder = new ByteArrayBuilder();
        } else {
            this._byteArrayBuilder.reset();
        }
        return this._byteArrayBuilder;
    }

    protected final JsonToken reset(boolean negative, int intLen, int fractLen, int expLen) throws IOException {
        if (fractLen < 1 && expLen < 1) {
            return this.resetInt(negative, intLen);
        }
        return this.resetFloat(negative, intLen, fractLen, expLen);
    }

    protected final JsonToken resetInt(boolean negative, int intLen) throws IOException {
        this._streamReadConstraints.validateIntegerLength(intLen);
        this._numberNegative = negative;
        this._intLength = intLen;
        this._fractLength = 0;
        this._expLength = 0;
        this._numTypesValid = 0;
        return JsonToken.VALUE_NUMBER_INT;
    }

    protected final JsonToken resetFloat(boolean negative, int intLen, int fractLen, int expLen) throws IOException {
        this._streamReadConstraints.validateFPLength(intLen + fractLen + expLen);
        this._numberNegative = negative;
        this._intLength = intLen;
        this._fractLength = fractLen;
        this._expLength = expLen;
        this._numTypesValid = 0;
        return JsonToken.VALUE_NUMBER_FLOAT;
    }

    protected final JsonToken resetAsNaN(String valueStr, double value) throws IOException {
        this._textBuffer.resetWithString(valueStr);
        this._numberDouble = value;
        this._numTypesValid = 8;
        return JsonToken.VALUE_NUMBER_FLOAT;
    }

    @Override
    public boolean isNaN() throws IOException {
        if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT && (this._numTypesValid & 8) != 0) {
            return !Double.isFinite(this._getNumberDouble());
        }
        return false;
    }

    @Override
    public Number getNumberValue() throws IOException {
        if (this._numTypesValid == 0) {
            this._parseNumericValue(0);
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if ((this._numTypesValid & 1) != 0) {
                return this._numberInt;
            }
            if ((this._numTypesValid & 2) != 0) {
                return this._numberLong;
            }
            if ((this._numTypesValid & 4) != 0) {
                return this._getBigInteger();
            }
            this._throwInternal();
        }
        if ((this._numTypesValid & 0x10) != 0) {
            return this._getBigDecimal();
        }
        if ((this._numTypesValid & 0x20) != 0) {
            return Float.valueOf(this._getNumberFloat());
        }
        if ((this._numTypesValid & 8) == 0) {
            this._throwInternal();
        }
        return this._getNumberDouble();
    }

    @Override
    public Number getNumberValueExact() throws IOException {
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if (this._numTypesValid == 0) {
                this._parseNumericValue(0);
            }
            if ((this._numTypesValid & 1) != 0) {
                return this._numberInt;
            }
            if ((this._numTypesValid & 2) != 0) {
                return this._numberLong;
            }
            if ((this._numTypesValid & 4) != 0) {
                return this._getBigInteger();
            }
            this._throwInternal();
        }
        if (this._numTypesValid == 0) {
            this._parseNumericValue(16);
        }
        if ((this._numTypesValid & 0x10) != 0) {
            return this._getBigDecimal();
        }
        if ((this._numTypesValid & 0x20) != 0) {
            return Float.valueOf(this._getNumberFloat());
        }
        if ((this._numTypesValid & 8) == 0) {
            this._throwInternal();
        }
        return this._getNumberDouble();
    }

    @Override
    public Object getNumberValueDeferred() throws IOException {
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if (this._numTypesValid == 0) {
                this._parseNumericValue(0);
            }
            if ((this._numTypesValid & 1) != 0) {
                return this._numberInt;
            }
            if ((this._numTypesValid & 2) != 0) {
                return this._numberLong;
            }
            if ((this._numTypesValid & 4) != 0) {
                if (this._numberBigInt != null) {
                    return this._numberBigInt;
                }
                if (this._numberString != null) {
                    return this._numberString;
                }
                return this._getBigInteger();
            }
            this._throwInternal();
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            if ((this._numTypesValid & 0x10) != 0) {
                return this._getBigDecimal();
            }
            if ((this._numTypesValid & 8) != 0) {
                return this._getNumberDouble();
            }
            if ((this._numTypesValid & 0x20) != 0) {
                return Float.valueOf(this._getNumberFloat());
            }
            return this._textBuffer.contentsAsString();
        }
        return this.getNumberValue();
    }

    @Override
    public JsonParser.NumberType getNumberType() throws IOException {
        if (this._numTypesValid == 0) {
            this._parseNumericValue(0);
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if ((this._numTypesValid & 1) != 0) {
                return JsonParser.NumberType.INT;
            }
            if ((this._numTypesValid & 2) != 0) {
                return JsonParser.NumberType.LONG;
            }
            return JsonParser.NumberType.BIG_INTEGER;
        }
        if ((this._numTypesValid & 0x10) != 0) {
            return JsonParser.NumberType.BIG_DECIMAL;
        }
        if ((this._numTypesValid & 0x20) != 0) {
            return JsonParser.NumberType.FLOAT;
        }
        return JsonParser.NumberType.DOUBLE;
    }

    @Override
    public int getIntValue() throws IOException {
        if ((this._numTypesValid & 1) == 0) {
            if (this._numTypesValid == 0) {
                return this._parseIntValue();
            }
            if ((this._numTypesValid & 1) == 0) {
                this.convertNumberToInt();
            }
        }
        return this._numberInt;
    }

    @Override
    public long getLongValue() throws IOException {
        if ((this._numTypesValid & 2) == 0) {
            if (this._numTypesValid == 0) {
                this._parseNumericValue(2);
            }
            if ((this._numTypesValid & 2) == 0) {
                this.convertNumberToLong();
            }
        }
        return this._numberLong;
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException {
        if ((this._numTypesValid & 4) == 0) {
            if (this._numTypesValid == 0) {
                this._parseNumericValue(4);
            }
            if ((this._numTypesValid & 4) == 0) {
                this.convertNumberToBigInteger();
            }
        }
        return this._getBigInteger();
    }

    @Override
    public float getFloatValue() throws IOException {
        if ((this._numTypesValid & 0x20) == 0) {
            if (this._numTypesValid == 0) {
                this._parseNumericValue(32);
            }
            if ((this._numTypesValid & 0x20) == 0) {
                this.convertNumberToFloat();
            }
        }
        return this._getNumberFloat();
    }

    @Override
    public double getDoubleValue() throws IOException {
        if ((this._numTypesValid & 8) == 0) {
            if (this._numTypesValid == 0) {
                this._parseNumericValue(8);
            }
            if ((this._numTypesValid & 8) == 0) {
                this.convertNumberToDouble();
            }
        }
        return this._getNumberDouble();
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException {
        if ((this._numTypesValid & 0x10) == 0) {
            if (this._numTypesValid == 0) {
                this._parseNumericValue(16);
            }
            if ((this._numTypesValid & 0x10) == 0) {
                this.convertNumberToBigDecimal();
            }
        }
        return this._getBigDecimal();
    }

    @Override
    public StreamReadConstraints streamReadConstraints() {
        return this._streamReadConstraints;
    }

    protected void _parseNumericValue(int expType) throws IOException {
        if (this._closed) {
            this._reportError("Internal error: _parseNumericValue called when parser instance closed");
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            int len = this._intLength;
            if (len <= 9) {
                this._numberInt = this._textBuffer.contentsAsInt(this._numberNegative);
                this._numTypesValid = 1;
                return;
            }
            if (len <= 18) {
                long l = this._textBuffer.contentsAsLong(this._numberNegative);
                if (len == 10) {
                    if (this._numberNegative) {
                        if (l >= Integer.MIN_VALUE) {
                            this._numberInt = (int)l;
                            this._numTypesValid = 1;
                            return;
                        }
                    } else if (l <= Integer.MAX_VALUE) {
                        this._numberInt = (int)l;
                        this._numTypesValid = 1;
                        return;
                    }
                }
                this._numberLong = l;
                this._numTypesValid = 2;
                return;
            }
            if (len == 19) {
                char[] buf = this._textBuffer.getTextBuffer();
                int offset = this._textBuffer.getTextOffset();
                if (this._numberNegative) {
                    ++offset;
                }
                if (NumberInput.inLongRange(buf, offset, len, this._numberNegative)) {
                    this._numberLong = NumberInput.parseLong19(buf, offset, this._numberNegative);
                    this._numTypesValid = 2;
                    return;
                }
            }
            this._parseSlowInt(expType);
            return;
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            this._parseSlowFloat(expType);
            return;
        }
        this._reportError("Current token (%s) not numeric, can not use numeric value accessors", (Object)this._currToken);
    }

    protected int _parseIntValue() throws IOException {
        if (this._closed) {
            this._reportError("Internal error: _parseNumericValue called when parser instance closed");
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT && this._intLength <= 9) {
            int i;
            this._numberInt = i = this._textBuffer.contentsAsInt(this._numberNegative);
            this._numTypesValid = 1;
            return i;
        }
        this._parseNumericValue(1);
        if ((this._numTypesValid & 1) == 0) {
            this.convertNumberToInt();
        }
        return this._numberInt;
    }

    private void _parseSlowFloat(int expType) throws IOException {
        if (expType == 16) {
            this._numberBigDecimal = null;
            this._numberString = this._textBuffer.contentsAsString();
            this._numTypesValid = 16;
        } else if (expType == 32) {
            this._numberFloat = 0.0f;
            this._numberString = this._textBuffer.contentsAsString();
            this._numTypesValid = 32;
        } else {
            this._numberDouble = 0.0;
            this._numberString = this._textBuffer.contentsAsString();
            this._numTypesValid = 8;
        }
    }

    private void _parseSlowInt(int expType) throws IOException {
        String numStr = this._textBuffer.contentsAsString();
        if (expType == 1 || expType == 2) {
            this._reportTooLongIntegral(expType, numStr);
        }
        if (expType == 8 || expType == 32) {
            this._numberString = numStr;
            this._numTypesValid = 8;
        } else {
            this._numberBigInt = null;
            this._numberString = numStr;
            this._numTypesValid = 4;
        }
    }

    protected void _reportTooLongIntegral(int expType, String rawNum) throws IOException {
        if (expType == 1) {
            this.reportOverflowInt(rawNum);
        } else {
            this.reportOverflowLong(rawNum);
        }
    }

    protected void convertNumberToInt() throws IOException {
        if ((this._numTypesValid & 2) != 0) {
            int result = (int)this._numberLong;
            if ((long)result != this._numberLong) {
                this.reportOverflowInt(this.getText(), this.currentToken());
            }
            this._numberInt = result;
        } else if ((this._numTypesValid & 4) != 0) {
            BigInteger bigInteger = this._getBigInteger();
            if (BI_MIN_INT.compareTo(bigInteger) > 0 || BI_MAX_INT.compareTo(bigInteger) < 0) {
                this.reportOverflowInt();
            }
            this._numberInt = bigInteger.intValue();
        } else if ((this._numTypesValid & 8) != 0) {
            double d = this._getNumberDouble();
            if (d < -2.147483648E9 || d > 2.147483647E9) {
                this.reportOverflowInt();
            }
            this._numberInt = (int)d;
        } else if ((this._numTypesValid & 0x10) != 0) {
            BigDecimal bigDecimal = this._getBigDecimal();
            if (BD_MIN_INT.compareTo(bigDecimal) > 0 || BD_MAX_INT.compareTo(bigDecimal) < 0) {
                this.reportOverflowInt();
            }
            this._numberInt = bigDecimal.intValue();
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 1;
    }

    protected void convertNumberToLong() throws IOException {
        if ((this._numTypesValid & 1) != 0) {
            this._numberLong = this._numberInt;
        } else if ((this._numTypesValid & 4) != 0) {
            BigInteger bigInteger = this._getBigInteger();
            if (BI_MIN_LONG.compareTo(bigInteger) > 0 || BI_MAX_LONG.compareTo(bigInteger) < 0) {
                this.reportOverflowLong();
            }
            this._numberLong = bigInteger.longValue();
        } else if ((this._numTypesValid & 8) != 0) {
            double d = this._getNumberDouble();
            if (d < -9.223372036854776E18 || d > 9.223372036854776E18) {
                this.reportOverflowLong();
            }
            this._numberLong = (long)d;
        } else if ((this._numTypesValid & 0x10) != 0) {
            BigDecimal bigDecimal = this._getBigDecimal();
            if (BD_MIN_LONG.compareTo(bigDecimal) > 0 || BD_MAX_LONG.compareTo(bigDecimal) < 0) {
                this.reportOverflowLong();
            }
            this._numberLong = bigDecimal.longValue();
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 2;
    }

    protected void convertNumberToBigInteger() throws IOException {
        if ((this._numTypesValid & 0x10) != 0) {
            this._numberBigInt = this._convertBigDecimalToBigInteger(this._getBigDecimal());
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberBigInt = BigInteger.valueOf(this._numberLong);
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberBigInt = BigInteger.valueOf(this._numberInt);
        } else if ((this._numTypesValid & 8) != 0) {
            this._numberBigInt = this._numberString != null ? this._convertBigDecimalToBigInteger(this._getBigDecimal()) : this._convertBigDecimalToBigInteger(BigDecimal.valueOf(this._getNumberDouble()));
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 4;
    }

    protected void convertNumberToDouble() throws IOException {
        if ((this._numTypesValid & 0x10) != 0) {
            this._numberDouble = this._numberString != null ? this._getNumberDouble() : this._getBigDecimal().doubleValue();
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberDouble = this._numberString != null ? this._getNumberDouble() : this._getBigInteger().doubleValue();
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberDouble = this._numberLong;
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberDouble = this._numberInt;
        } else if ((this._numTypesValid & 0x20) != 0) {
            this._numberDouble = this._numberString != null ? this._getNumberDouble() : (double)this._getNumberFloat();
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 8;
    }

    protected void convertNumberToFloat() throws IOException {
        if ((this._numTypesValid & 0x10) != 0) {
            this._numberFloat = this._numberString != null ? this._getNumberFloat() : this._getBigDecimal().floatValue();
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberFloat = this._numberString != null ? this._getNumberFloat() : this._getBigInteger().floatValue();
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberFloat = this._numberLong;
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberFloat = this._numberInt;
        } else if ((this._numTypesValid & 8) != 0) {
            this._numberFloat = this._numberString != null ? this._getNumberFloat() : (float)this._getNumberDouble();
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 0x20;
    }

    protected void convertNumberToBigDecimal() throws IOException {
        if ((this._numTypesValid & 8) != 0) {
            String numStr = this._numberString == null ? this.getText() : this._numberString;
            this._numberBigDecimal = NumberInput.parseBigDecimal(numStr, this.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberBigDecimal = new BigDecimal(this._getBigInteger());
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberBigDecimal = BigDecimal.valueOf(this._numberLong);
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberBigDecimal = BigDecimal.valueOf(this._numberInt);
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 0x10;
    }

    protected BigInteger _convertBigDecimalToBigInteger(BigDecimal bigDec) throws IOException {
        this._streamReadConstraints.validateBigIntegerScale(bigDec.scale());
        return bigDec.toBigInteger();
    }

    protected BigInteger _getBigInteger() throws JsonParseException {
        if (this._numberBigInt != null) {
            return this._numberBigInt;
        }
        if (this._numberString == null) {
            throw new IllegalStateException("cannot get BigInteger from current parser state");
        }
        try {
            this._numberBigInt = NumberInput.parseBigInteger(this._numberString, this.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
        }
        catch (NumberFormatException nex) {
            this._wrapError("Malformed numeric value (" + this._longNumberDesc(this._numberString) + ")", nex);
        }
        this._numberString = null;
        return this._numberBigInt;
    }

    protected BigDecimal _getBigDecimal() throws JsonParseException {
        if (this._numberBigDecimal != null) {
            return this._numberBigDecimal;
        }
        if (this._numberString == null) {
            throw new IllegalStateException("cannot get BigDecimal from current parser state");
        }
        try {
            this._numberBigDecimal = NumberInput.parseBigDecimal(this._numberString, this.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
        }
        catch (NumberFormatException nex) {
            this._wrapError("Malformed numeric value (" + this._longNumberDesc(this._numberString) + ")", nex);
        }
        this._numberString = null;
        return this._numberBigDecimal;
    }

    protected double _getNumberDouble() throws JsonParseException {
        if (this._numberString != null) {
            try {
                this._numberDouble = NumberInput.parseDouble(this._numberString, this.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
            }
            catch (NumberFormatException nex) {
                this._wrapError("Malformed numeric value (" + this._longNumberDesc(this._numberString) + ")", nex);
            }
            this._numberString = null;
        }
        return this._numberDouble;
    }

    protected float _getNumberFloat() throws JsonParseException {
        if (this._numberString != null) {
            try {
                this._numberFloat = NumberInput.parseFloat(this._numberString, this.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
            }
            catch (NumberFormatException nex) {
                this._wrapError("Malformed numeric value (" + this._longNumberDesc(this._numberString) + ")", nex);
            }
            this._numberString = null;
        }
        return this._numberFloat;
    }

    protected void createChildArrayContext(int lineNr, int colNr) throws IOException {
        this._parsingContext = this._parsingContext.createChildArrayContext(lineNr, colNr);
        this._streamReadConstraints.validateNestingDepth(this._parsingContext.getNestingDepth());
    }

    protected void createChildObjectContext(int lineNr, int colNr) throws IOException {
        this._parsingContext = this._parsingContext.createChildObjectContext(lineNr, colNr);
        this._streamReadConstraints.validateNestingDepth(this._parsingContext.getNestingDepth());
    }

    protected void _reportMismatchedEndMarker(int actCh, char expCh) throws JsonParseException {
        JsonReadContext ctxt = this.getParsingContext();
        this._reportError(String.format("Unexpected close marker '%s': expected '%c' (for %s starting at %s)", Character.valueOf((char)actCh), Character.valueOf(expCh), ctxt.typeDesc(), ctxt.startLocation(this._contentReference())));
    }

    protected char _handleUnrecognizedCharacterEscape(char ch) throws JsonProcessingException {
        if (this.isEnabled(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)) {
            return ch;
        }
        if (ch == '\'' && this.isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            return ch;
        }
        this._reportError("Unrecognized character escape " + ParserBase._getCharDesc(ch));
        return ch;
    }

    protected void _throwUnquotedSpace(int i, String ctxtDesc) throws JsonParseException {
        if (!this.isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS) || i > 32) {
            char c = (char)i;
            String msg = "Illegal unquoted character (" + ParserBase._getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
            this._reportError(msg);
        }
    }

    protected String _validJsonTokenList() throws IOException {
        return this._validJsonValueList();
    }

    protected String _validJsonValueList() throws IOException {
        if (this.isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
            return "(JSON String, Number (or 'NaN'/'INF'/'+INF'), Array, Object or token 'null', 'true' or 'false')";
        }
        return "(JSON String, Number, Array, Object or token 'null', 'true' or 'false')";
    }

    protected char _decodeEscaped() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index) throws IOException {
        if (ch != 92) {
            throw this.reportInvalidBase64Char(b64variant, ch, index);
        }
        char unescaped = this._decodeEscaped();
        if (unescaped <= ' ' && index == 0) {
            return -1;
        }
        int bits = b64variant.decodeBase64Char((int)unescaped);
        if (bits < 0 && bits != -2) {
            throw this.reportInvalidBase64Char(b64variant, unescaped, index);
        }
        return bits;
    }

    protected final int _decodeBase64Escape(Base64Variant b64variant, char ch, int index) throws IOException {
        if (ch != '\\') {
            throw this.reportInvalidBase64Char(b64variant, ch, index);
        }
        char unescaped = this._decodeEscaped();
        if (unescaped <= ' ' && index == 0) {
            return -1;
        }
        int bits = b64variant.decodeBase64Char(unescaped);
        if (bits < 0 && (bits != -2 || index < 2)) {
            throw this.reportInvalidBase64Char(b64variant, unescaped, index);
        }
        return bits;
    }

    protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex) throws IllegalArgumentException {
        return this.reportInvalidBase64Char(b64variant, ch, bindex, null);
    }

    protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex, String msg) throws IllegalArgumentException {
        String base = ch <= 32 ? String.format("Illegal white space character (code 0x%s) as character #%d of 4-char base64 unit: can only used between units", Integer.toHexString(ch), bindex + 1) : (b64variant.usesPaddingChar(ch) ? "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character" : (!Character.isDefined(ch) || Character.isISOControl(ch) ? "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content" : "Illegal character '" + (char)ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content"));
        if (msg != null) {
            base = base + ": " + msg;
        }
        return new IllegalArgumentException(base);
    }

    protected void _handleBase64MissingPadding(Base64Variant b64variant) throws IOException {
        this._reportError(b64variant.missingPaddingMessage());
    }

    @Deprecated
    protected Object _getSourceReference() {
        if (JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(this._features)) {
            return this._ioContext.contentReference().getRawContent();
        }
        return null;
    }

    protected ContentReference _contentReference() {
        if (JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(this._features)) {
            return this._ioContext.contentReference();
        }
        return ContentReference.unknown();
    }

    protected static int[] growArrayBy(int[] arr, int more) {
        if (arr == null) {
            return new int[more];
        }
        return Arrays.copyOf(arr, arr.length + more);
    }

    @Deprecated
    protected void loadMoreGuaranteed() throws IOException {
        if (!this.loadMore()) {
            this._reportInvalidEOF();
        }
    }

    @Deprecated
    protected boolean loadMore() throws IOException {
        return false;
    }

    protected void _finishString() throws IOException {
    }
}

