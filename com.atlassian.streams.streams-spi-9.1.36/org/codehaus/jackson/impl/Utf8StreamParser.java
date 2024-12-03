/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.SerializableString;
import org.codehaus.jackson.impl.JsonParserBase;
import org.codehaus.jackson.io.IOContext;
import org.codehaus.jackson.sym.BytesToNameCanonicalizer;
import org.codehaus.jackson.sym.Name;
import org.codehaus.jackson.util.ByteArrayBuilder;
import org.codehaus.jackson.util.CharTypes;

public final class Utf8StreamParser
extends JsonParserBase {
    static final byte BYTE_LF = 10;
    private static final int[] sInputCodesUtf8 = CharTypes.getInputCodeUtf8();
    private static final int[] sInputCodesLatin1 = CharTypes.getInputCodeLatin1();
    protected ObjectCodec _objectCodec;
    protected final BytesToNameCanonicalizer _symbols;
    protected int[] _quadBuffer = new int[16];
    protected boolean _tokenIncomplete = false;
    private int _quad1;
    protected InputStream _inputStream;
    protected byte[] _inputBuffer;
    protected boolean _bufferRecyclable;

    public Utf8StreamParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, BytesToNameCanonicalizer sym, byte[] inputBuffer, int start, int end, boolean bufferRecyclable) {
        super(ctxt, features);
        this._inputStream = in;
        this._objectCodec = codec;
        this._symbols = sym;
        this._inputBuffer = inputBuffer;
        this._inputPtr = start;
        this._inputEnd = end;
        this._bufferRecyclable = bufferRecyclable;
        if (!JsonParser.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(features)) {
            this._throwInternal();
        }
    }

    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    public void setCodec(ObjectCodec c) {
        this._objectCodec = c;
    }

    public int releaseBuffered(OutputStream out) throws IOException {
        int count = this._inputEnd - this._inputPtr;
        if (count < 1) {
            return 0;
        }
        int origPtr = this._inputPtr;
        out.write(this._inputBuffer, origPtr, count);
        return count;
    }

    public Object getInputSource() {
        return this._inputStream;
    }

    protected final boolean loadMore() throws IOException {
        this._currInputProcessed += (long)this._inputEnd;
        this._currInputRowStart -= this._inputEnd;
        if (this._inputStream != null) {
            int count = this._inputStream.read(this._inputBuffer, 0, this._inputBuffer.length);
            if (count > 0) {
                this._inputPtr = 0;
                this._inputEnd = count;
                return true;
            }
            this._closeInput();
            if (count == 0) {
                throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
            }
        }
        return false;
    }

    protected final boolean _loadToHaveAtLeast(int minAvailable) throws IOException {
        if (this._inputStream == null) {
            return false;
        }
        int amount = this._inputEnd - this._inputPtr;
        if (amount > 0 && this._inputPtr > 0) {
            this._currInputProcessed += (long)this._inputPtr;
            this._currInputRowStart -= this._inputPtr;
            System.arraycopy(this._inputBuffer, this._inputPtr, this._inputBuffer, 0, amount);
            this._inputEnd = amount;
        } else {
            this._inputEnd = 0;
        }
        this._inputPtr = 0;
        while (this._inputEnd < minAvailable) {
            int count = this._inputStream.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
            if (count < 1) {
                this._closeInput();
                if (count == 0) {
                    throw new IOException("InputStream.read() returned 0 characters when trying to read " + amount + " bytes");
                }
                return false;
            }
            this._inputEnd += count;
        }
        return true;
    }

    protected void _closeInput() throws IOException {
        if (this._inputStream != null) {
            if (this._ioContext.isResourceManaged() || this.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
                this._inputStream.close();
            }
            this._inputStream = null;
        }
    }

    protected void _releaseBuffers() throws IOException {
        byte[] buf;
        super._releaseBuffers();
        if (this._bufferRecyclable && (buf = this._inputBuffer) != null) {
            this._inputBuffer = null;
            this._ioContext.releaseReadIOBuffer(buf);
        }
    }

    public String getText() throws IOException, JsonParseException {
        JsonToken t = this._currToken;
        if (t == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                this._finishString();
            }
            return this._textBuffer.contentsAsString();
        }
        return this._getText2(t);
    }

    protected final String _getText2(JsonToken t) {
        if (t == null) {
            return null;
        }
        switch (t) {
            case FIELD_NAME: {
                return this._parsingContext.getCurrentName();
            }
            case VALUE_STRING: 
            case VALUE_NUMBER_INT: 
            case VALUE_NUMBER_FLOAT: {
                return this._textBuffer.contentsAsString();
            }
        }
        return t.asString();
    }

    public char[] getTextCharacters() throws IOException, JsonParseException {
        if (this._currToken != null) {
            switch (this._currToken) {
                case FIELD_NAME: {
                    if (!this._nameCopied) {
                        String name = this._parsingContext.getCurrentName();
                        int nameLen = name.length();
                        if (this._nameCopyBuffer == null) {
                            this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
                        } else if (this._nameCopyBuffer.length < nameLen) {
                            this._nameCopyBuffer = new char[nameLen];
                        }
                        name.getChars(0, nameLen, this._nameCopyBuffer, 0);
                        this._nameCopied = true;
                    }
                    return this._nameCopyBuffer;
                }
                case VALUE_STRING: {
                    if (this._tokenIncomplete) {
                        this._tokenIncomplete = false;
                        this._finishString();
                    }
                }
                case VALUE_NUMBER_INT: 
                case VALUE_NUMBER_FLOAT: {
                    return this._textBuffer.getTextBuffer();
                }
            }
            return this._currToken.asCharArray();
        }
        return null;
    }

    public int getTextLength() throws IOException, JsonParseException {
        if (this._currToken != null) {
            switch (this._currToken) {
                case FIELD_NAME: {
                    return this._parsingContext.getCurrentName().length();
                }
                case VALUE_STRING: {
                    if (this._tokenIncomplete) {
                        this._tokenIncomplete = false;
                        this._finishString();
                    }
                }
                case VALUE_NUMBER_INT: 
                case VALUE_NUMBER_FLOAT: {
                    return this._textBuffer.size();
                }
            }
            return this._currToken.asCharArray().length;
        }
        return 0;
    }

    public int getTextOffset() throws IOException, JsonParseException {
        if (this._currToken != null) {
            switch (this._currToken) {
                case FIELD_NAME: {
                    return 0;
                }
                case VALUE_STRING: {
                    if (this._tokenIncomplete) {
                        this._tokenIncomplete = false;
                        this._finishString();
                    }
                }
                case VALUE_NUMBER_INT: 
                case VALUE_NUMBER_FLOAT: {
                    return this._textBuffer.getTextOffset();
                }
            }
        }
        return 0;
    }

    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException, JsonParseException {
        if (this._currToken != JsonToken.VALUE_STRING && (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT || this._binaryValue == null)) {
            this._reportError("Current token (" + (Object)((Object)this._currToken) + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
        }
        if (this._tokenIncomplete) {
            try {
                this._binaryValue = this._decodeBase64(b64variant);
            }
            catch (IllegalArgumentException iae) {
                throw this._constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
            }
            this._tokenIncomplete = false;
        } else if (this._binaryValue == null) {
            ByteArrayBuilder builder = this._getByteArrayBuilder();
            this._decodeBase64(this.getText(), builder, b64variant);
            this._binaryValue = builder.toByteArray();
        }
        return this._binaryValue;
    }

    public JsonToken nextToken() throws IOException, JsonParseException {
        JsonToken t;
        int i;
        this._numTypesValid = 0;
        if (this._currToken == JsonToken.FIELD_NAME) {
            return this._nextAfterName();
        }
        if (this._tokenIncomplete) {
            this._skipString();
        }
        if ((i = this._skipWSOrEnd()) < 0) {
            this.close();
            this._currToken = null;
            return null;
        }
        this._tokenInputTotal = this._currInputProcessed + (long)this._inputPtr - 1L;
        this._tokenInputRow = this._currInputRow;
        this._tokenInputCol = this._inputPtr - this._currInputRowStart - 1;
        this._binaryValue = null;
        if (i == 93) {
            if (!this._parsingContext.inArray()) {
                this._reportMismatchedEndMarker(i, '}');
            }
            this._parsingContext = this._parsingContext.getParent();
            this._currToken = JsonToken.END_ARRAY;
            return this._currToken;
        }
        if (i == 125) {
            if (!this._parsingContext.inObject()) {
                this._reportMismatchedEndMarker(i, ']');
            }
            this._parsingContext = this._parsingContext.getParent();
            this._currToken = JsonToken.END_OBJECT;
            return this._currToken;
        }
        if (this._parsingContext.expectComma()) {
            if (i != 44) {
                this._reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.getTypeDesc() + " entries");
            }
            i = this._skipWS();
        }
        if (!this._parsingContext.inObject()) {
            return this._nextTokenNotInObject(i);
        }
        Name n = this._parseFieldName(i);
        this._parsingContext.setCurrentName(n.getName());
        this._currToken = JsonToken.FIELD_NAME;
        i = this._skipWS();
        if (i != 58) {
            this._reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
        }
        if ((i = this._skipWS()) == 34) {
            this._tokenIncomplete = true;
            this._nextToken = JsonToken.VALUE_STRING;
            return this._currToken;
        }
        switch (i) {
            case 91: {
                t = JsonToken.START_ARRAY;
                break;
            }
            case 123: {
                t = JsonToken.START_OBJECT;
                break;
            }
            case 93: 
            case 125: {
                this._reportUnexpectedChar(i, "expected a value");
            }
            case 116: {
                this._matchToken("true", 1);
                t = JsonToken.VALUE_TRUE;
                break;
            }
            case 102: {
                this._matchToken("false", 1);
                t = JsonToken.VALUE_FALSE;
                break;
            }
            case 110: {
                this._matchToken("null", 1);
                t = JsonToken.VALUE_NULL;
                break;
            }
            case 45: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: {
                t = this.parseNumberText(i);
                break;
            }
            default: {
                t = this._handleUnexpectedValue(i);
            }
        }
        this._nextToken = t;
        return this._currToken;
    }

    private final JsonToken _nextTokenNotInObject(int i) throws IOException, JsonParseException {
        if (i == 34) {
            this._tokenIncomplete = true;
            this._currToken = JsonToken.VALUE_STRING;
            return this._currToken;
        }
        switch (i) {
            case 91: {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                this._currToken = JsonToken.START_ARRAY;
                return this._currToken;
            }
            case 123: {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                this._currToken = JsonToken.START_OBJECT;
                return this._currToken;
            }
            case 93: 
            case 125: {
                this._reportUnexpectedChar(i, "expected a value");
            }
            case 116: {
                this._matchToken("true", 1);
                this._currToken = JsonToken.VALUE_TRUE;
                return this._currToken;
            }
            case 102: {
                this._matchToken("false", 1);
                this._currToken = JsonToken.VALUE_FALSE;
                return this._currToken;
            }
            case 110: {
                this._matchToken("null", 1);
                this._currToken = JsonToken.VALUE_NULL;
                return this._currToken;
            }
            case 45: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: {
                this._currToken = this.parseNumberText(i);
                return this._currToken;
            }
        }
        this._currToken = this._handleUnexpectedValue(i);
        return this._currToken;
    }

    private final JsonToken _nextAfterName() {
        this._nameCopied = false;
        JsonToken t = this._nextToken;
        this._nextToken = null;
        if (t == JsonToken.START_ARRAY) {
            this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
        } else if (t == JsonToken.START_OBJECT) {
            this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
        }
        this._currToken = t;
        return this._currToken;
    }

    public void close() throws IOException {
        super.close();
        this._symbols.release();
    }

    public boolean nextFieldName(SerializableString str) throws IOException, JsonParseException {
        int end;
        byte[] nameBytes;
        int len;
        int i;
        this._numTypesValid = 0;
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nextAfterName();
            return false;
        }
        if (this._tokenIncomplete) {
            this._skipString();
        }
        if ((i = this._skipWSOrEnd()) < 0) {
            this.close();
            this._currToken = null;
            return false;
        }
        this._tokenInputTotal = this._currInputProcessed + (long)this._inputPtr - 1L;
        this._tokenInputRow = this._currInputRow;
        this._tokenInputCol = this._inputPtr - this._currInputRowStart - 1;
        this._binaryValue = null;
        if (i == 93) {
            if (!this._parsingContext.inArray()) {
                this._reportMismatchedEndMarker(i, '}');
            }
            this._parsingContext = this._parsingContext.getParent();
            this._currToken = JsonToken.END_ARRAY;
            return false;
        }
        if (i == 125) {
            if (!this._parsingContext.inObject()) {
                this._reportMismatchedEndMarker(i, ']');
            }
            this._parsingContext = this._parsingContext.getParent();
            this._currToken = JsonToken.END_OBJECT;
            return false;
        }
        if (this._parsingContext.expectComma()) {
            if (i != 44) {
                this._reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.getTypeDesc() + " entries");
            }
            i = this._skipWS();
        }
        if (!this._parsingContext.inObject()) {
            this._nextTokenNotInObject(i);
            return false;
        }
        if (i == 34 && this._inputPtr + (len = (nameBytes = str.asQuotedUTF8()).length) < this._inputEnd && this._inputBuffer[end = this._inputPtr + len] == 34) {
            int offset = 0;
            int ptr = this._inputPtr;
            while (true) {
                if (offset == len) {
                    this._inputPtr = end + 1;
                    this._parsingContext.setCurrentName(str.getValue());
                    this._currToken = JsonToken.FIELD_NAME;
                    this._isNextTokenNameYes();
                    return true;
                }
                if (nameBytes[offset] != this._inputBuffer[ptr + offset]) break;
                ++offset;
            }
        }
        this._isNextTokenNameNo(i);
        return false;
    }

    private final void _isNextTokenNameYes() throws IOException, JsonParseException {
        int i;
        if (this._inputPtr < this._inputEnd - 1 && this._inputBuffer[this._inputPtr] == 58) {
            ++this._inputPtr;
            if ((i = this._inputBuffer[this._inputPtr++]) == 34) {
                this._tokenIncomplete = true;
                this._nextToken = JsonToken.VALUE_STRING;
                return;
            }
            if (i == 123) {
                this._nextToken = JsonToken.START_OBJECT;
                return;
            }
            if (i == 91) {
                this._nextToken = JsonToken.START_ARRAY;
                return;
            }
            if ((i &= 0xFF) <= 32 || i == 47) {
                --this._inputPtr;
                i = this._skipWS();
            }
        } else {
            i = this._skipColon();
        }
        switch (i) {
            case 34: {
                this._tokenIncomplete = true;
                this._nextToken = JsonToken.VALUE_STRING;
                return;
            }
            case 91: {
                this._nextToken = JsonToken.START_ARRAY;
                return;
            }
            case 123: {
                this._nextToken = JsonToken.START_OBJECT;
                return;
            }
            case 93: 
            case 125: {
                this._reportUnexpectedChar(i, "expected a value");
            }
            case 116: {
                this._matchToken("true", 1);
                this._nextToken = JsonToken.VALUE_TRUE;
                return;
            }
            case 102: {
                this._matchToken("false", 1);
                this._nextToken = JsonToken.VALUE_FALSE;
                return;
            }
            case 110: {
                this._matchToken("null", 1);
                this._nextToken = JsonToken.VALUE_NULL;
                return;
            }
            case 45: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: {
                this._nextToken = this.parseNumberText(i);
                return;
            }
        }
        this._nextToken = this._handleUnexpectedValue(i);
    }

    private final void _isNextTokenNameNo(int i) throws IOException, JsonParseException {
        JsonToken t;
        Name n = this._parseFieldName(i);
        this._parsingContext.setCurrentName(n.getName());
        this._currToken = JsonToken.FIELD_NAME;
        i = this._skipWS();
        if (i != 58) {
            this._reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
        }
        if ((i = this._skipWS()) == 34) {
            this._tokenIncomplete = true;
            this._nextToken = JsonToken.VALUE_STRING;
            return;
        }
        switch (i) {
            case 91: {
                t = JsonToken.START_ARRAY;
                break;
            }
            case 123: {
                t = JsonToken.START_OBJECT;
                break;
            }
            case 93: 
            case 125: {
                this._reportUnexpectedChar(i, "expected a value");
            }
            case 116: {
                this._matchToken("true", 1);
                t = JsonToken.VALUE_TRUE;
                break;
            }
            case 102: {
                this._matchToken("false", 1);
                t = JsonToken.VALUE_FALSE;
                break;
            }
            case 110: {
                this._matchToken("null", 1);
                t = JsonToken.VALUE_NULL;
                break;
            }
            case 45: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: {
                t = this.parseNumberText(i);
                break;
            }
            default: {
                t = this._handleUnexpectedValue(i);
            }
        }
        this._nextToken = t;
    }

    public String nextTextValue() throws IOException, JsonParseException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            JsonToken t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_STRING) {
                if (this._tokenIncomplete) {
                    this._tokenIncomplete = false;
                    this._finishString();
                }
                return this._textBuffer.contentsAsString();
            }
            if (t == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }
            return null;
        }
        return this.nextToken() == JsonToken.VALUE_STRING ? this.getText() : null;
    }

    public int nextIntValue(int defaultValue) throws IOException, JsonParseException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            JsonToken t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_NUMBER_INT) {
                return this.getIntValue();
            }
            if (t == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }
            return defaultValue;
        }
        return this.nextToken() == JsonToken.VALUE_NUMBER_INT ? this.getIntValue() : defaultValue;
    }

    public long nextLongValue(long defaultValue) throws IOException, JsonParseException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            JsonToken t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_NUMBER_INT) {
                return this.getLongValue();
            }
            if (t == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }
            return defaultValue;
        }
        return this.nextToken() == JsonToken.VALUE_NUMBER_INT ? this.getLongValue() : defaultValue;
    }

    public Boolean nextBooleanValue() throws IOException, JsonParseException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            JsonToken t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            if (t == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }
            return null;
        }
        switch (this.nextToken()) {
            case VALUE_TRUE: {
                return Boolean.TRUE;
            }
            case VALUE_FALSE: {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    protected final JsonToken parseNumberText(int c) throws IOException, JsonParseException {
        boolean negative;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr = 0;
        boolean bl = negative = c == 45;
        if (negative) {
            outBuf[outPtr++] = 45;
            if (this._inputPtr >= this._inputEnd) {
                this.loadMoreGuaranteed();
            }
            if ((c = this._inputBuffer[this._inputPtr++] & 0xFF) < 48 || c > 57) {
                return this._handleInvalidNumberStart(c, true);
            }
        }
        if (c == 48) {
            c = this._verifyNoLeadingZeroes();
        }
        outBuf[outPtr++] = (char)c;
        int intLen = 1;
        int end = this._inputPtr + outBuf.length;
        if (end > this._inputEnd) {
            end = this._inputEnd;
        }
        while (true) {
            if (this._inputPtr >= end) {
                return this._parserNumber2(outBuf, outPtr, negative, intLen);
            }
            if ((c = this._inputBuffer[this._inputPtr++] & 0xFF) < 48 || c > 57) break;
            ++intLen;
            outBuf[outPtr++] = (char)c;
        }
        if (c == 46 || c == 101 || c == 69) {
            return this._parseFloatText(outBuf, outPtr, c, negative, intLen);
        }
        --this._inputPtr;
        this._textBuffer.setCurrentLength(outPtr);
        return this.resetInt(negative, intLen);
    }

    private final JsonToken _parserNumber2(char[] outBuf, int outPtr, boolean negative, int intPartLength) throws IOException, JsonParseException {
        while (true) {
            int c;
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._textBuffer.setCurrentLength(outPtr);
                return this.resetInt(negative, intPartLength);
            }
            if ((c = this._inputBuffer[this._inputPtr++] & 0xFF) > 57 || c < 48) {
                if (c != 46 && c != 101 && c != 69) break;
                return this._parseFloatText(outBuf, outPtr, c, negative, intPartLength);
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = (char)c;
            ++intPartLength;
        }
        --this._inputPtr;
        this._textBuffer.setCurrentLength(outPtr);
        return this.resetInt(negative, intPartLength);
    }

    private final int _verifyNoLeadingZeroes() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            return 48;
        }
        int ch = this._inputBuffer[this._inputPtr] & 0xFF;
        if (ch < 48 || ch > 57) {
            return 48;
        }
        if (!this.isEnabled(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
            this.reportInvalidNumber("Leading zeroes not allowed");
        }
        ++this._inputPtr;
        if (ch == 48) {
            while (this._inputPtr < this._inputEnd || this.loadMore()) {
                ch = this._inputBuffer[this._inputPtr] & 0xFF;
                if (ch < 48 || ch > 57) {
                    return 48;
                }
                ++this._inputPtr;
                if (ch == 48) continue;
                break;
            }
        }
        return ch;
    }

    private final JsonToken _parseFloatText(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException, JsonParseException {
        int fractLen = 0;
        boolean eof = false;
        if (c == 46) {
            outBuf[outPtr++] = (char)c;
            while (true) {
                if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                    eof = true;
                    break;
                }
                if ((c = this._inputBuffer[this._inputPtr++] & 0xFF) < 48 || c > 57) break;
                ++fractLen;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                outBuf[outPtr++] = (char)c;
            }
            if (fractLen == 0) {
                this.reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
            }
        }
        int expLen = 0;
        if (c == 101 || c == 69) {
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = (char)c;
            if (this._inputPtr >= this._inputEnd) {
                this.loadMoreGuaranteed();
            }
            if ((c = this._inputBuffer[this._inputPtr++] & 0xFF) == 45 || c == 43) {
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                outBuf[outPtr++] = (char)c;
                if (this._inputPtr >= this._inputEnd) {
                    this.loadMoreGuaranteed();
                }
                c = this._inputBuffer[this._inputPtr++] & 0xFF;
            }
            while (c <= 57 && c >= 48) {
                ++expLen;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                outBuf[outPtr++] = (char)c;
                if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                    eof = true;
                    break;
                }
                c = this._inputBuffer[this._inputPtr++] & 0xFF;
            }
            if (expLen == 0) {
                this.reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
            }
        }
        if (!eof) {
            --this._inputPtr;
        }
        this._textBuffer.setCurrentLength(outPtr);
        return this.resetFloat(negative, integerPartLength, fractLen, expLen);
    }

    protected final Name _parseFieldName(int i) throws IOException, JsonParseException {
        int q;
        if (i != 34) {
            return this._handleUnusualFieldName(i);
        }
        if (this._inputPtr + 9 > this._inputEnd) {
            return this.slowParseFieldName();
        }
        int[] codes = sInputCodesLatin1;
        byte[] input = this._inputBuffer;
        if (codes[q = input[this._inputPtr++] & 0xFF] == 0) {
            if (codes[i = input[this._inputPtr++] & 0xFF] == 0) {
                q = q << 8 | i;
                if (codes[i = input[this._inputPtr++] & 0xFF] == 0) {
                    q = q << 8 | i;
                    if (codes[i = input[this._inputPtr++] & 0xFF] == 0) {
                        q = q << 8 | i;
                        if (codes[i = input[this._inputPtr++] & 0xFF] == 0) {
                            this._quad1 = q;
                            return this.parseMediumFieldName(i, codes);
                        }
                        if (i == 34) {
                            return this.findName(q, 4);
                        }
                        return this.parseFieldName(q, i, 4);
                    }
                    if (i == 34) {
                        return this.findName(q, 3);
                    }
                    return this.parseFieldName(q, i, 3);
                }
                if (i == 34) {
                    return this.findName(q, 2);
                }
                return this.parseFieldName(q, i, 2);
            }
            if (i == 34) {
                return this.findName(q, 1);
            }
            return this.parseFieldName(q, i, 1);
        }
        if (q == 34) {
            return BytesToNameCanonicalizer.getEmptyName();
        }
        return this.parseFieldName(0, q, 0);
    }

    protected final Name parseMediumFieldName(int q2, int[] codes) throws IOException, JsonParseException {
        int i;
        if (codes[i = this._inputBuffer[this._inputPtr++] & 0xFF] != 0) {
            if (i == 34) {
                return this.findName(this._quad1, q2, 1);
            }
            return this.parseFieldName(this._quad1, q2, i, 1);
        }
        q2 = q2 << 8 | i;
        if (codes[i = this._inputBuffer[this._inputPtr++] & 0xFF] != 0) {
            if (i == 34) {
                return this.findName(this._quad1, q2, 2);
            }
            return this.parseFieldName(this._quad1, q2, i, 2);
        }
        q2 = q2 << 8 | i;
        if (codes[i = this._inputBuffer[this._inputPtr++] & 0xFF] != 0) {
            if (i == 34) {
                return this.findName(this._quad1, q2, 3);
            }
            return this.parseFieldName(this._quad1, q2, i, 3);
        }
        q2 = q2 << 8 | i;
        if (codes[i = this._inputBuffer[this._inputPtr++] & 0xFF] != 0) {
            if (i == 34) {
                return this.findName(this._quad1, q2, 4);
            }
            return this.parseFieldName(this._quad1, q2, i, 4);
        }
        this._quadBuffer[0] = this._quad1;
        this._quadBuffer[1] = q2;
        return this.parseLongFieldName(i);
    }

    protected Name parseLongFieldName(int q) throws IOException, JsonParseException {
        int[] codes = sInputCodesLatin1;
        int qlen = 2;
        while (this._inputEnd - this._inputPtr >= 4) {
            int i;
            if (codes[i = this._inputBuffer[this._inputPtr++] & 0xFF] != 0) {
                if (i == 34) {
                    return this.findName(this._quadBuffer, qlen, q, 1);
                }
                return this.parseEscapedFieldName(this._quadBuffer, qlen, q, i, 1);
            }
            q = q << 8 | i;
            if (codes[i = this._inputBuffer[this._inputPtr++] & 0xFF] != 0) {
                if (i == 34) {
                    return this.findName(this._quadBuffer, qlen, q, 2);
                }
                return this.parseEscapedFieldName(this._quadBuffer, qlen, q, i, 2);
            }
            q = q << 8 | i;
            if (codes[i = this._inputBuffer[this._inputPtr++] & 0xFF] != 0) {
                if (i == 34) {
                    return this.findName(this._quadBuffer, qlen, q, 3);
                }
                return this.parseEscapedFieldName(this._quadBuffer, qlen, q, i, 3);
            }
            q = q << 8 | i;
            if (codes[i = this._inputBuffer[this._inputPtr++] & 0xFF] != 0) {
                if (i == 34) {
                    return this.findName(this._quadBuffer, qlen, q, 4);
                }
                return this.parseEscapedFieldName(this._quadBuffer, qlen, q, i, 4);
            }
            if (qlen >= this._quadBuffer.length) {
                this._quadBuffer = Utf8StreamParser.growArrayBy(this._quadBuffer, qlen);
            }
            this._quadBuffer[qlen++] = q;
            q = i;
        }
        return this.parseEscapedFieldName(this._quadBuffer, qlen, 0, q, 0);
    }

    protected Name slowParseFieldName() throws IOException, JsonParseException {
        int i;
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            this._reportInvalidEOF(": was expecting closing '\"' for name");
        }
        if ((i = this._inputBuffer[this._inputPtr++] & 0xFF) == 34) {
            return BytesToNameCanonicalizer.getEmptyName();
        }
        return this.parseEscapedFieldName(this._quadBuffer, 0, 0, i, 0);
    }

    private final Name parseFieldName(int q1, int ch, int lastQuadBytes) throws IOException, JsonParseException {
        return this.parseEscapedFieldName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
    }

    private final Name parseFieldName(int q1, int q2, int ch, int lastQuadBytes) throws IOException, JsonParseException {
        this._quadBuffer[0] = q1;
        return this.parseEscapedFieldName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
    }

    protected Name parseEscapedFieldName(int[] quads, int qlen, int currQuad, int ch, int currQuadBytes) throws IOException, JsonParseException {
        Name name;
        int[] codes = sInputCodesLatin1;
        while (true) {
            if (codes[ch] != 0) {
                if (ch == 34) break;
                if (ch != 92) {
                    this._throwUnquotedSpace(ch, "name");
                } else {
                    ch = this._decodeEscaped();
                }
                if (ch > 127) {
                    if (currQuadBytes >= 4) {
                        if (qlen >= quads.length) {
                            quads = Utf8StreamParser.growArrayBy(quads, quads.length);
                            this._quadBuffer = quads;
                        }
                        quads[qlen++] = currQuad;
                        currQuad = 0;
                        currQuadBytes = 0;
                    }
                    if (ch < 2048) {
                        currQuad = currQuad << 8 | (0xC0 | ch >> 6);
                        ++currQuadBytes;
                    } else {
                        currQuad = currQuad << 8 | (0xE0 | ch >> 12);
                        if (++currQuadBytes >= 4) {
                            if (qlen >= quads.length) {
                                quads = Utf8StreamParser.growArrayBy(quads, quads.length);
                                this._quadBuffer = quads;
                            }
                            quads[qlen++] = currQuad;
                            currQuad = 0;
                            currQuadBytes = 0;
                        }
                        currQuad = currQuad << 8 | (0x80 | ch >> 6 & 0x3F);
                        ++currQuadBytes;
                    }
                    ch = 0x80 | ch & 0x3F;
                }
            }
            if (currQuadBytes < 4) {
                ++currQuadBytes;
                currQuad = currQuad << 8 | ch;
            } else {
                if (qlen >= quads.length) {
                    quads = Utf8StreamParser.growArrayBy(quads, quads.length);
                    this._quadBuffer = quads;
                }
                quads[qlen++] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            }
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._reportInvalidEOF(" in field name");
            }
            ch = this._inputBuffer[this._inputPtr++] & 0xFF;
        }
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                quads = Utf8StreamParser.growArrayBy(quads, quads.length);
                this._quadBuffer = quads;
            }
            quads[qlen++] = currQuad;
        }
        if ((name = this._symbols.findName(quads, qlen)) == null) {
            name = this.addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    protected final Name _handleUnusualFieldName(int ch) throws IOException, JsonParseException {
        Name name;
        int[] codes;
        if (ch == 39 && this.isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            return this._parseApostropheFieldName();
        }
        if (!this.isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
            this._reportUnexpectedChar(ch, "was expecting double-quote to start field name");
        }
        if ((codes = CharTypes.getInputCodeUtf8JsNames())[ch] != 0) {
            this._reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
        }
        int[] quads = this._quadBuffer;
        int qlen = 0;
        int currQuad = 0;
        int currQuadBytes = 0;
        while (true) {
            if (currQuadBytes < 4) {
                ++currQuadBytes;
                currQuad = currQuad << 8 | ch;
            } else {
                if (qlen >= quads.length) {
                    this._quadBuffer = quads = Utf8StreamParser.growArrayBy(quads, quads.length);
                }
                quads[qlen++] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            }
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._reportInvalidEOF(" in field name");
            }
            if (codes[ch = this._inputBuffer[this._inputPtr] & 0xFF] != 0) break;
            ++this._inputPtr;
        }
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                this._quadBuffer = quads = Utf8StreamParser.growArrayBy(quads, quads.length);
            }
            quads[qlen++] = currQuad;
        }
        if ((name = this._symbols.findName(quads, qlen)) == null) {
            name = this.addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    protected final Name _parseApostropheFieldName() throws IOException, JsonParseException {
        Name name;
        int ch;
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            this._reportInvalidEOF(": was expecting closing ''' for name");
        }
        if ((ch = this._inputBuffer[this._inputPtr++] & 0xFF) == 39) {
            return BytesToNameCanonicalizer.getEmptyName();
        }
        int[] quads = this._quadBuffer;
        int qlen = 0;
        int currQuad = 0;
        int currQuadBytes = 0;
        int[] codes = sInputCodesLatin1;
        while (ch != 39) {
            if (ch != 34 && codes[ch] != 0) {
                if (ch != 92) {
                    this._throwUnquotedSpace(ch, "name");
                } else {
                    ch = this._decodeEscaped();
                }
                if (ch > 127) {
                    if (currQuadBytes >= 4) {
                        if (qlen >= quads.length) {
                            this._quadBuffer = quads = Utf8StreamParser.growArrayBy(quads, quads.length);
                        }
                        quads[qlen++] = currQuad;
                        currQuad = 0;
                        currQuadBytes = 0;
                    }
                    if (ch < 2048) {
                        currQuad = currQuad << 8 | (0xC0 | ch >> 6);
                        ++currQuadBytes;
                    } else {
                        currQuad = currQuad << 8 | (0xE0 | ch >> 12);
                        if (++currQuadBytes >= 4) {
                            if (qlen >= quads.length) {
                                this._quadBuffer = quads = Utf8StreamParser.growArrayBy(quads, quads.length);
                            }
                            quads[qlen++] = currQuad;
                            currQuad = 0;
                            currQuadBytes = 0;
                        }
                        currQuad = currQuad << 8 | (0x80 | ch >> 6 & 0x3F);
                        ++currQuadBytes;
                    }
                    ch = 0x80 | ch & 0x3F;
                }
            }
            if (currQuadBytes < 4) {
                ++currQuadBytes;
                currQuad = currQuad << 8 | ch;
            } else {
                if (qlen >= quads.length) {
                    this._quadBuffer = quads = Utf8StreamParser.growArrayBy(quads, quads.length);
                }
                quads[qlen++] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            }
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._reportInvalidEOF(" in field name");
            }
            ch = this._inputBuffer[this._inputPtr++] & 0xFF;
        }
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                this._quadBuffer = quads = Utf8StreamParser.growArrayBy(quads, quads.length);
            }
            quads[qlen++] = currQuad;
        }
        if ((name = this._symbols.findName(quads, qlen)) == null) {
            name = this.addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    private final Name findName(int q1, int lastQuadBytes) throws JsonParseException {
        Name name = this._symbols.findName(q1);
        if (name != null) {
            return name;
        }
        this._quadBuffer[0] = q1;
        return this.addName(this._quadBuffer, 1, lastQuadBytes);
    }

    private final Name findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
        Name name = this._symbols.findName(q1, q2);
        if (name != null) {
            return name;
        }
        this._quadBuffer[0] = q1;
        this._quadBuffer[1] = q2;
        return this.addName(this._quadBuffer, 2, lastQuadBytes);
    }

    private final Name findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException {
        if (qlen >= quads.length) {
            quads = Utf8StreamParser.growArrayBy(quads, quads.length);
            this._quadBuffer = quads;
        }
        quads[qlen++] = lastQuad;
        Name name = this._symbols.findName(quads, qlen);
        if (name == null) {
            return this.addName(quads, qlen, lastQuadBytes);
        }
        return name;
    }

    private final Name addName(int[] quads, int qlen, int lastQuadBytes) throws JsonParseException {
        int lastQuad;
        int byteLen = (qlen << 2) - 4 + lastQuadBytes;
        if (lastQuadBytes < 4) {
            lastQuad = quads[qlen - 1];
            quads[qlen - 1] = lastQuad << (4 - lastQuadBytes << 3);
        } else {
            lastQuad = 0;
        }
        char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
        int cix = 0;
        int ix = 0;
        while (ix < byteLen) {
            int ch = quads[ix >> 2];
            int byteIx = ix & 3;
            ch = ch >> (3 - byteIx << 3) & 0xFF;
            ++ix;
            if (ch > 127) {
                int needed;
                if ((ch & 0xE0) == 192) {
                    ch &= 0x1F;
                    needed = 1;
                } else if ((ch & 0xF0) == 224) {
                    ch &= 0xF;
                    needed = 2;
                } else if ((ch & 0xF8) == 240) {
                    ch &= 7;
                    needed = 3;
                } else {
                    this._reportInvalidInitial(ch);
                    ch = 1;
                    needed = 1;
                }
                if (ix + needed > byteLen) {
                    this._reportInvalidEOF(" in field name");
                }
                int ch2 = quads[ix >> 2];
                byteIx = ix & 3;
                ++ix;
                if (((ch2 >>= 3 - byteIx << 3) & 0xC0) != 128) {
                    this._reportInvalidOther(ch2);
                }
                ch = ch << 6 | ch2 & 0x3F;
                if (needed > 1) {
                    ch2 = quads[ix >> 2];
                    byteIx = ix & 3;
                    ++ix;
                    if (((ch2 >>= 3 - byteIx << 3) & 0xC0) != 128) {
                        this._reportInvalidOther(ch2);
                    }
                    ch = ch << 6 | ch2 & 0x3F;
                    if (needed > 2) {
                        ch2 = quads[ix >> 2];
                        byteIx = ix & 3;
                        ++ix;
                        if (((ch2 >>= 3 - byteIx << 3) & 0xC0) != 128) {
                            this._reportInvalidOther(ch2 & 0xFF);
                        }
                        ch = ch << 6 | ch2 & 0x3F;
                    }
                }
                if (needed > 2) {
                    ch -= 65536;
                    if (cix >= cbuf.length) {
                        cbuf = this._textBuffer.expandCurrentSegment();
                    }
                    cbuf[cix++] = (char)(55296 + (ch >> 10));
                    ch = 0xDC00 | ch & 0x3FF;
                }
            }
            if (cix >= cbuf.length) {
                cbuf = this._textBuffer.expandCurrentSegment();
            }
            cbuf[cix++] = (char)ch;
        }
        String baseName = new String(cbuf, 0, cix);
        if (lastQuadBytes < 4) {
            quads[qlen - 1] = lastQuad;
        }
        return this._symbols.addName(baseName, quads, qlen);
    }

    protected void _finishString() throws IOException, JsonParseException {
        int ptr = this._inputPtr;
        if (ptr >= this._inputEnd) {
            this.loadMoreGuaranteed();
            ptr = this._inputPtr;
        }
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = sInputCodesUtf8;
        int max = Math.min(this._inputEnd, ptr + outBuf.length);
        byte[] inputBuffer = this._inputBuffer;
        while (ptr < max) {
            int c = inputBuffer[ptr] & 0xFF;
            if (codes[c] != 0) {
                if (c != 34) break;
                this._inputPtr = ptr + 1;
                this._textBuffer.setCurrentLength(outPtr);
                return;
            }
            ++ptr;
            outBuf[outPtr++] = (char)c;
        }
        this._inputPtr = ptr;
        this._finishString2(outBuf, outPtr);
    }

    private final void _finishString2(char[] outBuf, int outPtr) throws IOException, JsonParseException {
        int[] codes = sInputCodesUtf8;
        byte[] inputBuffer = this._inputBuffer;
        while (true) {
            int c;
            int ptr;
            block15: {
                if ((ptr = this._inputPtr) >= this._inputEnd) {
                    this.loadMoreGuaranteed();
                    ptr = this._inputPtr;
                }
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                int max = Math.min(this._inputEnd, ptr + (outBuf.length - outPtr));
                while (ptr < max) {
                    if (codes[c = inputBuffer[ptr++] & 0xFF] == 0) {
                        outBuf[outPtr++] = (char)c;
                        continue;
                    }
                    break block15;
                }
                this._inputPtr = ptr;
                continue;
            }
            this._inputPtr = ptr;
            if (c == 34) break;
            switch (codes[c]) {
                case 1: {
                    c = this._decodeEscaped();
                    break;
                }
                case 2: {
                    c = this._decodeUtf8_2(c);
                    break;
                }
                case 3: {
                    if (this._inputEnd - this._inputPtr >= 2) {
                        c = this._decodeUtf8_3fast(c);
                        break;
                    }
                    c = this._decodeUtf8_3(c);
                    break;
                }
                case 4: {
                    c = this._decodeUtf8_4(c);
                    outBuf[outPtr++] = (char)(0xD800 | c >> 10);
                    if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.finishCurrentSegment();
                        outPtr = 0;
                    }
                    c = 0xDC00 | c & 0x3FF;
                    break;
                }
                default: {
                    if (c < 32) {
                        this._throwUnquotedSpace(c, "string value");
                        break;
                    }
                    this._reportInvalidChar(c);
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = (char)c;
        }
        this._textBuffer.setCurrentLength(outPtr);
    }

    protected void _skipString() throws IOException, JsonParseException {
        this._tokenIncomplete = false;
        int[] codes = sInputCodesUtf8;
        byte[] inputBuffer = this._inputBuffer;
        block6: while (true) {
            int c;
            int ptr;
            block10: {
                int max;
                if ((ptr = this._inputPtr) >= (max = this._inputEnd)) {
                    this.loadMoreGuaranteed();
                    ptr = this._inputPtr;
                    max = this._inputEnd;
                }
                while (ptr < max) {
                    if (codes[c = inputBuffer[ptr++] & 0xFF] == 0) continue;
                    break block10;
                }
                this._inputPtr = ptr;
                continue;
            }
            this._inputPtr = ptr;
            if (c == 34) break;
            switch (codes[c]) {
                case 1: {
                    this._decodeEscaped();
                    continue block6;
                }
                case 2: {
                    this._skipUtf8_2(c);
                    continue block6;
                }
                case 3: {
                    this._skipUtf8_3(c);
                    continue block6;
                }
                case 4: {
                    this._skipUtf8_4(c);
                    continue block6;
                }
            }
            if (c < 32) {
                this._throwUnquotedSpace(c, "string value");
                continue;
            }
            this._reportInvalidChar(c);
        }
    }

    protected JsonToken _handleUnexpectedValue(int c) throws IOException, JsonParseException {
        switch (c) {
            case 39: {
                if (!this.isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) break;
                return this._handleApostropheValue();
            }
            case 78: {
                this._matchToken("NaN", 1);
                if (this.isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    return this.resetAsNaN("NaN", Double.NaN);
                }
                this._reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
                break;
            }
            case 43: {
                if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                    this._reportInvalidEOFInValue();
                }
                return this._handleInvalidNumberStart(this._inputBuffer[this._inputPtr++] & 0xFF, false);
            }
        }
        this._reportUnexpectedChar(c, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
        return null;
    }

    protected JsonToken _handleApostropheValue() throws IOException, JsonParseException {
        int c = 0;
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = sInputCodesUtf8;
        byte[] inputBuffer = this._inputBuffer;
        block6: while (true) {
            int max;
            int max2;
            if (this._inputPtr >= this._inputEnd) {
                this.loadMoreGuaranteed();
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            if ((max2 = this._inputPtr + (outBuf.length - outPtr)) < (max = this._inputEnd)) {
                max = max2;
            }
            while (true) {
                if (this._inputPtr >= max) continue block6;
                if ((c = inputBuffer[this._inputPtr++] & 0xFF) == 39 || codes[c] != 0) break;
                outBuf[outPtr++] = (char)c;
            }
            if (c == 39) break;
            switch (codes[c]) {
                case 1: {
                    if (c == 34) break;
                    c = this._decodeEscaped();
                    break;
                }
                case 2: {
                    c = this._decodeUtf8_2(c);
                    break;
                }
                case 3: {
                    if (this._inputEnd - this._inputPtr >= 2) {
                        c = this._decodeUtf8_3fast(c);
                        break;
                    }
                    c = this._decodeUtf8_3(c);
                    break;
                }
                case 4: {
                    c = this._decodeUtf8_4(c);
                    outBuf[outPtr++] = (char)(0xD800 | c >> 10);
                    if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.finishCurrentSegment();
                        outPtr = 0;
                    }
                    c = 0xDC00 | c & 0x3FF;
                    break;
                }
                default: {
                    if (c < 32) {
                        this._throwUnquotedSpace(c, "string value");
                    }
                    this._reportInvalidChar(c);
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = (char)c;
        }
        this._textBuffer.setCurrentLength(outPtr);
        return JsonToken.VALUE_STRING;
    }

    protected JsonToken _handleInvalidNumberStart(int ch, boolean negative) throws IOException, JsonParseException {
        if (ch == 73) {
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._reportInvalidEOFInValue();
            }
            if ((ch = this._inputBuffer[this._inputPtr++]) == 78) {
                String match = negative ? "-INF" : "+INF";
                this._matchToken(match, 3);
                if (this.isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    return this.resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
                }
                this._reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
            } else if (ch == 110) {
                String match = negative ? "-Infinity" : "+Infinity";
                this._matchToken(match, 3);
                if (this.isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    return this.resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
                }
                this._reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
            }
        }
        this.reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
        return null;
    }

    protected final void _matchToken(String matchStr, int i) throws IOException, JsonParseException {
        int len = matchStr.length();
        do {
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._reportInvalidEOF(" in a value");
            }
            if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
                this._reportInvalidToken(matchStr.substring(0, i), "'null', 'true', 'false' or NaN");
            }
            ++this._inputPtr;
        } while (++i < len);
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            return;
        }
        int ch = this._inputBuffer[this._inputPtr] & 0xFF;
        if (ch < 48 || ch == 93 || ch == 125) {
            return;
        }
        char c = (char)this._decodeCharForError(ch);
        if (Character.isJavaIdentifierPart(c)) {
            ++this._inputPtr;
            this._reportInvalidToken(matchStr.substring(0, i), "'null', 'true', 'false' or NaN");
        }
    }

    protected void _reportInvalidToken(String matchedPart, String msg) throws IOException, JsonParseException {
        byte i;
        char c;
        StringBuilder sb = new StringBuilder(matchedPart);
        while ((this._inputPtr < this._inputEnd || this.loadMore()) && Character.isJavaIdentifierPart(c = (char)this._decodeCharForError(i = this._inputBuffer[this._inputPtr++]))) {
            sb.append(c);
        }
        this._reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
    }

    private final int _skipWS() throws IOException, JsonParseException {
        while (this._inputPtr < this._inputEnd || this.loadMore()) {
            int i;
            if ((i = this._inputBuffer[this._inputPtr++] & 0xFF) > 32) {
                if (i != 47) {
                    return i;
                }
                this._skipComment();
                continue;
            }
            if (i == 32) continue;
            if (i == 10) {
                this._skipLF();
                continue;
            }
            if (i == 13) {
                this._skipCR();
                continue;
            }
            if (i == 9) continue;
            this._throwInvalidSpace(i);
        }
        throw this._constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
    }

    private final int _skipWSOrEnd() throws IOException, JsonParseException {
        while (this._inputPtr < this._inputEnd || this.loadMore()) {
            int i;
            if ((i = this._inputBuffer[this._inputPtr++] & 0xFF) > 32) {
                if (i != 47) {
                    return i;
                }
                this._skipComment();
                continue;
            }
            if (i == 32) continue;
            if (i == 10) {
                this._skipLF();
                continue;
            }
            if (i == 13) {
                this._skipCR();
                continue;
            }
            if (i == 9) continue;
            this._throwInvalidSpace(i);
        }
        this._handleEOF();
        return -1;
    }

    private final int _skipColon() throws IOException, JsonParseException {
        int i;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if ((i = this._inputBuffer[this._inputPtr++]) == 58) {
            if (this._inputPtr < this._inputEnd && (i = this._inputBuffer[this._inputPtr] & 0xFF) > 32 && i != 47) {
                ++this._inputPtr;
                return i;
            }
        } else {
            i &= 0xFF;
            block6: while (true) {
                switch (i) {
                    case 9: 
                    case 32: {
                        break;
                    }
                    case 13: {
                        this._skipCR();
                        break;
                    }
                    case 10: {
                        this._skipLF();
                        break;
                    }
                    case 47: {
                        this._skipComment();
                        break;
                    }
                    default: {
                        if (i >= 32) break block6;
                        this._throwInvalidSpace(i);
                        break block6;
                    }
                }
                if (this._inputPtr >= this._inputEnd) {
                    this.loadMoreGuaranteed();
                }
                i = this._inputBuffer[this._inputPtr++] & 0xFF;
            }
            if (i != 58) {
                this._reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
            }
        }
        while (this._inputPtr < this._inputEnd || this.loadMore()) {
            if ((i = this._inputBuffer[this._inputPtr++] & 0xFF) > 32) {
                if (i != 47) {
                    return i;
                }
                this._skipComment();
                continue;
            }
            if (i == 32) continue;
            if (i == 10) {
                this._skipLF();
                continue;
            }
            if (i == 13) {
                this._skipCR();
                continue;
            }
            if (i == 9) continue;
            this._throwInvalidSpace(i);
        }
        throw this._constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
    }

    private final void _skipComment() throws IOException, JsonParseException {
        int c;
        if (!this.isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
            this._reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            this._reportInvalidEOF(" in a comment");
        }
        if ((c = this._inputBuffer[this._inputPtr++] & 0xFF) == 47) {
            this._skipCppComment();
        } else if (c == 42) {
            this._skipCComment();
        } else {
            this._reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
        }
    }

    private final void _skipCComment() throws IOException, JsonParseException {
        int[] codes = CharTypes.getInputCodeComment();
        block8: while (this._inputPtr < this._inputEnd || this.loadMore()) {
            int i;
            int code;
            if ((code = codes[i = this._inputBuffer[this._inputPtr++] & 0xFF]) == 0) continue;
            switch (code) {
                case 42: {
                    if (this._inputPtr >= this._inputEnd && !this.loadMore()) break block8;
                    if (this._inputBuffer[this._inputPtr] != 47) continue block8;
                    ++this._inputPtr;
                    return;
                }
                case 10: {
                    this._skipLF();
                    break;
                }
                case 13: {
                    this._skipCR();
                    break;
                }
                case 2: {
                    this._skipUtf8_2(i);
                    break;
                }
                case 3: {
                    this._skipUtf8_3(i);
                    break;
                }
                case 4: {
                    this._skipUtf8_4(i);
                    break;
                }
                default: {
                    this._reportInvalidChar(i);
                }
            }
        }
        this._reportInvalidEOF(" in a comment");
    }

    private final void _skipCppComment() throws IOException, JsonParseException {
        int[] codes = CharTypes.getInputCodeComment();
        block8: while (this._inputPtr < this._inputEnd || this.loadMore()) {
            int i;
            int code;
            if ((code = codes[i = this._inputBuffer[this._inputPtr++] & 0xFF]) == 0) continue;
            switch (code) {
                case 10: {
                    this._skipLF();
                    return;
                }
                case 13: {
                    this._skipCR();
                    return;
                }
                case 42: {
                    continue block8;
                }
                case 2: {
                    this._skipUtf8_2(i);
                    continue block8;
                }
                case 3: {
                    this._skipUtf8_3(i);
                    continue block8;
                }
                case 4: {
                    this._skipUtf8_4(i);
                    continue block8;
                }
            }
            this._reportInvalidChar(i);
        }
    }

    protected final char _decodeEscaped() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            this._reportInvalidEOF(" in character escape sequence");
        }
        byte c = this._inputBuffer[this._inputPtr++];
        switch (c) {
            case 98: {
                return '\b';
            }
            case 116: {
                return '\t';
            }
            case 110: {
                return '\n';
            }
            case 102: {
                return '\f';
            }
            case 114: {
                return '\r';
            }
            case 34: 
            case 47: 
            case 92: {
                return (char)c;
            }
            case 117: {
                break;
            }
            default: {
                return this._handleUnrecognizedCharacterEscape((char)this._decodeCharForError(c));
            }
        }
        int value = 0;
        for (int i = 0; i < 4; ++i) {
            byte ch;
            int digit;
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._reportInvalidEOF(" in character escape sequence");
            }
            if ((digit = CharTypes.charToHex(ch = this._inputBuffer[this._inputPtr++])) < 0) {
                this._reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
            }
            value = value << 4 | digit;
        }
        return (char)value;
    }

    protected int _decodeCharForError(int firstByte) throws IOException, JsonParseException {
        int c = firstByte;
        if (c < 0) {
            int needed;
            if ((c & 0xE0) == 192) {
                c &= 0x1F;
                needed = 1;
            } else if ((c & 0xF0) == 224) {
                c &= 0xF;
                needed = 2;
            } else if ((c & 0xF8) == 240) {
                c &= 7;
                needed = 3;
            } else {
                this._reportInvalidInitial(c & 0xFF);
                needed = 1;
            }
            int d = this.nextByte();
            if ((d & 0xC0) != 128) {
                this._reportInvalidOther(d & 0xFF);
            }
            c = c << 6 | d & 0x3F;
            if (needed > 1) {
                d = this.nextByte();
                if ((d & 0xC0) != 128) {
                    this._reportInvalidOther(d & 0xFF);
                }
                c = c << 6 | d & 0x3F;
                if (needed > 2) {
                    d = this.nextByte();
                    if ((d & 0xC0) != 128) {
                        this._reportInvalidOther(d & 0xFF);
                    }
                    c = c << 6 | d & 0x3F;
                }
            }
        }
        return c;
    }

    private final int _decodeUtf8_2(int c) throws IOException, JsonParseException {
        byte d;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        return (c & 0x1F) << 6 | d & 0x3F;
    }

    private final int _decodeUtf8_3(int c1) throws IOException, JsonParseException {
        byte d;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        c1 &= 0xF;
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        int c = c1 << 6 | d & 0x3F;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        c = c << 6 | d & 0x3F;
        return c;
    }

    private final int _decodeUtf8_3fast(int c1) throws IOException, JsonParseException {
        byte d;
        c1 &= 0xF;
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        int c = c1 << 6 | d & 0x3F;
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        c = c << 6 | d & 0x3F;
        return c;
    }

    private final int _decodeUtf8_4(int c) throws IOException, JsonParseException {
        byte d;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        c = (c & 7) << 6 | d & 0x3F;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        c = c << 6 | d & 0x3F;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        return (c << 6 | d & 0x3F) - 65536;
    }

    private final void _skipUtf8_2(int c) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((c = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(c & 0xFF, this._inputPtr);
        }
    }

    private final void _skipUtf8_3(int c) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((c = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(c & 0xFF, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((c = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(c & 0xFF, this._inputPtr);
        }
    }

    private final void _skipUtf8_4(int c) throws IOException, JsonParseException {
        byte d;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if (((d = this._inputBuffer[this._inputPtr++]) & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
    }

    protected final void _skipCR() throws IOException {
        if ((this._inputPtr < this._inputEnd || this.loadMore()) && this._inputBuffer[this._inputPtr] == 10) {
            ++this._inputPtr;
        }
        ++this._currInputRow;
        this._currInputRowStart = this._inputPtr;
    }

    protected final void _skipLF() throws IOException {
        ++this._currInputRow;
        this._currInputRowStart = this._inputPtr;
    }

    private int nextByte() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        return this._inputBuffer[this._inputPtr++] & 0xFF;
    }

    protected void _reportInvalidChar(int c) throws JsonParseException {
        if (c < 32) {
            this._throwInvalidSpace(c);
        }
        this._reportInvalidInitial(c);
    }

    protected void _reportInvalidInitial(int mask) throws JsonParseException {
        this._reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
    }

    protected void _reportInvalidOther(int mask) throws JsonParseException {
        this._reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
    }

    protected void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
        this._inputPtr = ptr;
        this._reportInvalidOther(mask);
    }

    public static int[] growArrayBy(int[] arr, int more) {
        if (arr == null) {
            return new int[more];
        }
        int[] old = arr;
        int len = arr.length;
        arr = new int[len + more];
        System.arraycopy(old, 0, arr, 0, len);
        return arr;
    }

    protected byte[] _decodeBase64(Base64Variant b64variant) throws IOException, JsonParseException {
        ByteArrayBuilder builder = this._getByteArrayBuilder();
        while (true) {
            int ch;
            if (this._inputPtr >= this._inputEnd) {
                this.loadMoreGuaranteed();
            }
            if ((ch = this._inputBuffer[this._inputPtr++] & 0xFF) <= 32) continue;
            int bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
                if (ch == 34) {
                    return builder.toByteArray();
                }
                bits = this._decodeBase64Escape(b64variant, ch, 0);
                if (bits < 0) continue;
            }
            int decodedData = bits;
            if (this._inputPtr >= this._inputEnd) {
                this.loadMoreGuaranteed();
            }
            if ((bits = b64variant.decodeBase64Char(ch = this._inputBuffer[this._inputPtr++] & 0xFF)) < 0) {
                bits = this._decodeBase64Escape(b64variant, ch, 1);
            }
            decodedData = decodedData << 6 | bits;
            if (this._inputPtr >= this._inputEnd) {
                this.loadMoreGuaranteed();
            }
            if ((bits = b64variant.decodeBase64Char(ch = this._inputBuffer[this._inputPtr++] & 0xFF)) < 0) {
                if (bits != -2) {
                    if (ch == 34 && !b64variant.usesPadding()) {
                        builder.append(decodedData >>= 4);
                        return builder.toByteArray();
                    }
                    bits = this._decodeBase64Escape(b64variant, ch, 2);
                }
                if (bits == -2) {
                    if (this._inputPtr >= this._inputEnd) {
                        this.loadMoreGuaranteed();
                    }
                    if (!b64variant.usesPaddingChar(ch = this._inputBuffer[this._inputPtr++] & 0xFF)) {
                        throw this.reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                    }
                    builder.append(decodedData >>= 4);
                    continue;
                }
            }
            decodedData = decodedData << 6 | bits;
            if (this._inputPtr >= this._inputEnd) {
                this.loadMoreGuaranteed();
            }
            if ((bits = b64variant.decodeBase64Char(ch = this._inputBuffer[this._inputPtr++] & 0xFF)) < 0) {
                if (bits != -2) {
                    if (ch == 34 && !b64variant.usesPadding()) {
                        builder.appendTwoBytes(decodedData >>= 2);
                        return builder.toByteArray();
                    }
                    bits = this._decodeBase64Escape(b64variant, ch, 3);
                }
                if (bits == -2) {
                    builder.appendTwoBytes(decodedData >>= 2);
                    continue;
                }
            }
            decodedData = decodedData << 6 | bits;
            builder.appendThreeBytes(decodedData);
        }
    }
}

