/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.impl;

import java.io.IOException;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.io.NumberInput;
import org.codehaus.jackson.util.ByteArrayBuilder;

public abstract class JsonParserMinimalBase
extends JsonParser {
    protected static final int INT_TAB = 9;
    protected static final int INT_LF = 10;
    protected static final int INT_CR = 13;
    protected static final int INT_SPACE = 32;
    protected static final int INT_LBRACKET = 91;
    protected static final int INT_RBRACKET = 93;
    protected static final int INT_LCURLY = 123;
    protected static final int INT_RCURLY = 125;
    protected static final int INT_QUOTE = 34;
    protected static final int INT_BACKSLASH = 92;
    protected static final int INT_SLASH = 47;
    protected static final int INT_COLON = 58;
    protected static final int INT_COMMA = 44;
    protected static final int INT_ASTERISK = 42;
    protected static final int INT_APOSTROPHE = 39;
    protected static final int INT_b = 98;
    protected static final int INT_f = 102;
    protected static final int INT_n = 110;
    protected static final int INT_r = 114;
    protected static final int INT_t = 116;
    protected static final int INT_u = 117;

    protected JsonParserMinimalBase() {
    }

    protected JsonParserMinimalBase(int features) {
        super(features);
    }

    public abstract JsonToken nextToken() throws IOException, JsonParseException;

    public JsonParser skipChildren() throws IOException, JsonParseException {
        if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY) {
            return this;
        }
        int open = 1;
        while (true) {
            JsonToken t;
            if ((t = this.nextToken()) == null) {
                this._handleEOF();
                return this;
            }
            switch (t) {
                case START_OBJECT: 
                case START_ARRAY: {
                    ++open;
                    break;
                }
                case END_OBJECT: 
                case END_ARRAY: {
                    if (--open != 0) break;
                    return this;
                }
            }
        }
    }

    protected abstract void _handleEOF() throws JsonParseException;

    public abstract String getCurrentName() throws IOException, JsonParseException;

    public abstract void close() throws IOException;

    public abstract boolean isClosed();

    public abstract JsonStreamContext getParsingContext();

    public abstract String getText() throws IOException, JsonParseException;

    public abstract char[] getTextCharacters() throws IOException, JsonParseException;

    public abstract boolean hasTextCharacters();

    public abstract int getTextLength() throws IOException, JsonParseException;

    public abstract int getTextOffset() throws IOException, JsonParseException;

    public abstract byte[] getBinaryValue(Base64Variant var1) throws IOException, JsonParseException;

    public boolean getValueAsBoolean(boolean defaultValue) throws IOException, JsonParseException {
        if (this._currToken != null) {
            switch (this._currToken) {
                case VALUE_NUMBER_INT: {
                    return this.getIntValue() != 0;
                }
                case VALUE_TRUE: {
                    return true;
                }
                case VALUE_FALSE: 
                case VALUE_NULL: {
                    return false;
                }
                case VALUE_EMBEDDED_OBJECT: {
                    Object value = this.getEmbeddedObject();
                    if (value instanceof Boolean) {
                        return (Boolean)value;
                    }
                }
                case VALUE_STRING: {
                    String str = this.getText().trim();
                    if (!"true".equals(str)) break;
                    return true;
                }
            }
        }
        return defaultValue;
    }

    public int getValueAsInt(int defaultValue) throws IOException, JsonParseException {
        if (this._currToken != null) {
            switch (this._currToken) {
                case VALUE_NUMBER_INT: 
                case VALUE_NUMBER_FLOAT: {
                    return this.getIntValue();
                }
                case VALUE_TRUE: {
                    return 1;
                }
                case VALUE_FALSE: 
                case VALUE_NULL: {
                    return 0;
                }
                case VALUE_STRING: {
                    return NumberInput.parseAsInt(this.getText(), defaultValue);
                }
                case VALUE_EMBEDDED_OBJECT: {
                    Object value = this.getEmbeddedObject();
                    if (!(value instanceof Number)) break;
                    return ((Number)value).intValue();
                }
            }
        }
        return defaultValue;
    }

    public long getValueAsLong(long defaultValue) throws IOException, JsonParseException {
        if (this._currToken != null) {
            switch (this._currToken) {
                case VALUE_NUMBER_INT: 
                case VALUE_NUMBER_FLOAT: {
                    return this.getLongValue();
                }
                case VALUE_TRUE: {
                    return 1L;
                }
                case VALUE_FALSE: 
                case VALUE_NULL: {
                    return 0L;
                }
                case VALUE_STRING: {
                    return NumberInput.parseAsLong(this.getText(), defaultValue);
                }
                case VALUE_EMBEDDED_OBJECT: {
                    Object value = this.getEmbeddedObject();
                    if (!(value instanceof Number)) break;
                    return ((Number)value).longValue();
                }
            }
        }
        return defaultValue;
    }

    public double getValueAsDouble(double defaultValue) throws IOException, JsonParseException {
        if (this._currToken != null) {
            switch (this._currToken) {
                case VALUE_NUMBER_INT: 
                case VALUE_NUMBER_FLOAT: {
                    return this.getDoubleValue();
                }
                case VALUE_TRUE: {
                    return 1.0;
                }
                case VALUE_FALSE: 
                case VALUE_NULL: {
                    return 0.0;
                }
                case VALUE_STRING: {
                    return NumberInput.parseAsDouble(this.getText(), defaultValue);
                }
                case VALUE_EMBEDDED_OBJECT: {
                    Object value = this.getEmbeddedObject();
                    if (!(value instanceof Number)) break;
                    return ((Number)value).doubleValue();
                }
            }
        }
        return defaultValue;
    }

    protected void _decodeBase64(String str, ByteArrayBuilder builder, Base64Variant b64variant) throws IOException, JsonParseException {
        int ptr = 0;
        int len = str.length();
        block0: while (ptr < len) {
            char ch;
            do {
                ch = str.charAt(ptr++);
                if (ptr >= len) break block0;
            } while (ch <= ' ');
            int bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
                this._reportInvalidBase64(b64variant, ch, 0, null);
            }
            int decodedData = bits;
            if (ptr >= len) {
                this._reportBase64EOF();
            }
            if ((bits = b64variant.decodeBase64Char(ch = str.charAt(ptr++))) < 0) {
                this._reportInvalidBase64(b64variant, ch, 1, null);
            }
            decodedData = decodedData << 6 | bits;
            if (ptr >= len) {
                if (!b64variant.usesPadding()) {
                    builder.append(decodedData >>= 4);
                    break;
                }
                this._reportBase64EOF();
            }
            if ((bits = b64variant.decodeBase64Char(ch = str.charAt(ptr++))) < 0) {
                if (bits != -2) {
                    this._reportInvalidBase64(b64variant, ch, 2, null);
                }
                if (ptr >= len) {
                    this._reportBase64EOF();
                }
                if (!b64variant.usesPaddingChar(ch = str.charAt(ptr++))) {
                    this._reportInvalidBase64(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                }
                builder.append(decodedData >>= 4);
                continue;
            }
            decodedData = decodedData << 6 | bits;
            if (ptr >= len) {
                if (!b64variant.usesPadding()) {
                    builder.appendTwoBytes(decodedData >>= 2);
                    break;
                }
                this._reportBase64EOF();
            }
            if ((bits = b64variant.decodeBase64Char(ch = str.charAt(ptr++))) < 0) {
                if (bits != -2) {
                    this._reportInvalidBase64(b64variant, ch, 3, null);
                }
                builder.appendTwoBytes(decodedData >>= 2);
                continue;
            }
            decodedData = decodedData << 6 | bits;
            builder.appendThreeBytes(decodedData);
        }
    }

    protected void _reportInvalidBase64(Base64Variant b64variant, char ch, int bindex, String msg) throws JsonParseException {
        String base = ch <= ' ' ? "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units" : (b64variant.usesPaddingChar(ch) ? "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character" : (!Character.isDefined(ch) || Character.isISOControl(ch) ? "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content" : "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content"));
        if (msg != null) {
            base = base + ": " + msg;
        }
        throw this._constructError(base);
    }

    protected void _reportBase64EOF() throws JsonParseException {
        throw this._constructError("Unexpected end-of-String in base64 content");
    }

    protected void _reportUnexpectedChar(int ch, String comment) throws JsonParseException {
        String msg = "Unexpected character (" + JsonParserMinimalBase._getCharDesc(ch) + ")";
        if (comment != null) {
            msg = msg + ": " + comment;
        }
        this._reportError(msg);
    }

    protected void _reportInvalidEOF() throws JsonParseException {
        this._reportInvalidEOF(" in " + (Object)((Object)this._currToken));
    }

    protected void _reportInvalidEOF(String msg) throws JsonParseException {
        this._reportError("Unexpected end-of-input" + msg);
    }

    protected void _reportInvalidEOFInValue() throws JsonParseException {
        this._reportInvalidEOF(" in a value");
    }

    protected void _throwInvalidSpace(int i) throws JsonParseException {
        char c = (char)i;
        String msg = "Illegal character (" + JsonParserMinimalBase._getCharDesc(c) + "): only regular white space (\\r, \\n, \\t) is allowed between tokens";
        this._reportError(msg);
    }

    protected void _throwUnquotedSpace(int i, String ctxtDesc) throws JsonParseException {
        if (!this.isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS) || i >= 32) {
            char c = (char)i;
            String msg = "Illegal unquoted character (" + JsonParserMinimalBase._getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
            this._reportError(msg);
        }
    }

    protected char _handleUnrecognizedCharacterEscape(char ch) throws JsonProcessingException {
        if (this.isEnabled(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)) {
            return ch;
        }
        if (ch == '\'' && this.isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            return ch;
        }
        this._reportError("Unrecognized character escape " + JsonParserMinimalBase._getCharDesc(ch));
        return ch;
    }

    protected static final String _getCharDesc(int ch) {
        char c = (char)ch;
        if (Character.isISOControl(c)) {
            return "(CTRL-CHAR, code " + ch + ")";
        }
        if (ch > 255) {
            return "'" + c + "' (code " + ch + " / 0x" + Integer.toHexString(ch) + ")";
        }
        return "'" + c + "' (code " + ch + ")";
    }

    protected final void _reportError(String msg) throws JsonParseException {
        throw this._constructError(msg);
    }

    protected final void _wrapError(String msg, Throwable t) throws JsonParseException {
        throw this._constructError(msg, t);
    }

    protected final void _throwInternal() {
        throw new RuntimeException("Internal error: this code path should never get executed");
    }

    protected final JsonParseException _constructError(String msg, Throwable t) {
        return new JsonParseException(msg, this.getCurrentLocation(), t);
    }
}

