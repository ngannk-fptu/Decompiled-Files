/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.sw.XmlWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import org.codehaus.stax2.validation.XMLValidator;

public abstract class EncodingXmlWriter
extends XmlWriter {
    static final int DEFAULT_BUFFER_SIZE = 4000;
    static final byte BYTE_SPACE = 32;
    static final byte BYTE_COLON = 58;
    static final byte BYTE_SEMICOLON = 59;
    static final byte BYTE_LBRACKET = 91;
    static final byte BYTE_RBRACKET = 93;
    static final byte BYTE_QMARK = 63;
    static final byte BYTE_EQ = 61;
    static final byte BYTE_SLASH = 47;
    static final byte BYTE_HASH = 35;
    static final byte BYTE_HYPHEN = 45;
    static final byte BYTE_LT = 60;
    static final byte BYTE_GT = 62;
    static final byte BYTE_AMP = 38;
    static final byte BYTE_QUOT = 34;
    static final byte BYTE_APOS = 39;
    static final byte BYTE_A = 97;
    static final byte BYTE_G = 103;
    static final byte BYTE_L = 108;
    static final byte BYTE_M = 109;
    static final byte BYTE_O = 111;
    static final byte BYTE_P = 112;
    static final byte BYTE_Q = 113;
    static final byte BYTE_S = 115;
    static final byte BYTE_T = 116;
    static final byte BYTE_U = 117;
    static final byte BYTE_X = 120;
    private final OutputStream mOut;
    protected byte[] mOutputBuffer;
    protected int mOutputPtr;
    protected int mSurrogate = 0;

    public EncodingXmlWriter(OutputStream out, WriterConfig cfg, String encoding, boolean autoclose) throws IOException {
        super(cfg, encoding, autoclose);
        this.mOut = out;
        this.mOutputBuffer = cfg.allocFullBBuffer(4000);
        this.mOutputPtr = 0;
    }

    @Override
    protected int getOutputPtr() {
        return this.mOutputPtr;
    }

    @Override
    protected final OutputStream getOutputStream() {
        return this.mOut;
    }

    @Override
    protected final Writer getWriter() {
        return null;
    }

    @Override
    public void close(boolean forceRealClose) throws IOException {
        this.flush();
        byte[] buf = this.mOutputBuffer;
        if (buf != null) {
            this.mOutputBuffer = null;
            this.mConfig.freeFullBBuffer(buf);
        }
        if (forceRealClose || this.mAutoCloseOutput) {
            this.mOut.close();
        }
    }

    @Override
    public final void flush() throws IOException {
        this.flushBuffer();
        this.mOut.flush();
    }

    @Override
    public abstract void writeRaw(char[] var1, int var2, int var3) throws IOException;

    @Override
    public abstract void writeRaw(String var1, int var2, int var3) throws IOException;

    @Override
    public final void writeCDataStart() throws IOException {
        this.writeAscii("<![CDATA[");
    }

    @Override
    public final void writeCDataEnd() throws IOException {
        this.writeAscii("]]>");
    }

    @Override
    public final void writeCommentStart() throws IOException {
        this.writeAscii("<!--");
    }

    @Override
    public final void writeCommentEnd() throws IOException {
        this.writeAscii("-->");
    }

    @Override
    public final void writePIStart(String target, boolean addSpace) throws IOException {
        this.writeAscii((byte)60, (byte)63);
        this.writeRaw(target);
        if (addSpace) {
            this.writeAscii((byte)32);
        }
    }

    @Override
    public final void writePIEnd() throws IOException {
        this.writeAscii((byte)63, (byte)62);
    }

    @Override
    public int writeCData(String data) throws IOException {
        this.writeAscii("<![CDATA[");
        int ix = this.writeCDataContent(data);
        if (ix >= 0) {
            return ix;
        }
        this.writeAscii("]]>");
        return -1;
    }

    @Override
    public int writeCData(char[] cbuf, int offset, int len) throws IOException {
        this.writeAscii("<![CDATA[");
        int ix = this.writeCDataContent(cbuf, offset, len);
        if (ix >= 0) {
            return ix;
        }
        this.writeAscii("]]>");
        return -1;
    }

    @Override
    public final void writeCharacters(String data) throws IOException {
        if (this.mTextWriter != null) {
            this.mTextWriter.write(data);
        } else {
            this.writeTextContent(data);
        }
    }

    @Override
    public final void writeCharacters(char[] cbuf, int offset, int len) throws IOException {
        if (this.mTextWriter != null) {
            this.mTextWriter.write(cbuf, offset, len);
        } else {
            this.writeTextContent(cbuf, offset, len);
        }
    }

    @Override
    public int writeComment(String data) throws IOException {
        this.writeAscii("<!--");
        int ix = this.writeCommentContent(data);
        if (ix >= 0) {
            return ix;
        }
        this.writeAscii("-->");
        return -1;
    }

    @Override
    public void writeDTD(String data) throws IOException {
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        this.writeRaw(data, 0, data.length());
    }

    @Override
    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws IOException, XMLStreamException {
        this.writeAscii("<!DOCTYPE ");
        this.writeAscii(rootName);
        if (systemId != null) {
            if (publicId != null) {
                this.writeAscii(" PUBLIC \"");
                this.writeRaw(publicId, 0, publicId.length());
                this.writeAscii("\" \"");
            } else {
                this.writeAscii(" SYSTEM \"");
            }
            this.writeRaw(systemId, 0, systemId.length());
            this.writeAscii((byte)34);
        }
        if (internalSubset != null && internalSubset.length() > 0) {
            this.writeAscii((byte)32, (byte)91);
            this.writeRaw(internalSubset, 0, internalSubset.length());
            this.writeAscii((byte)93);
        }
        this.writeAscii((byte)62);
    }

    @Override
    public void writeEntityReference(String name) throws IOException, XMLStreamException {
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        this.writeAscii((byte)38);
        this.writeName(name);
        this.writeAscii((byte)59);
    }

    @Override
    public void writeXmlDeclaration(String version, String encoding, String standalone) throws IOException {
        byte byQuote = this.mUseDoubleQuotesInXmlDecl ? (byte)34 : 39;
        this.writeAscii("<?xml version=");
        this.writeAscii(byQuote);
        this.writeAscii(version);
        this.writeAscii(byQuote);
        if (encoding != null && encoding.length() > 0) {
            this.writeAscii(" encoding=");
            this.writeAscii(byQuote);
            this.writeRaw(encoding, 0, encoding.length());
            this.writeAscii(byQuote);
        }
        if (standalone != null) {
            this.writeAscii(" standalone=");
            this.writeAscii(byQuote);
            this.writeAscii(standalone);
            this.writeAscii(byQuote);
        }
        this.writeAscii((byte)63, (byte)62);
    }

    @Override
    public int writePI(String target, String data) throws IOException, XMLStreamException {
        this.writeAscii((byte)60, (byte)63);
        this.writeName(target);
        if (data != null && data.length() > 0) {
            this.writeAscii((byte)32);
            int ix = this.writePIData(data);
            if (ix >= 0) {
                return ix;
            }
        }
        this.writeAscii((byte)63, (byte)62);
        return -1;
    }

    @Override
    public void writeStartTagStart(String localName) throws IOException, XMLStreamException {
        this.writeAscii((byte)60);
        this.writeName(localName);
    }

    @Override
    public void writeStartTagStart(String prefix, String localName) throws IOException, XMLStreamException {
        if (prefix == null || prefix.length() == 0) {
            this.writeStartTagStart(localName);
            return;
        }
        this.writeAscii((byte)60);
        this.writeName(prefix);
        this.writeAscii((byte)58);
        this.writeName(localName);
    }

    @Override
    public void writeStartTagEnd() throws IOException {
        this.writeAscii((byte)62);
    }

    @Override
    public void writeStartTagEmptyEnd() throws IOException {
        if (this.mAddSpaceAfterEmptyElem) {
            this.writeAscii(" />");
        } else {
            this.writeAscii((byte)47, (byte)62);
        }
    }

    @Override
    public void writeEndTag(String localName) throws IOException {
        this.writeAscii((byte)60, (byte)47);
        this.writeNameUnchecked(localName);
        this.writeAscii((byte)62);
    }

    @Override
    public void writeEndTag(String prefix, String localName) throws IOException {
        this.writeAscii((byte)60, (byte)47);
        if (prefix != null && prefix.length() > 0) {
            this.writeNameUnchecked(prefix);
            this.writeAscii((byte)58);
        }
        this.writeNameUnchecked(localName);
        this.writeAscii((byte)62);
    }

    @Override
    public void writeAttribute(String localName, String value) throws IOException, XMLStreamException {
        this.writeAscii((byte)32);
        this.writeName(localName);
        this.writeAscii((byte)61, (byte)34);
        int len = value.length();
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, 0, len);
            } else {
                this.writeAttrValue(value);
            }
        }
        this.writeAscii((byte)34);
    }

    @Override
    public void writeAttribute(String localName, char[] value, int offset, int len) throws IOException, XMLStreamException {
        this.writeAscii((byte)32);
        this.writeName(localName);
        this.writeAscii((byte)61, (byte)34);
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, offset, len);
            } else {
                this.writeAttrValue(value, offset, len);
            }
        }
        this.writeAscii((byte)34);
    }

    @Override
    public void writeAttribute(String prefix, String localName, String value) throws IOException, XMLStreamException {
        this.writeAscii((byte)32);
        this.writeName(prefix);
        this.writeAscii((byte)58);
        this.writeName(localName);
        this.writeAscii((byte)61, (byte)34);
        int len = value.length();
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, 0, len);
            } else {
                this.writeAttrValue(value);
            }
        }
        this.writeAscii((byte)34);
    }

    @Override
    public void writeAttribute(String prefix, String localName, char[] value, int offset, int len) throws IOException, XMLStreamException {
        this.writeAscii((byte)32);
        this.writeName(prefix);
        this.writeAscii((byte)58);
        this.writeName(localName);
        this.writeAscii((byte)61, (byte)34);
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, offset, len);
            } else {
                this.writeAttrValue(value, offset, len);
            }
        }
        this.writeAscii((byte)34);
    }

    @Override
    public final void writeTypedElement(AsciiValueEncoder enc) throws IOException {
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        if (enc.bufferNeedsFlush(this.mOutputBuffer.length - this.mOutputPtr)) {
            this.flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBuffer.length);
            if (enc.isCompleted()) break;
            this.flush();
        }
    }

    @Override
    public final void writeTypedElement(AsciiValueEncoder enc, XMLValidator validator, char[] copyBuffer) throws IOException, XMLStreamException {
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        int copyBufferLen = copyBuffer.length;
        do {
            int ptr = enc.encodeMore(copyBuffer, 0, copyBufferLen);
            validator.validateText(copyBuffer, 0, ptr, false);
            this.writeRawAscii(copyBuffer, 0, ptr);
        } while (!enc.isCompleted());
    }

    @Override
    public void writeTypedAttribute(String localName, AsciiValueEncoder enc) throws IOException, XMLStreamException {
        this.writeAscii((byte)32);
        this.writeName(localName);
        this.writeAscii((byte)61, (byte)34);
        if (enc.bufferNeedsFlush(this.mOutputBuffer.length - this.mOutputPtr)) {
            this.flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBuffer.length);
            if (enc.isCompleted()) break;
            this.flush();
        }
        this.writeAscii((byte)34);
    }

    @Override
    public void writeTypedAttribute(String prefix, String localName, AsciiValueEncoder enc) throws IOException, XMLStreamException {
        System.err.println("DEBUG: write typed attr/0 '" + localName + "'");
        this.writeAscii((byte)32);
        this.writeName(prefix);
        this.writeAscii((byte)58);
        this.writeName(localName);
        this.writeAscii((byte)61, (byte)34);
        if (enc.bufferNeedsFlush(this.mOutputBuffer.length - this.mOutputPtr)) {
            this.flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBuffer.length);
            if (enc.isCompleted()) break;
            this.flush();
        }
        this.writeAscii((byte)34);
    }

    @Override
    public void writeTypedAttribute(String prefix, String localName, String nsURI, AsciiValueEncoder enc, XMLValidator validator, char[] copyBuffer) throws IOException, XMLStreamException {
        boolean hasPrefix;
        boolean bl = hasPrefix = prefix != null && prefix.length() > 0;
        if (nsURI == null) {
            nsURI = "";
        }
        System.err.println("DEBUG: write typed attr/1 '" + localName + "', vld == " + validator);
        this.writeAscii((byte)32);
        if (hasPrefix) {
            this.writeName(prefix);
            this.writeAscii((byte)58);
        }
        this.writeName(localName);
        this.writeAscii((byte)61, (byte)34);
        int copyBufferLen = copyBuffer.length;
        int last = enc.encodeMore(copyBuffer, 0, copyBufferLen);
        this.writeRawAscii(copyBuffer, 0, last);
        if (enc.isCompleted()) {
            validator.validateAttribute(localName, nsURI, prefix, copyBuffer, 0, last);
            return;
        }
        StringBuilder sb = new StringBuilder(copyBufferLen << 1);
        sb.append(copyBuffer, 0, last);
        do {
            last = enc.encodeMore(copyBuffer, 0, copyBufferLen);
            this.writeRawAscii(copyBuffer, 0, last);
            sb.append(copyBuffer, 0, last);
        } while (!enc.isCompleted());
        this.writeAscii((byte)34);
        String valueStr = sb.toString();
        validator.validateAttribute(localName, nsURI, prefix, valueStr);
    }

    protected final void flushBuffer() throws IOException {
        if (this.mOutputPtr > 0 && this.mOutputBuffer != null) {
            int ptr = this.mOutputPtr;
            this.mOutputPtr = 0;
            this.mOut.write(this.mOutputBuffer, 0, ptr);
        }
    }

    protected final void writeAscii(byte b) throws IOException {
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        if (this.mOutputPtr >= this.mOutputBuffer.length) {
            this.flushBuffer();
        }
        this.mOutputBuffer[this.mOutputPtr++] = b;
    }

    protected final void writeAscii(byte b1, byte b2) throws IOException {
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        if (this.mOutputPtr + 1 >= this.mOutputBuffer.length) {
            this.flushBuffer();
        }
        this.mOutputBuffer[this.mOutputPtr++] = b1;
        this.mOutputBuffer[this.mOutputPtr++] = b2;
    }

    protected final void writeAscii(String str) throws IOException {
        byte[] buf;
        int len;
        int ptr;
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        if ((ptr = this.mOutputPtr) + (len = str.length()) >= (buf = this.mOutputBuffer).length) {
            if (len > buf.length) {
                this.writeRaw(str, 0, len);
                return;
            }
            this.flushBuffer();
            ptr = this.mOutputPtr;
        }
        this.mOutputPtr += len;
        for (int i = 0; i < len; ++i) {
            buf[ptr++] = (byte)str.charAt(i);
        }
    }

    @Override
    public final void writeRawAscii(char[] buf, int offset, int len) throws IOException {
        byte[] dst;
        int ptr;
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        if ((ptr = this.mOutputPtr) + len >= (dst = this.mOutputBuffer).length) {
            if (len > dst.length) {
                this.writeRaw(buf, offset, len);
                return;
            }
            this.flushBuffer();
            ptr = this.mOutputPtr;
        }
        this.mOutputPtr += len;
        for (int i = 0; i < len; ++i) {
            dst[ptr + i] = (byte)buf[offset + i];
        }
    }

    protected final int writeAsEntity(int c) throws IOException {
        int ptr = this.mOutputPtr;
        byte[] buf = this.mOutputBuffer;
        if (ptr + 10 >= buf.length) {
            this.flushBuffer();
            ptr = this.mOutputPtr;
        }
        buf[ptr++] = 38;
        if (c < 256) {
            if (c == 38) {
                buf[ptr++] = 97;
                buf[ptr++] = 109;
                buf[ptr++] = 112;
            } else if (c == 60) {
                buf[ptr++] = 108;
                buf[ptr++] = 116;
            } else if (c == 62) {
                buf[ptr++] = 103;
                buf[ptr++] = 116;
            } else if (c == 39) {
                buf[ptr++] = 97;
                buf[ptr++] = 112;
                buf[ptr++] = 111;
                buf[ptr++] = 115;
            } else if (c == 34) {
                buf[ptr++] = 113;
                buf[ptr++] = 117;
                buf[ptr++] = 111;
                buf[ptr++] = 116;
            } else {
                buf[ptr++] = 35;
                buf[ptr++] = 120;
                if (c >= 16) {
                    int digit = c >> 4;
                    buf[ptr++] = (byte)(digit < 10 ? 48 + digit : 87 + digit);
                    c &= 0xF;
                }
                buf[ptr++] = (byte)(c < 10 ? 48 + c : 87 + c);
            }
        } else {
            buf[ptr++] = 35;
            buf[ptr++] = 120;
            int shift = 20;
            int origPtr = ptr;
            do {
                int digit;
                if ((digit = c >> shift & 0xF) <= 0 && ptr == origPtr) continue;
                buf[ptr++] = (byte)(digit < 10 ? 48 + digit : 87 + digit);
            } while ((shift -= 4) > 0);
            buf[ptr++] = (byte)((c &= 0xF) < 10 ? 48 + c : 87 + c);
        }
        buf[ptr++] = 59;
        this.mOutputPtr = ptr;
        return ptr;
    }

    protected final void writeName(String name) throws IOException, XMLStreamException {
        if (this.mCheckNames) {
            this.verifyNameValidity(name, this.mNsAware);
        }
        this.writeRaw(name, 0, name.length());
    }

    protected final void writeNameUnchecked(String name) throws IOException {
        this.writeRaw(name, 0, name.length());
    }

    protected final int calcSurrogate(int secondSurr) throws IOException {
        int ch;
        int firstSurr = this.mSurrogate;
        this.mSurrogate = 0;
        if (firstSurr < 55296 || firstSurr > 56319) {
            this.throwUnpairedSurrogate(firstSurr);
        }
        if (secondSurr < 56320 || secondSurr > 57343) {
            this.throwUnpairedSurrogate(secondSurr);
        }
        if ((ch = 65536 + (firstSurr - 55296 << 10) + (secondSurr - 56320)) > 0x10FFFF) {
            throw new IOException("Illegal surrogate character pair, resulting code 0x" + Integer.toHexString(ch) + " above legal XML character range");
        }
        return ch;
    }

    protected final void throwUnpairedSurrogate() throws IOException {
        int surr = this.mSurrogate;
        this.mSurrogate = 0;
        this.throwUnpairedSurrogate(surr);
    }

    protected final void throwUnpairedSurrogate(int code) throws IOException {
        this.flush();
        throw new IOException("Unpaired surrogate character (0x" + Integer.toHexString(code) + ")");
    }

    protected abstract void writeAttrValue(String var1) throws IOException;

    protected abstract void writeAttrValue(char[] var1, int var2, int var3) throws IOException;

    protected abstract int writeCDataContent(String var1) throws IOException;

    protected abstract int writeCDataContent(char[] var1, int var2, int var3) throws IOException;

    protected abstract int writeCommentContent(String var1) throws IOException;

    protected abstract int writePIData(String var1) throws IOException, XMLStreamException;

    protected abstract void writeTextContent(String var1) throws IOException;

    protected abstract void writeTextContent(char[] var1, int var2, int var3) throws IOException;
}

