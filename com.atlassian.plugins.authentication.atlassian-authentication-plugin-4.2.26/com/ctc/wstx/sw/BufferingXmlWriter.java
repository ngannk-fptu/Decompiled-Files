/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.io.CharsetNames;
import com.ctc.wstx.io.CompletelyCloseable;
import com.ctc.wstx.sw.XmlWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import org.codehaus.stax2.validation.XMLValidator;

public final class BufferingXmlWriter
extends XmlWriter
implements XMLStreamConstants {
    static final int DEFAULT_BUFFER_SIZE = 1000;
    static final int DEFAULT_SMALL_SIZE = 256;
    protected static final int HIGHEST_ENCODABLE_ATTR_CHAR = 60;
    protected static final int HIGHEST_ENCODABLE_TEXT_CHAR = 62;
    protected static final int[] QUOTABLE_TEXT_CHARS;
    protected final Writer mOut;
    protected char[] mOutputBuffer;
    protected final int mSmallWriteSize;
    protected int mOutputPtr;
    protected int mOutputBufLen;
    protected final OutputStream mUnderlyingStream;
    private final int mEncHighChar;
    final char mEncQuoteChar;
    final String mEncQuoteEntity;

    public BufferingXmlWriter(Writer out, WriterConfig cfg, String enc, boolean autoclose, OutputStream outs, int bitsize) throws IOException {
        super(cfg, enc, autoclose);
        this.mOut = out;
        this.mOutputBuffer = cfg.allocFullCBuffer(1000);
        this.mOutputBufLen = this.mOutputBuffer.length;
        this.mSmallWriteSize = 256;
        this.mOutputPtr = 0;
        this.mUnderlyingStream = outs;
        this.mEncQuoteChar = (char)34;
        this.mEncQuoteEntity = "&quot;";
        if (bitsize < 1) {
            bitsize = BufferingXmlWriter.guessEncodingBitSize(enc);
        }
        this.mEncHighChar = bitsize < 16 ? 1 << bitsize : 65534;
    }

    @Override
    protected int getOutputPtr() {
        return this.mOutputPtr;
    }

    @Override
    protected final OutputStream getOutputStream() {
        return this.mUnderlyingStream;
    }

    @Override
    protected final Writer getWriter() {
        return this.mOut;
    }

    @Override
    public void close(boolean forceRealClose) throws IOException {
        this.flush();
        this.mTextWriter = null;
        this.mAttrValueWriter = null;
        char[] buf = this.mOutputBuffer;
        if (buf != null) {
            this.mOutputBuffer = null;
            this.mConfig.freeFullCBuffer(buf);
        }
        if (forceRealClose || this.mAutoCloseOutput) {
            if (this.mOut instanceof CompletelyCloseable) {
                ((CompletelyCloseable)((Object)this.mOut)).closeCompletely();
            } else {
                this.mOut.close();
            }
        }
    }

    @Override
    public final void flush() throws IOException {
        this.flushBuffer();
        this.mOut.flush();
    }

    @Override
    public void writeRaw(char[] cbuf, int offset, int len) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (len < this.mSmallWriteSize) {
            if (this.mOutputPtr + len > this.mOutputBufLen) {
                this.flushBuffer();
            }
            System.arraycopy(cbuf, offset, this.mOutputBuffer, this.mOutputPtr, len);
            this.mOutputPtr += len;
            return;
        }
        int ptr = this.mOutputPtr;
        if (ptr > 0) {
            if (ptr < this.mSmallWriteSize) {
                int needed = this.mSmallWriteSize - ptr;
                System.arraycopy(cbuf, offset, this.mOutputBuffer, ptr, needed);
                this.mOutputPtr = ptr + needed;
                len -= needed;
                offset += needed;
            }
            this.flushBuffer();
        }
        this.mOut.write(cbuf, offset, len);
    }

    @Override
    public final void writeRawAscii(char[] cbuf, int offset, int len) throws IOException {
        this.writeRaw(cbuf, offset, len);
    }

    @Override
    public void writeRaw(String str) throws IOException {
        if (this.mOut == null) {
            return;
        }
        int len = str.length();
        if (len < this.mSmallWriteSize) {
            if (this.mOutputPtr + len >= this.mOutputBufLen) {
                this.flushBuffer();
            }
            str.getChars(0, len, this.mOutputBuffer, this.mOutputPtr);
            this.mOutputPtr += len;
            return;
        }
        this.writeRaw(str, 0, len);
    }

    @Override
    public void writeRaw(String str, int offset, int len) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (len < this.mSmallWriteSize) {
            if (this.mOutputPtr + len >= this.mOutputBufLen) {
                this.flushBuffer();
            }
            str.getChars(offset, offset + len, this.mOutputBuffer, this.mOutputPtr);
            this.mOutputPtr += len;
            return;
        }
        int ptr = this.mOutputPtr;
        if (ptr > 0) {
            if (ptr < this.mSmallWriteSize) {
                int needed = this.mSmallWriteSize - ptr;
                str.getChars(offset, offset + needed, this.mOutputBuffer, ptr);
                this.mOutputPtr = ptr + needed;
                len -= needed;
                offset += needed;
            }
            this.flushBuffer();
        }
        this.mOut.write(str, offset, len);
    }

    @Override
    public final void writeCDataStart() throws IOException {
        this.fastWriteRaw("<![CDATA[");
    }

    @Override
    public final void writeCDataEnd() throws IOException {
        this.fastWriteRaw("]]>");
    }

    @Override
    public final void writeCommentStart() throws IOException {
        this.fastWriteRaw("<!--");
    }

    @Override
    public final void writeCommentEnd() throws IOException {
        this.fastWriteRaw("-->");
    }

    @Override
    public final void writePIStart(String target, boolean addSpace) throws IOException {
        this.fastWriteRaw('<', '?');
        this.fastWriteRaw(target);
        if (addSpace) {
            this.fastWriteRaw(' ');
        }
    }

    @Override
    public final void writePIEnd() throws IOException {
        this.fastWriteRaw('?', '>');
    }

    @Override
    public int writeCData(String data) throws IOException {
        int ix;
        if (this.mCheckContent && (ix = this.verifyCDataContent(data)) >= 0) {
            if (!this.mFixContent) {
                return ix;
            }
            this.writeSegmentedCData(data, ix);
            return -1;
        }
        this.fastWriteRaw("<![CDATA[");
        this.writeRaw(data, 0, data.length());
        this.fastWriteRaw("]]>");
        return -1;
    }

    @Override
    public int writeCData(char[] cbuf, int offset, int len) throws IOException {
        int ix;
        if (this.mCheckContent && (ix = this.verifyCDataContent(cbuf, offset, len)) >= 0) {
            if (!this.mFixContent) {
                return ix;
            }
            this.writeSegmentedCData(cbuf, offset, len, ix);
            return -1;
        }
        this.fastWriteRaw("<![CDATA[");
        this.writeRaw(cbuf, offset, len);
        this.fastWriteRaw("]]>");
        return -1;
    }

    @Override
    public void writeCharacters(String text) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mTextWriter != null) {
            this.mTextWriter.write(text);
            return;
        }
        int inPtr = 0;
        int len = text.length();
        int[] QC = QUOTABLE_TEXT_CHARS;
        int highChar = this.mEncHighChar;
        int MAXQC = Math.min(QC.length, highChar);
        block0: while (true) {
            String ent = null;
            while (inPtr < len) {
                block14: {
                    char c;
                    block11: {
                        block10: {
                            block17: {
                                block16: {
                                    block15: {
                                        block12: {
                                            block13: {
                                                if ((c = text.charAt(inPtr++)) >= MAXQC) break block10;
                                                if (QC[c] == 0) break block11;
                                                if (c >= ' ') break block12;
                                                if (c == '\n' || c == '\t') break block11;
                                                if (c != '\r') break block13;
                                                if (!this.mEscapeCR) break block11;
                                                break block14;
                                            }
                                            if (this.mXml11 && c != '\u0000') break block14;
                                            c = this.handleInvalidChar(c);
                                            ent = String.valueOf(c);
                                            break block11;
                                        }
                                        if (c != '<') break block15;
                                        ent = "&lt;";
                                        break block14;
                                    }
                                    if (c != '&') break block16;
                                    ent = "&amp;";
                                    break block14;
                                }
                                if (c != '>') break block17;
                                if (inPtr >= 2 && text.charAt(inPtr - 2) != ']') break block11;
                                ent = "&gt;";
                                break block14;
                            }
                            if (c < '\u007f') break block11;
                            break block14;
                        }
                        if (c >= highChar) break block14;
                    }
                    if (this.mOutputPtr >= this.mOutputBufLen) {
                        this.flushBuffer();
                    }
                    this.mOutputBuffer[this.mOutputPtr++] = c;
                    continue;
                }
                if (ent != null) {
                    this.writeRaw(ent);
                    continue block0;
                }
                this.writeAsEntity(text.charAt(inPtr - 1));
                continue block0;
            }
            break;
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public void writeCharacters(char[] cbuf, int offset, int len) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mTextWriter != null) {
            this.mTextWriter.write(cbuf, offset, len);
            return;
        }
        int[] QC = QUOTABLE_TEXT_CHARS;
        int highChar = this.mEncHighChar;
        int MAXQC = Math.min(QC.length, highChar);
        len += offset;
        do {
            int outLen;
            char c = '\u0000';
            int start = offset;
            String ent = null;
            while (offset < len) {
                block16: {
                    block17: {
                        block18: {
                            c = cbuf[offset];
                            if (c >= MAXQC) break block17;
                            if (QC[c] == 0) break block16;
                            if (c == '<') {
                                ent = "&lt;";
                                break;
                            }
                            if (c == '&') {
                                ent = "&amp;";
                                break;
                            }
                            if (c != '>') break block18;
                            if (offset == start || cbuf[offset - 1] == ']') {
                                ent = "&gt;";
                                break;
                            }
                            break block16;
                        }
                        if (c < ' ') {
                            if (c != '\n' && c != '\t') {
                                if (c == '\r') {
                                    if (this.mEscapeCR) {
                                        break;
                                    }
                                    break block16;
                                } else {
                                    if (this.mXml11 && c != '\u0000') break;
                                    c = this.handleInvalidChar(c);
                                    ent = String.valueOf(c);
                                    break;
                                }
                            }
                            break block16;
                        } else if (c >= '\u007f') {
                            break;
                        }
                        break block16;
                    }
                    if (c >= highChar) break;
                }
                ++offset;
            }
            if ((outLen = offset - start) > 0) {
                this.writeRaw(cbuf, start, outLen);
            }
            if (ent != null) {
                this.writeRaw(ent);
                ent = null;
                continue;
            }
            if (offset >= len) continue;
            this.writeAsEntity(c);
        } while (++offset < len);
    }

    @Override
    public int writeComment(String data) throws IOException {
        int ix;
        if (this.mCheckContent && (ix = this.verifyCommentContent(data)) >= 0) {
            if (!this.mFixContent) {
                return ix;
            }
            this.writeSegmentedComment(data, ix);
            return -1;
        }
        this.fastWriteRaw("<!--");
        this.writeRaw(data);
        this.fastWriteRaw("-->");
        return -1;
    }

    @Override
    public void writeDTD(String data) throws IOException {
        this.writeRaw(data);
    }

    @Override
    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws IOException, XMLStreamException {
        this.fastWriteRaw("<!DOCTYPE ");
        if (this.mCheckNames) {
            this.verifyNameValidity(rootName, false);
        }
        this.fastWriteRaw(rootName);
        if (systemId != null) {
            if (publicId != null) {
                this.fastWriteRaw(" PUBLIC \"");
                this.fastWriteRaw(publicId);
                this.fastWriteRaw("\" \"");
            } else {
                this.fastWriteRaw(" SYSTEM \"");
            }
            this.fastWriteRaw(systemId);
            this.fastWriteRaw('\"');
        }
        if (internalSubset != null && internalSubset.length() > 0) {
            this.fastWriteRaw(' ', '[');
            this.fastWriteRaw(internalSubset);
            this.fastWriteRaw(']');
        }
        this.fastWriteRaw('>');
    }

    @Override
    public void writeEntityReference(String name) throws IOException, XMLStreamException {
        if (this.mCheckNames) {
            this.verifyNameValidity(name, this.mNsAware);
        }
        this.fastWriteRaw('&');
        this.fastWriteRaw(name);
        this.fastWriteRaw(';');
    }

    @Override
    public void writeXmlDeclaration(String version, String encoding, String standalone) throws IOException {
        char chQuote = this.mUseDoubleQuotesInXmlDecl ? (char)'\"' : '\'';
        this.fastWriteRaw("<?xml version=");
        this.fastWriteRaw(chQuote);
        this.fastWriteRaw(version);
        this.fastWriteRaw(chQuote);
        if (encoding != null && encoding.length() > 0) {
            this.fastWriteRaw(" encoding=");
            this.fastWriteRaw(chQuote);
            this.fastWriteRaw(encoding);
            this.fastWriteRaw(chQuote);
        }
        if (standalone != null) {
            this.fastWriteRaw(" standalone=");
            this.fastWriteRaw(chQuote);
            this.fastWriteRaw(standalone);
            this.fastWriteRaw(chQuote);
        }
        this.fastWriteRaw('?', '>');
    }

    @Override
    public int writePI(String target, String data) throws IOException, XMLStreamException {
        if (this.mCheckNames) {
            this.verifyNameValidity(target, this.mNsAware);
        }
        this.fastWriteRaw('<', '?');
        this.fastWriteRaw(target);
        if (data != null && data.length() > 0) {
            int ix;
            if (this.mCheckContent && (ix = data.indexOf(63)) >= 0 && (ix = data.indexOf("?>", ix)) >= 0) {
                return ix;
            }
            this.fastWriteRaw(' ');
            this.writeRaw(data);
        }
        this.fastWriteRaw('?', '>');
        return -1;
    }

    @Override
    public void writeStartTagStart(String localName) throws IOException, XMLStreamException {
        int ptr;
        int extra;
        if (this.mCheckNames) {
            this.verifyNameValidity(localName, this.mNsAware);
        }
        if ((extra = this.mOutputBufLen - (ptr = this.mOutputPtr) - (1 + localName.length())) < 0) {
            this.fastWriteRaw('<');
            this.fastWriteRaw(localName);
        } else {
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 60;
            int len = localName.length();
            localName.getChars(0, len, buf, ptr);
            this.mOutputPtr = ptr + len;
        }
    }

    @Override
    public void writeStartTagStart(String prefix, String localName) throws IOException, XMLStreamException {
        if (prefix == null || prefix.length() == 0) {
            this.writeStartTagStart(localName);
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(prefix, this.mNsAware);
            this.verifyNameValidity(localName, this.mNsAware);
        }
        int ptr = this.mOutputPtr;
        int len = prefix.length();
        int extra = this.mOutputBufLen - ptr - (2 + localName.length() + len);
        if (extra < 0) {
            this.fastWriteRaw('<');
            this.fastWriteRaw(prefix);
            this.fastWriteRaw(':');
            this.fastWriteRaw(localName);
        } else {
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 60;
            prefix.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 58;
            len = localName.length();
            localName.getChars(0, len, buf, ptr);
            this.mOutputPtr = ptr + len;
        }
    }

    @Override
    public void writeStartTagEnd() throws IOException {
        this.fastWriteRaw('>');
    }

    @Override
    public void writeStartTagEmptyEnd() throws IOException {
        int ptr = this.mOutputPtr;
        if (ptr + 3 >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            }
            this.flushBuffer();
            ptr = this.mOutputPtr;
        }
        char[] buf = this.mOutputBuffer;
        if (this.mAddSpaceAfterEmptyElem) {
            buf[ptr++] = 32;
        }
        buf[ptr++] = 47;
        buf[ptr++] = 62;
        this.mOutputPtr = ptr;
    }

    @Override
    public void writeEndTag(String localName) throws IOException {
        int ptr = this.mOutputPtr;
        int extra = this.mOutputBufLen - ptr - (3 + localName.length());
        if (extra < 0) {
            this.fastWriteRaw('<', '/');
            this.fastWriteRaw(localName);
            this.fastWriteRaw('>');
        } else {
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 60;
            buf[ptr++] = 47;
            int len = localName.length();
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 62;
            this.mOutputPtr = ptr;
        }
    }

    @Override
    public void writeEndTag(String prefix, String localName) throws IOException {
        if (prefix == null || prefix.length() == 0) {
            this.writeEndTag(localName);
            return;
        }
        int ptr = this.mOutputPtr;
        int len = prefix.length();
        int extra = this.mOutputBufLen - ptr - (4 + localName.length() + len);
        if (extra < 0) {
            this.fastWriteRaw('<', '/');
            this.fastWriteRaw(prefix);
            this.fastWriteRaw(':');
            this.fastWriteRaw(localName);
            this.fastWriteRaw('>');
        } else {
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 60;
            buf[ptr++] = 47;
            prefix.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 58;
            len = localName.length();
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 62;
            this.mOutputPtr = ptr;
        }
    }

    @Override
    public void writeAttribute(String localName, String value) throws IOException, XMLStreamException {
        int len;
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(localName, this.mNsAware);
        }
        if (this.mOutputBufLen - this.mOutputPtr - (3 + (len = localName.length())) < 0) {
            this.fastWriteRaw(' ');
            this.fastWriteRaw(localName);
            this.fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 32;
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 61;
            buf[ptr++] = 34;
            this.mOutputPtr = ptr;
        }
        int n = len = value == null ? 0 : value.length();
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, 0, len);
            } else {
                this.writeAttrValue(value, len);
            }
        }
        this.fastWriteRaw('\"');
    }

    @Override
    public void writeAttribute(String localName, char[] value, int offset, int vlen) throws IOException, XMLStreamException {
        int len;
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(localName, this.mNsAware);
        }
        if (this.mOutputBufLen - this.mOutputPtr - (3 + (len = localName.length())) < 0) {
            this.fastWriteRaw(' ');
            this.fastWriteRaw(localName);
            this.fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 32;
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 61;
            buf[ptr++] = 34;
            this.mOutputPtr = ptr;
        }
        if (vlen > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, offset, vlen);
            } else {
                this.writeAttrValue(value, offset, vlen);
            }
        }
        this.fastWriteRaw('\"');
    }

    @Override
    public void writeAttribute(String prefix, String localName, String value) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(prefix, this.mNsAware);
            this.verifyNameValidity(localName, this.mNsAware);
        }
        int len = prefix.length();
        if (this.mOutputBufLen - this.mOutputPtr - (4 + localName.length() + len) < 0) {
            this.fastWriteRaw(' ');
            if (len > 0) {
                this.fastWriteRaw(prefix);
                this.fastWriteRaw(':');
            }
            this.fastWriteRaw(localName);
            this.fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 32;
            prefix.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 58;
            len = localName.length();
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 61;
            buf[ptr++] = 34;
            this.mOutputPtr = ptr;
        }
        int n = len = value == null ? 0 : value.length();
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, 0, len);
            } else {
                this.writeAttrValue(value, len);
            }
        }
        this.fastWriteRaw('\"');
    }

    @Override
    public void writeAttribute(String prefix, String localName, char[] value, int offset, int vlen) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(prefix, this.mNsAware);
            this.verifyNameValidity(localName, this.mNsAware);
        }
        int len = prefix.length();
        if (this.mOutputBufLen - this.mOutputPtr - (4 + localName.length() + len) < 0) {
            this.fastWriteRaw(' ');
            if (len > 0) {
                this.fastWriteRaw(prefix);
                this.fastWriteRaw(':');
            }
            this.fastWriteRaw(localName);
            this.fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 32;
            prefix.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 58;
            len = localName.length();
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 61;
            buf[ptr++] = 34;
            this.mOutputPtr = ptr;
        }
        if (vlen > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, offset, vlen);
            } else {
                this.writeAttrValue(value, offset, vlen);
            }
        }
        this.fastWriteRaw('\"');
    }

    private final void writeAttrValue(String value, int len) throws IOException {
        int inPtr = 0;
        char qchar = this.mEncQuoteChar;
        int highChar = this.mEncHighChar;
        block0: while (true) {
            String ent = null;
            while (inPtr < len) {
                block11: {
                    char c;
                    block10: {
                        block7: {
                            block13: {
                                block12: {
                                    block8: {
                                        block9: {
                                            if ((c = value.charAt(inPtr++)) > '<') break block7;
                                            if (c >= ' ') break block8;
                                            if (c != '\r') break block9;
                                            if (!this.mEscapeCR) break block10;
                                            break block11;
                                        }
                                        if (c == '\n' || c == '\t' || this.mXml11 && c != '\u0000') break block11;
                                        c = this.handleInvalidChar(c);
                                        break block10;
                                    }
                                    if (c != qchar) break block12;
                                    ent = this.mEncQuoteEntity;
                                    break block11;
                                }
                                if (c != '<') break block13;
                                ent = "&lt;";
                                break block11;
                            }
                            if (c != '&') break block10;
                            ent = "&amp;";
                            break block11;
                        }
                        if (c >= highChar) break block11;
                    }
                    if (this.mOutputPtr >= this.mOutputBufLen) {
                        this.flushBuffer();
                    }
                    this.mOutputBuffer[this.mOutputPtr++] = c;
                    continue;
                }
                if (ent != null) {
                    this.writeRaw(ent);
                    continue block0;
                }
                this.writeAsEntity(value.charAt(inPtr - 1));
                continue block0;
            }
            break;
        }
    }

    private final void writeAttrValue(char[] value, int offset, int len) throws IOException {
        len += offset;
        char qchar = this.mEncQuoteChar;
        int highChar = this.mEncHighChar;
        block0: while (true) {
            String ent = null;
            while (offset < len) {
                block11: {
                    char c;
                    block10: {
                        block7: {
                            block13: {
                                block12: {
                                    block8: {
                                        block9: {
                                            if ((c = value[offset++]) > '<') break block7;
                                            if (c >= ' ') break block8;
                                            if (c != '\r') break block9;
                                            if (!this.mEscapeCR) break block10;
                                            break block11;
                                        }
                                        if (c == '\n' || c == '\t' || this.mXml11 && c != '\u0000') break block11;
                                        c = this.handleInvalidChar(c);
                                        break block10;
                                    }
                                    if (c != qchar) break block12;
                                    ent = this.mEncQuoteEntity;
                                    break block11;
                                }
                                if (c != '<') break block13;
                                ent = "&lt;";
                                break block11;
                            }
                            if (c != '&') break block10;
                            ent = "&amp;";
                            break block11;
                        }
                        if (c >= highChar) break block11;
                    }
                    if (this.mOutputPtr >= this.mOutputBufLen) {
                        this.flushBuffer();
                    }
                    this.mOutputBuffer[this.mOutputPtr++] = c;
                    continue;
                }
                if (ent != null) {
                    this.writeRaw(ent);
                    continue block0;
                }
                this.writeAsEntity(value[offset - 1]);
                continue block0;
            }
            break;
        }
    }

    @Override
    public final void writeTypedElement(AsciiValueEncoder enc) throws IOException {
        if (this.mOut == null) {
            return;
        }
        int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            this.flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            if (enc.isCompleted()) break;
            this.flush();
        }
    }

    @Override
    public final void writeTypedElement(AsciiValueEncoder enc, XMLValidator validator, char[] copyBuffer) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            this.flush();
        }
        int start = this.mOutputPtr;
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            validator.validateText(this.mOutputBuffer, start, this.mOutputPtr, false);
            if (enc.isCompleted()) break;
            this.flush();
            start = this.mOutputPtr;
        }
    }

    @Override
    public void writeTypedAttribute(String localName, AsciiValueEncoder enc) throws IOException, XMLStreamException {
        int len;
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(localName, this.mNsAware);
        }
        if (this.mOutputPtr + 3 + (len = localName.length()) > this.mOutputBufLen) {
            this.fastWriteRaw(' ');
            this.fastWriteRaw(localName);
            this.fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 32;
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = 61;
            buf[ptr++] = 34;
            this.mOutputPtr = ptr;
        }
        int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            this.flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            if (enc.isCompleted()) break;
            this.flush();
        }
        this.fastWriteRaw('\"');
    }

    @Override
    public void writeTypedAttribute(String prefix, String localName, AsciiValueEncoder enc) throws IOException, XMLStreamException {
        int llen;
        int plen;
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(prefix, this.mNsAware);
            this.verifyNameValidity(localName, this.mNsAware);
        }
        if (this.mOutputPtr + 4 + (plen = prefix.length()) + (llen = localName.length()) > this.mOutputBufLen) {
            this.writePrefixedName(prefix, localName);
            this.fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 32;
            if (plen > 0) {
                prefix.getChars(0, plen, buf, ptr);
                ptr += plen;
                buf[ptr++] = 58;
            }
            localName.getChars(0, llen, buf, ptr);
            ptr += llen;
            buf[ptr++] = 61;
            buf[ptr++] = 34;
            this.mOutputPtr = ptr;
        }
        int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            this.flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            if (enc.isCompleted()) break;
            this.flush();
        }
        this.fastWriteRaw('\"');
    }

    @Override
    public void writeTypedAttribute(String prefix, String localName, String nsURI, AsciiValueEncoder enc, XMLValidator validator, char[] copyBuffer) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        if (prefix == null) {
            prefix = "";
        }
        if (nsURI == null) {
            nsURI = "";
        }
        int plen = prefix.length();
        if (this.mCheckNames) {
            if (plen > 0) {
                this.verifyNameValidity(prefix, this.mNsAware);
            }
            this.verifyNameValidity(localName, this.mNsAware);
        }
        if (this.mOutputBufLen - this.mOutputPtr - (4 + localName.length() + plen) < 0) {
            this.writePrefixedName(prefix, localName);
            this.fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            buf[ptr++] = 32;
            if (plen > 0) {
                prefix.getChars(0, plen, buf, ptr);
                ptr += plen;
                buf[ptr++] = 58;
            }
            int llen = localName.length();
            localName.getChars(0, llen, buf, ptr);
            ptr += llen;
            buf[ptr++] = 61;
            buf[ptr++] = 34;
            this.mOutputPtr = ptr;
        }
        int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            this.flush();
        }
        int start = this.mOutputPtr;
        this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
        if (enc.isCompleted()) {
            validator.validateAttribute(localName, nsURI, prefix, this.mOutputBuffer, start, this.mOutputPtr);
            this.fastWriteRaw('\"');
            return;
        }
        StringBuilder sb = new StringBuilder(this.mOutputBuffer.length << 1);
        sb.append(this.mOutputBuffer, start, this.mOutputPtr - start);
        do {
            this.flush();
            start = this.mOutputPtr;
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            sb.append(this.mOutputBuffer, start, this.mOutputPtr - start);
        } while (!enc.isCompleted());
        this.fastWriteRaw('\"');
        String valueStr = sb.toString();
        validator.validateAttribute(localName, nsURI, prefix, valueStr);
    }

    protected final void writePrefixedName(String prefix, String localName) throws IOException {
        this.fastWriteRaw(' ');
        if (prefix.length() > 0) {
            this.fastWriteRaw(prefix);
            this.fastWriteRaw(':');
        }
        this.fastWriteRaw(localName);
    }

    private final void flushBuffer() throws IOException {
        if (this.mOutputPtr > 0 && this.mOutputBuffer != null) {
            int ptr = this.mOutputPtr;
            this.mLocPastChars += ptr;
            this.mLocRowStartOffset -= ptr;
            this.mOutputPtr = 0;
            this.mOut.write(this.mOutputBuffer, 0, ptr);
        }
    }

    private final void fastWriteRaw(char c) throws IOException {
        if (this.mOutputPtr >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            }
            this.flushBuffer();
        }
        this.mOutputBuffer[this.mOutputPtr++] = c;
    }

    private final void fastWriteRaw(char c1, char c2) throws IOException {
        if (this.mOutputPtr + 1 >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            }
            this.flushBuffer();
        }
        this.mOutputBuffer[this.mOutputPtr++] = c1;
        this.mOutputBuffer[this.mOutputPtr++] = c2;
    }

    private final void fastWriteRaw(String str) throws IOException {
        int ptr = this.mOutputPtr;
        int len = str.length();
        if (ptr + len >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            }
            if (len > this.mOutputBufLen) {
                this.writeRaw(str);
                return;
            }
            this.flushBuffer();
            ptr = this.mOutputPtr;
        }
        str.getChars(0, len, this.mOutputBuffer, ptr);
        this.mOutputPtr = ptr + len;
    }

    protected int verifyCDataContent(String content) {
        int ix;
        if (content != null && content.length() >= 3 && (ix = content.indexOf(93)) >= 0) {
            return content.indexOf("]]>", ix);
        }
        return -1;
    }

    protected int verifyCDataContent(char[] c, int start, int end) {
        if (c != null) {
            start += 2;
            while (start < end) {
                char ch = c[start];
                if (ch == ']') {
                    ++start;
                    continue;
                }
                if (ch == '>' && c[start - 1] == ']' && c[start - 2] == ']') {
                    return start - 2;
                }
                start += 2;
            }
        }
        return -1;
    }

    protected int verifyCommentContent(String content) {
        int ix = content.indexOf(45);
        if (ix >= 0 && ix < content.length() - 1) {
            ix = content.indexOf("--", ix);
        }
        return ix;
    }

    protected void writeSegmentedCData(String content, int index) throws IOException {
        int start = 0;
        while (index >= 0) {
            this.fastWriteRaw("<![CDATA[");
            this.writeRaw(content, start, index + 2 - start);
            this.fastWriteRaw("]]>");
            start = index + 2;
            index = content.indexOf("]]>", start);
        }
        this.fastWriteRaw("<![CDATA[");
        this.writeRaw(content, start, content.length() - start);
        this.fastWriteRaw("]]>");
    }

    protected void writeSegmentedCData(char[] c, int start, int len, int index) throws IOException {
        int end = start + len;
        while (index >= 0) {
            this.fastWriteRaw("<![CDATA[");
            this.writeRaw(c, start, index + 2 - start);
            this.fastWriteRaw("]]>");
            start = index + 2;
            index = this.verifyCDataContent(c, start, end);
        }
        this.fastWriteRaw("<![CDATA[");
        this.writeRaw(c, start, end - start);
        this.fastWriteRaw("]]>");
    }

    protected void writeSegmentedComment(String content, int index) throws IOException {
        int len = content.length();
        if (index == len - 1) {
            this.fastWriteRaw("<!--");
            this.writeRaw(content);
            this.fastWriteRaw(" -->");
            return;
        }
        this.fastWriteRaw("<!--");
        int start = 0;
        while (index >= 0) {
            this.writeRaw(content, start, index + 1 - start);
            this.fastWriteRaw(' ');
            start = index + 1;
            index = content.indexOf("--", start);
        }
        this.writeRaw(content, start, len - start);
        if (content.charAt(len - 1) == '-') {
            this.fastWriteRaw(' ');
        }
        this.fastWriteRaw("-->");
    }

    public static int guessEncodingBitSize(String enc) {
        if (enc == null || enc.length() == 0) {
            return 16;
        }
        if ((enc = CharsetNames.normalize(enc)) == "UTF-8") {
            return 16;
        }
        if (enc == "ISO-8859-1") {
            return 8;
        }
        if (enc == "US-ASCII") {
            return 7;
        }
        if (enc == "UTF-16" || enc == "UTF-16BE" || enc == "UTF-16LE" || enc == "UTF-32BE" || enc == "UTF-32LE") {
            return 16;
        }
        return 8;
    }

    protected final void writeAsEntity(int c) throws IOException {
        int ptr = this.mOutputPtr;
        char[] buf = this.mOutputBuffer;
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
                    buf[ptr++] = (char)(digit < 10 ? 48 + digit : 87 + digit);
                    c &= 0xF;
                }
                buf[ptr++] = (char)(c < 10 ? 48 + c : 87 + c);
            }
        } else {
            buf[ptr++] = 35;
            buf[ptr++] = 120;
            int shift = 20;
            int origPtr = ptr;
            do {
                int digit;
                if ((digit = c >> shift & 0xF) <= 0 && ptr == origPtr) continue;
                buf[ptr++] = (char)(digit < 10 ? 48 + digit : 87 + digit);
            } while ((shift -= 4) > 0);
            buf[ptr++] = (char)((c &= 0xF) < 10 ? 48 + c : 87 + c);
        }
        buf[ptr++] = 59;
        this.mOutputPtr = ptr;
    }

    static {
        int[] q = new int[4096];
        Arrays.fill(q, 0, 32, 1);
        Arrays.fill(q, 127, 160, 1);
        q[9] = 0;
        q[10] = 0;
        q[60] = 1;
        q[62] = 1;
        q[38] = 1;
        QUOTABLE_TEXT_CHARS = q;
    }
}

