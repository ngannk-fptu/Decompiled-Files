/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.impl;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.SerializableString;
import org.codehaus.jackson.impl.JsonGeneratorBase;
import org.codehaus.jackson.impl.JsonWriteContext;
import org.codehaus.jackson.io.CharacterEscapes;
import org.codehaus.jackson.io.IOContext;
import org.codehaus.jackson.io.NumberOutput;
import org.codehaus.jackson.io.SerializedString;
import org.codehaus.jackson.util.CharTypes;

public final class WriterBasedGenerator
extends JsonGeneratorBase {
    protected static final int SHORT_WRITE = 32;
    protected static final char[] HEX_CHARS = CharTypes.copyHexChars();
    protected static final int[] sOutputEscapes = CharTypes.get7BitOutputEscapes();
    protected final IOContext _ioContext;
    protected final Writer _writer;
    protected int[] _outputEscapes = sOutputEscapes;
    protected int _maximumNonEscapedChar;
    protected CharacterEscapes _characterEscapes;
    protected SerializableString _currentEscape;
    protected char[] _outputBuffer;
    protected int _outputHead = 0;
    protected int _outputTail = 0;
    protected int _outputEnd;
    protected char[] _entityBuffer;

    public WriterBasedGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w) {
        super(features, codec);
        this._ioContext = ctxt;
        this._writer = w;
        this._outputBuffer = ctxt.allocConcatBuffer();
        this._outputEnd = this._outputBuffer.length;
        if (this.isEnabled(JsonGenerator.Feature.ESCAPE_NON_ASCII)) {
            this.setHighestNonEscapedChar(127);
        }
    }

    public JsonGenerator setHighestNonEscapedChar(int charCode) {
        this._maximumNonEscapedChar = charCode < 0 ? 0 : charCode;
        return this;
    }

    public int getHighestEscapedChar() {
        return this._maximumNonEscapedChar;
    }

    public JsonGenerator setCharacterEscapes(CharacterEscapes esc) {
        this._characterEscapes = esc;
        this._outputEscapes = esc == null ? sOutputEscapes : esc.getEscapeCodesForAscii();
        return this;
    }

    public CharacterEscapes getCharacterEscapes() {
        return this._characterEscapes;
    }

    public Object getOutputTarget() {
        return this._writer;
    }

    public final void writeFieldName(String name) throws IOException, JsonGenerationException {
        int status = this._writeContext.writeFieldName(name);
        if (status == 4) {
            this._reportError("Can not write a field name, expecting a value");
        }
        this._writeFieldName(name, status == 1);
    }

    public final void writeStringField(String fieldName, String value) throws IOException, JsonGenerationException {
        this.writeFieldName(fieldName);
        this.writeString(value);
    }

    public final void writeFieldName(SerializedString name) throws IOException, JsonGenerationException {
        int status = this._writeContext.writeFieldName(name.getValue());
        if (status == 4) {
            this._reportError("Can not write a field name, expecting a value");
        }
        this._writeFieldName(name, status == 1);
    }

    public final void writeFieldName(SerializableString name) throws IOException, JsonGenerationException {
        int status = this._writeContext.writeFieldName(name.getValue());
        if (status == 4) {
            this._reportError("Can not write a field name, expecting a value");
        }
        this._writeFieldName(name, status == 1);
    }

    public final void writeStartArray() throws IOException, JsonGenerationException {
        this._verifyValueWrite("start an array");
        this._writeContext = this._writeContext.createChildArrayContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartArray(this);
        } else {
            if (this._outputTail >= this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail++] = 91;
        }
    }

    public final void writeEndArray() throws IOException, JsonGenerationException {
        if (!this._writeContext.inArray()) {
            this._reportError("Current context not an ARRAY but " + this._writeContext.getTypeDesc());
        }
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
        } else {
            if (this._outputTail >= this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail++] = 93;
        }
        this._writeContext = this._writeContext.getParent();
    }

    public final void writeStartObject() throws IOException, JsonGenerationException {
        this._verifyValueWrite("start an object");
        this._writeContext = this._writeContext.createChildObjectContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartObject(this);
        } else {
            if (this._outputTail >= this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail++] = 123;
        }
    }

    public final void writeEndObject() throws IOException, JsonGenerationException {
        if (!this._writeContext.inObject()) {
            this._reportError("Current context not an object but " + this._writeContext.getTypeDesc());
        }
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
        } else {
            if (this._outputTail >= this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail++] = 125;
        }
        this._writeContext = this._writeContext.getParent();
    }

    protected void _writeFieldName(String name, boolean commaBefore) throws IOException, JsonGenerationException {
        if (this._cfgPrettyPrinter != null) {
            this._writePPFieldName(name, commaBefore);
            return;
        }
        if (this._outputTail + 1 >= this._outputEnd) {
            this._flushBuffer();
        }
        if (commaBefore) {
            this._outputBuffer[this._outputTail++] = 44;
        }
        if (!this.isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
            this._writeString(name);
            return;
        }
        this._outputBuffer[this._outputTail++] = 34;
        this._writeString(name);
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
    }

    public void _writeFieldName(SerializableString name, boolean commaBefore) throws IOException, JsonGenerationException {
        if (this._cfgPrettyPrinter != null) {
            this._writePPFieldName(name, commaBefore);
            return;
        }
        if (this._outputTail + 1 >= this._outputEnd) {
            this._flushBuffer();
        }
        if (commaBefore) {
            this._outputBuffer[this._outputTail++] = 44;
        }
        char[] quoted = name.asQuotedChars();
        if (!this.isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
            this.writeRaw(quoted, 0, quoted.length);
            return;
        }
        this._outputBuffer[this._outputTail++] = 34;
        int qlen = quoted.length;
        if (this._outputTail + qlen + 1 >= this._outputEnd) {
            this.writeRaw(quoted, 0, qlen);
            if (this._outputTail >= this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail++] = 34;
        } else {
            System.arraycopy(quoted, 0, this._outputBuffer, this._outputTail, qlen);
            this._outputTail += qlen;
            this._outputBuffer[this._outputTail++] = 34;
        }
    }

    protected final void _writePPFieldName(String name, boolean commaBefore) throws IOException, JsonGenerationException {
        if (commaBefore) {
            this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
        } else {
            this._cfgPrettyPrinter.beforeObjectEntries(this);
        }
        if (this.isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
            if (this._outputTail >= this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail++] = 34;
            this._writeString(name);
            if (this._outputTail >= this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail++] = 34;
        } else {
            this._writeString(name);
        }
    }

    protected final void _writePPFieldName(SerializableString name, boolean commaBefore) throws IOException, JsonGenerationException {
        if (commaBefore) {
            this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
        } else {
            this._cfgPrettyPrinter.beforeObjectEntries(this);
        }
        char[] quoted = name.asQuotedChars();
        if (this.isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
            if (this._outputTail >= this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail++] = 34;
            this.writeRaw(quoted, 0, quoted.length);
            if (this._outputTail >= this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail++] = 34;
        } else {
            this.writeRaw(quoted, 0, quoted.length);
        }
    }

    public void writeString(String text) throws IOException, JsonGenerationException {
        this._verifyValueWrite("write text value");
        if (text == null) {
            this._writeNull();
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
        this._writeString(text);
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
    }

    public void writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        this._verifyValueWrite("write text value");
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
        this._writeString(text, offset, len);
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
    }

    public final void writeString(SerializableString sstr) throws IOException, JsonGenerationException {
        this._verifyValueWrite("write text value");
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
        char[] text = sstr.asQuotedChars();
        int len = text.length;
        if (len < 32) {
            int room = this._outputEnd - this._outputTail;
            if (len > room) {
                this._flushBuffer();
            }
            System.arraycopy(text, 0, this._outputBuffer, this._outputTail, len);
            this._outputTail += len;
        } else {
            this._flushBuffer();
            this._writer.write(text, 0, len);
        }
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
    }

    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }

    public void writeRaw(String text) throws IOException, JsonGenerationException {
        int len = text.length();
        int room = this._outputEnd - this._outputTail;
        if (room == 0) {
            this._flushBuffer();
            room = this._outputEnd - this._outputTail;
        }
        if (room >= len) {
            text.getChars(0, len, this._outputBuffer, this._outputTail);
            this._outputTail += len;
        } else {
            this.writeRawLong(text);
        }
    }

    public void writeRaw(String text, int start, int len) throws IOException, JsonGenerationException {
        int room = this._outputEnd - this._outputTail;
        if (room < len) {
            this._flushBuffer();
            room = this._outputEnd - this._outputTail;
        }
        if (room >= len) {
            text.getChars(start, start + len, this._outputBuffer, this._outputTail);
            this._outputTail += len;
        } else {
            this.writeRawLong(text.substring(start, start + len));
        }
    }

    public void writeRaw(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        if (len < 32) {
            int room = this._outputEnd - this._outputTail;
            if (len > room) {
                this._flushBuffer();
            }
            System.arraycopy(text, offset, this._outputBuffer, this._outputTail, len);
            this._outputTail += len;
            return;
        }
        this._flushBuffer();
        this._writer.write(text, offset, len);
    }

    public void writeRaw(char c) throws IOException, JsonGenerationException {
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = c;
    }

    private void writeRawLong(String text) throws IOException, JsonGenerationException {
        int len;
        int amount;
        int room = this._outputEnd - this._outputTail;
        text.getChars(0, room, this._outputBuffer, this._outputTail);
        this._outputTail += room;
        this._flushBuffer();
        int offset = room;
        for (len = text.length() - room; len > this._outputEnd; len -= amount) {
            amount = this._outputEnd;
            text.getChars(offset, offset + amount, this._outputBuffer, 0);
            this._outputHead = 0;
            this._outputTail = amount;
            this._flushBuffer();
            offset += amount;
        }
        text.getChars(offset, offset + len, this._outputBuffer, 0);
        this._outputHead = 0;
        this._outputTail = len;
    }

    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
        this._verifyValueWrite("write binary value");
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
        this._writeBinary(b64variant, data, offset, offset + len);
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
    }

    public void writeNumber(int i) throws IOException, JsonGenerationException {
        this._verifyValueWrite("write number");
        if (this._cfgNumbersAsStrings) {
            this._writeQuotedInt(i);
            return;
        }
        if (this._outputTail + 11 >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
    }

    private final void _writeQuotedInt(int i) throws IOException {
        if (this._outputTail + 13 >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
        this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
        this._outputBuffer[this._outputTail++] = 34;
    }

    public void writeNumber(long l) throws IOException, JsonGenerationException {
        this._verifyValueWrite("write number");
        if (this._cfgNumbersAsStrings) {
            this._writeQuotedLong(l);
            return;
        }
        if (this._outputTail + 21 >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
    }

    private final void _writeQuotedLong(long l) throws IOException {
        if (this._outputTail + 23 >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
        this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
        this._outputBuffer[this._outputTail++] = 34;
    }

    public void writeNumber(BigInteger value) throws IOException, JsonGenerationException {
        this._verifyValueWrite("write number");
        if (value == null) {
            this._writeNull();
        } else if (this._cfgNumbersAsStrings) {
            this._writeQuotedRaw(value);
        } else {
            this.writeRaw(value.toString());
        }
    }

    public void writeNumber(double d) throws IOException, JsonGenerationException {
        if (this._cfgNumbersAsStrings || (Double.isNaN(d) || Double.isInfinite(d)) && this.isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS)) {
            this.writeString(String.valueOf(d));
            return;
        }
        this._verifyValueWrite("write number");
        this.writeRaw(String.valueOf(d));
    }

    public void writeNumber(float f) throws IOException, JsonGenerationException {
        if (this._cfgNumbersAsStrings || (Float.isNaN(f) || Float.isInfinite(f)) && this.isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS)) {
            this.writeString(String.valueOf(f));
            return;
        }
        this._verifyValueWrite("write number");
        this.writeRaw(String.valueOf(f));
    }

    public void writeNumber(BigDecimal value) throws IOException, JsonGenerationException {
        this._verifyValueWrite("write number");
        if (value == null) {
            this._writeNull();
        } else if (this._cfgNumbersAsStrings) {
            this._writeQuotedRaw(value);
        } else {
            this.writeRaw(value.toString());
        }
    }

    public void writeNumber(String encodedValue) throws IOException, JsonGenerationException {
        this._verifyValueWrite("write number");
        if (this._cfgNumbersAsStrings) {
            this._writeQuotedRaw(encodedValue);
        } else {
            this.writeRaw(encodedValue);
        }
    }

    private final void _writeQuotedRaw(Object value) throws IOException {
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
        this.writeRaw(value.toString());
        if (this._outputTail >= this._outputEnd) {
            this._flushBuffer();
        }
        this._outputBuffer[this._outputTail++] = 34;
    }

    public void writeBoolean(boolean state) throws IOException, JsonGenerationException {
        this._verifyValueWrite("write boolean value");
        if (this._outputTail + 5 >= this._outputEnd) {
            this._flushBuffer();
        }
        int ptr = this._outputTail;
        char[] buf = this._outputBuffer;
        if (state) {
            buf[ptr] = 116;
            buf[++ptr] = 114;
            buf[++ptr] = 117;
            buf[++ptr] = 101;
        } else {
            buf[ptr] = 102;
            buf[++ptr] = 97;
            buf[++ptr] = 108;
            buf[++ptr] = 115;
            buf[++ptr] = 101;
        }
        this._outputTail = ptr + 1;
    }

    public void writeNull() throws IOException, JsonGenerationException {
        this._verifyValueWrite("write null value");
        this._writeNull();
    }

    protected final void _verifyValueWrite(String typeMsg) throws IOException, JsonGenerationException {
        int status = this._writeContext.writeValue();
        if (status == 5) {
            this._reportError("Can not " + typeMsg + ", expecting field name");
        }
        if (this._cfgPrettyPrinter == null) {
            int c;
            switch (status) {
                case 1: {
                    c = 44;
                    break;
                }
                case 2: {
                    c = 58;
                    break;
                }
                case 3: {
                    c = 32;
                    break;
                }
                default: {
                    return;
                }
            }
            if (this._outputTail >= this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail] = c;
            ++this._outputTail;
            return;
        }
        this._verifyPrettyValueWrite(typeMsg, status);
    }

    protected final void _verifyPrettyValueWrite(String typeMsg, int status) throws IOException, JsonGenerationException {
        switch (status) {
            case 1: {
                this._cfgPrettyPrinter.writeArrayValueSeparator(this);
                break;
            }
            case 2: {
                this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
                break;
            }
            case 3: {
                this._cfgPrettyPrinter.writeRootValueSeparator(this);
                break;
            }
            case 0: {
                if (this._writeContext.inArray()) {
                    this._cfgPrettyPrinter.beforeArrayValues(this);
                    break;
                }
                if (!this._writeContext.inObject()) break;
                this._cfgPrettyPrinter.beforeObjectEntries(this);
                break;
            }
            default: {
                this._cantHappen();
            }
        }
    }

    public final void flush() throws IOException {
        this._flushBuffer();
        if (this._writer != null && this.isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
            this._writer.flush();
        }
    }

    public void close() throws IOException {
        super.close();
        if (this._outputBuffer != null && this.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
            while (true) {
                JsonWriteContext ctxt;
                if ((ctxt = this.getOutputContext()).inArray()) {
                    this.writeEndArray();
                    continue;
                }
                if (!ctxt.inObject()) break;
                this.writeEndObject();
            }
        }
        this._flushBuffer();
        if (this._writer != null) {
            if (this._ioContext.isResourceManaged() || this.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
                this._writer.close();
            } else if (this.isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
                this._writer.flush();
            }
        }
        this._releaseBuffers();
    }

    protected void _releaseBuffers() {
        char[] buf = this._outputBuffer;
        if (buf != null) {
            this._outputBuffer = null;
            this._ioContext.releaseConcatBuffer(buf);
        }
    }

    private void _writeString(String text) throws IOException, JsonGenerationException {
        int len = text.length();
        if (len > this._outputEnd) {
            this._writeLongString(text);
            return;
        }
        if (this._outputTail + len > this._outputEnd) {
            this._flushBuffer();
        }
        text.getChars(0, len, this._outputBuffer, this._outputTail);
        if (this._characterEscapes != null) {
            this._writeStringCustom(len);
        } else if (this._maximumNonEscapedChar != 0) {
            this._writeStringASCII(len, this._maximumNonEscapedChar);
        } else {
            this._writeString2(len);
        }
    }

    private void _writeString2(int len) throws IOException, JsonGenerationException {
        int end = this._outputTail + len;
        int[] escCodes = this._outputEscapes;
        int escLen = escCodes.length;
        block0: while (this._outputTail < end) {
            char c;
            while ((c = this._outputBuffer[this._outputTail]) >= escLen || escCodes[c] == 0) {
                if (++this._outputTail < end) continue;
                break block0;
            }
            int flushLen = this._outputTail - this._outputHead;
            if (flushLen > 0) {
                this._writer.write(this._outputBuffer, this._outputHead, flushLen);
            }
            char c2 = this._outputBuffer[this._outputTail++];
            this._prependOrWriteCharacterEscape(c2, escCodes[c2]);
        }
    }

    private void _writeLongString(String text) throws IOException, JsonGenerationException {
        int segmentLen;
        this._flushBuffer();
        int textLen = text.length();
        int offset = 0;
        do {
            int max;
            segmentLen = offset + (max = this._outputEnd) > textLen ? textLen - offset : max;
            text.getChars(offset, offset + segmentLen, this._outputBuffer, 0);
            if (this._characterEscapes != null) {
                this._writeSegmentCustom(segmentLen);
                continue;
            }
            if (this._maximumNonEscapedChar != 0) {
                this._writeSegmentASCII(segmentLen, this._maximumNonEscapedChar);
                continue;
            }
            this._writeSegment(segmentLen);
        } while ((offset += segmentLen) < textLen);
    }

    private final void _writeSegment(int end) throws IOException, JsonGenerationException {
        int ptr;
        int[] escCodes = this._outputEscapes;
        int escLen = escCodes.length;
        int start = ptr = 0;
        while (ptr < end) {
            char c;
            while (((c = this._outputBuffer[ptr]) >= escLen || escCodes[c] == 0) && ++ptr < end) {
            }
            int flushLen = ptr - start;
            if (flushLen > 0) {
                this._writer.write(this._outputBuffer, start, flushLen);
                if (ptr >= end) break;
            }
            start = this._prependOrWriteCharacterEscape(this._outputBuffer, ++ptr, end, c, escCodes[c]);
        }
    }

    private final void _writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        if (this._characterEscapes != null) {
            this._writeStringCustom(text, offset, len);
            return;
        }
        if (this._maximumNonEscapedChar != 0) {
            this._writeStringASCII(text, offset, len, this._maximumNonEscapedChar);
            return;
        }
        len += offset;
        int[] escCodes = this._outputEscapes;
        int escLen = escCodes.length;
        while (offset < len) {
            char c;
            int start = offset;
            while (((c = text[offset]) >= escLen || escCodes[c] == 0) && ++offset < len) {
            }
            int newAmount = offset - start;
            if (newAmount < 32) {
                if (this._outputTail + newAmount > this._outputEnd) {
                    this._flushBuffer();
                }
                if (newAmount > 0) {
                    System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
                    this._outputTail += newAmount;
                }
            } else {
                this._flushBuffer();
                this._writer.write(text, start, newAmount);
            }
            if (offset >= len) break;
            char c2 = text[offset++];
            this._appendCharacterEscape(c2, escCodes[c2]);
        }
    }

    private void _writeStringASCII(int len, int maxNonEscaped) throws IOException, JsonGenerationException {
        int end = this._outputTail + len;
        int[] escCodes = this._outputEscapes;
        int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
        int escCode = 0;
        while (this._outputTail < end) {
            char c;
            block5: {
                do {
                    if ((c = this._outputBuffer[this._outputTail]) < escLimit) {
                        escCode = escCodes[c];
                        if (escCode == 0) continue;
                    } else {
                        if (c <= maxNonEscaped) continue;
                        escCode = -1;
                    }
                    break block5;
                } while (++this._outputTail < end);
                break;
            }
            int flushLen = this._outputTail - this._outputHead;
            if (flushLen > 0) {
                this._writer.write(this._outputBuffer, this._outputHead, flushLen);
            }
            ++this._outputTail;
            this._prependOrWriteCharacterEscape(c, escCode);
        }
    }

    private final void _writeSegmentASCII(int end, int maxNonEscaped) throws IOException, JsonGenerationException {
        int[] escCodes = this._outputEscapes;
        int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
        int ptr = 0;
        int escCode = 0;
        int start = ptr;
        while (ptr < end) {
            char c;
            do {
                if ((c = this._outputBuffer[ptr]) < escLimit) {
                    escCode = escCodes[c];
                    if (escCode == 0) continue;
                    break;
                }
                if (c <= maxNonEscaped) continue;
                escCode = -1;
                break;
            } while (++ptr < end);
            int flushLen = ptr - start;
            if (flushLen > 0) {
                this._writer.write(this._outputBuffer, start, flushLen);
                if (ptr >= end) break;
            }
            start = this._prependOrWriteCharacterEscape(this._outputBuffer, ++ptr, end, c, escCode);
        }
    }

    private final void _writeStringASCII(char[] text, int offset, int len, int maxNonEscaped) throws IOException, JsonGenerationException {
        len += offset;
        int[] escCodes = this._outputEscapes;
        int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
        int escCode = 0;
        while (offset < len) {
            char c;
            int start = offset;
            do {
                if ((c = text[offset]) < escLimit) {
                    escCode = escCodes[c];
                    if (escCode == 0) continue;
                    break;
                }
                if (c <= maxNonEscaped) continue;
                escCode = -1;
                break;
            } while (++offset < len);
            int newAmount = offset - start;
            if (newAmount < 32) {
                if (this._outputTail + newAmount > this._outputEnd) {
                    this._flushBuffer();
                }
                if (newAmount > 0) {
                    System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
                    this._outputTail += newAmount;
                }
            } else {
                this._flushBuffer();
                this._writer.write(text, start, newAmount);
            }
            if (offset >= len) break;
            ++offset;
            this._appendCharacterEscape(c, escCode);
        }
    }

    private void _writeStringCustom(int len) throws IOException, JsonGenerationException {
        int end = this._outputTail + len;
        int[] escCodes = this._outputEscapes;
        int maxNonEscaped = this._maximumNonEscapedChar < 1 ? 65535 : this._maximumNonEscapedChar;
        int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
        int escCode = 0;
        CharacterEscapes customEscapes = this._characterEscapes;
        while (this._outputTail < end) {
            int c;
            block7: {
                do {
                    if ((c = this._outputBuffer[this._outputTail]) < escLimit) {
                        escCode = escCodes[c];
                        if (escCode == 0) continue;
                    } else if (c > maxNonEscaped) {
                        escCode = -1;
                    } else {
                        this._currentEscape = customEscapes.getEscapeSequence(c);
                        if (this._currentEscape == null) continue;
                        escCode = -2;
                    }
                    break block7;
                } while (++this._outputTail < end);
                break;
            }
            int flushLen = this._outputTail - this._outputHead;
            if (flushLen > 0) {
                this._writer.write(this._outputBuffer, this._outputHead, flushLen);
            }
            ++this._outputTail;
            this._prependOrWriteCharacterEscape((char)c, escCode);
        }
    }

    private final void _writeSegmentCustom(int end) throws IOException, JsonGenerationException {
        int[] escCodes = this._outputEscapes;
        int maxNonEscaped = this._maximumNonEscapedChar < 1 ? 65535 : this._maximumNonEscapedChar;
        int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
        CharacterEscapes customEscapes = this._characterEscapes;
        int ptr = 0;
        int escCode = 0;
        int start = ptr;
        while (ptr < end) {
            int c;
            do {
                if ((c = this._outputBuffer[ptr]) < escLimit) {
                    escCode = escCodes[c];
                    if (escCode == 0) continue;
                    break;
                }
                if (c > maxNonEscaped) {
                    escCode = -1;
                    break;
                }
                this._currentEscape = customEscapes.getEscapeSequence(c);
                if (this._currentEscape == null) continue;
                escCode = -2;
                break;
            } while (++ptr < end);
            int flushLen = ptr - start;
            if (flushLen > 0) {
                this._writer.write(this._outputBuffer, start, flushLen);
                if (ptr >= end) break;
            }
            start = this._prependOrWriteCharacterEscape(this._outputBuffer, ++ptr, end, (char)c, escCode);
        }
    }

    private final void _writeStringCustom(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        len += offset;
        int[] escCodes = this._outputEscapes;
        int maxNonEscaped = this._maximumNonEscapedChar < 1 ? 65535 : this._maximumNonEscapedChar;
        int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
        CharacterEscapes customEscapes = this._characterEscapes;
        int escCode = 0;
        while (offset < len) {
            int c;
            int start = offset;
            do {
                if ((c = text[offset]) < escLimit) {
                    escCode = escCodes[c];
                    if (escCode == 0) continue;
                    break;
                }
                if (c > maxNonEscaped) {
                    escCode = -1;
                    break;
                }
                this._currentEscape = customEscapes.getEscapeSequence(c);
                if (this._currentEscape == null) continue;
                escCode = -2;
                break;
            } while (++offset < len);
            int newAmount = offset - start;
            if (newAmount < 32) {
                if (this._outputTail + newAmount > this._outputEnd) {
                    this._flushBuffer();
                }
                if (newAmount > 0) {
                    System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
                    this._outputTail += newAmount;
                }
            } else {
                this._flushBuffer();
                this._writer.write(text, start, newAmount);
            }
            if (offset >= len) break;
            ++offset;
            this._appendCharacterEscape((char)c, escCode);
        }
    }

    protected void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd) throws IOException, JsonGenerationException {
        int safeInputEnd = inputEnd - 3;
        int safeOutputEnd = this._outputEnd - 6;
        int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
        while (inputPtr <= safeInputEnd) {
            if (this._outputTail > safeOutputEnd) {
                this._flushBuffer();
            }
            int b24 = input[inputPtr++] << 8;
            b24 |= input[inputPtr++] & 0xFF;
            b24 = b24 << 8 | input[inputPtr++] & 0xFF;
            this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
            if (--chunksBeforeLF > 0) continue;
            this._outputBuffer[this._outputTail++] = 92;
            this._outputBuffer[this._outputTail++] = 110;
            chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
        }
        int inputLeft = inputEnd - inputPtr;
        if (inputLeft > 0) {
            if (this._outputTail > safeOutputEnd) {
                this._flushBuffer();
            }
            int b24 = input[inputPtr++] << 16;
            if (inputLeft == 2) {
                b24 |= (input[inputPtr++] & 0xFF) << 8;
            }
            this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
        }
    }

    private final void _writeNull() throws IOException {
        if (this._outputTail + 4 >= this._outputEnd) {
            this._flushBuffer();
        }
        int ptr = this._outputTail;
        char[] buf = this._outputBuffer;
        buf[ptr] = 110;
        buf[++ptr] = 117;
        buf[++ptr] = 108;
        buf[++ptr] = 108;
        this._outputTail = ptr + 1;
    }

    private final void _prependOrWriteCharacterEscape(char ch, int escCode) throws IOException, JsonGenerationException {
        String escape;
        if (escCode >= 0) {
            if (this._outputTail >= 2) {
                int ptr;
                this._outputHead = ptr = this._outputTail - 2;
                this._outputBuffer[ptr++] = 92;
                this._outputBuffer[ptr] = (char)escCode;
                return;
            }
            char[] buf = this._entityBuffer;
            if (buf == null) {
                buf = this._allocateEntityBuffer();
            }
            this._outputHead = this._outputTail;
            buf[1] = (char)escCode;
            this._writer.write(buf, 0, 2);
            return;
        }
        if (escCode != -2) {
            if (this._outputTail >= 6) {
                int ptr;
                char[] buf = this._outputBuffer;
                this._outputHead = ptr = this._outputTail - 6;
                buf[ptr] = 92;
                buf[++ptr] = 117;
                if (ch > '\u00ff') {
                    int hi = ch >> 8 & 0xFF;
                    buf[++ptr] = HEX_CHARS[hi >> 4];
                    buf[++ptr] = HEX_CHARS[hi & 0xF];
                    ch = (char)(ch & 0xFF);
                } else {
                    buf[++ptr] = 48;
                    buf[++ptr] = 48;
                }
                buf[++ptr] = HEX_CHARS[ch >> 4];
                buf[++ptr] = HEX_CHARS[ch & 0xF];
                return;
            }
            char[] buf = this._entityBuffer;
            if (buf == null) {
                buf = this._allocateEntityBuffer();
            }
            this._outputHead = this._outputTail;
            if (ch > '\u00ff') {
                int hi = ch >> 8 & 0xFF;
                int lo = ch & 0xFF;
                buf[10] = HEX_CHARS[hi >> 4];
                buf[11] = HEX_CHARS[hi & 0xF];
                buf[12] = HEX_CHARS[lo >> 4];
                buf[13] = HEX_CHARS[lo & 0xF];
                this._writer.write(buf, 8, 6);
            } else {
                buf[6] = HEX_CHARS[ch >> 4];
                buf[7] = HEX_CHARS[ch & 0xF];
                this._writer.write(buf, 2, 6);
            }
            return;
        }
        if (this._currentEscape == null) {
            escape = this._characterEscapes.getEscapeSequence(ch).getValue();
        } else {
            escape = this._currentEscape.getValue();
            this._currentEscape = null;
        }
        int len = escape.length();
        if (this._outputTail >= len) {
            int ptr;
            this._outputHead = ptr = this._outputTail - len;
            escape.getChars(0, len, this._outputBuffer, ptr);
            return;
        }
        this._outputHead = this._outputTail;
        this._writer.write(escape);
    }

    private final int _prependOrWriteCharacterEscape(char[] buffer, int ptr, int end, char ch, int escCode) throws IOException, JsonGenerationException {
        String escape;
        if (escCode >= 0) {
            if (ptr > 1 && ptr < end) {
                buffer[ptr -= 2] = 92;
                buffer[ptr + 1] = (char)escCode;
            } else {
                char[] ent = this._entityBuffer;
                if (ent == null) {
                    ent = this._allocateEntityBuffer();
                }
                ent[1] = (char)escCode;
                this._writer.write(ent, 0, 2);
            }
            return ptr;
        }
        if (escCode != -2) {
            if (ptr > 5 && ptr < end) {
                ptr -= 6;
                buffer[ptr++] = 92;
                buffer[ptr++] = 117;
                if (ch > '\u00ff') {
                    int hi = ch >> 8 & 0xFF;
                    buffer[ptr++] = HEX_CHARS[hi >> 4];
                    buffer[ptr++] = HEX_CHARS[hi & 0xF];
                    ch = (char)(ch & 0xFF);
                } else {
                    buffer[ptr++] = 48;
                    buffer[ptr++] = 48;
                }
                buffer[ptr++] = HEX_CHARS[ch >> 4];
                buffer[ptr] = HEX_CHARS[ch & 0xF];
                ptr -= 5;
            } else {
                char[] ent = this._entityBuffer;
                if (ent == null) {
                    ent = this._allocateEntityBuffer();
                }
                this._outputHead = this._outputTail;
                if (ch > '\u00ff') {
                    int hi = ch >> 8 & 0xFF;
                    int lo = ch & 0xFF;
                    ent[10] = HEX_CHARS[hi >> 4];
                    ent[11] = HEX_CHARS[hi & 0xF];
                    ent[12] = HEX_CHARS[lo >> 4];
                    ent[13] = HEX_CHARS[lo & 0xF];
                    this._writer.write(ent, 8, 6);
                } else {
                    ent[6] = HEX_CHARS[ch >> 4];
                    ent[7] = HEX_CHARS[ch & 0xF];
                    this._writer.write(ent, 2, 6);
                }
            }
            return ptr;
        }
        if (this._currentEscape == null) {
            escape = this._characterEscapes.getEscapeSequence(ch).getValue();
        } else {
            escape = this._currentEscape.getValue();
            this._currentEscape = null;
        }
        int len = escape.length();
        if (ptr >= len && ptr < end) {
            escape.getChars(0, len, buffer, ptr -= len);
        } else {
            this._writer.write(escape);
        }
        return ptr;
    }

    private final void _appendCharacterEscape(char ch, int escCode) throws IOException, JsonGenerationException {
        String escape;
        if (escCode >= 0) {
            if (this._outputTail + 2 > this._outputEnd) {
                this._flushBuffer();
            }
            this._outputBuffer[this._outputTail++] = 92;
            this._outputBuffer[this._outputTail++] = (char)escCode;
            return;
        }
        if (escCode != -2) {
            if (this._outputTail + 2 > this._outputEnd) {
                this._flushBuffer();
            }
            int ptr = this._outputTail;
            char[] buf = this._outputBuffer;
            buf[ptr++] = 92;
            buf[ptr++] = 117;
            if (ch > '\u00ff') {
                int hi = ch >> 8 & 0xFF;
                buf[ptr++] = HEX_CHARS[hi >> 4];
                buf[ptr++] = HEX_CHARS[hi & 0xF];
                ch = (char)(ch & 0xFF);
            } else {
                buf[ptr++] = 48;
                buf[ptr++] = 48;
            }
            buf[ptr++] = HEX_CHARS[ch >> 4];
            buf[ptr] = HEX_CHARS[ch & 0xF];
            this._outputTail = ptr;
            return;
        }
        if (this._currentEscape == null) {
            escape = this._characterEscapes.getEscapeSequence(ch).getValue();
        } else {
            escape = this._currentEscape.getValue();
            this._currentEscape = null;
        }
        int len = escape.length();
        if (this._outputTail + len > this._outputEnd) {
            this._flushBuffer();
            if (len > this._outputEnd) {
                this._writer.write(escape);
                return;
            }
        }
        escape.getChars(0, len, this._outputBuffer, this._outputTail);
        this._outputTail += len;
    }

    private char[] _allocateEntityBuffer() {
        char[] buf = new char[14];
        buf[0] = 92;
        buf[2] = 92;
        buf[3] = 117;
        buf[4] = 48;
        buf[5] = 48;
        buf[8] = 92;
        buf[9] = 117;
        this._entityBuffer = buf;
        return buf;
    }

    protected final void _flushBuffer() throws IOException {
        int len = this._outputTail - this._outputHead;
        if (len > 0) {
            int offset = this._outputHead;
            this._outputHead = 0;
            this._outputTail = 0;
            this._writer.write(this._outputBuffer, offset, len);
        }
    }
}

