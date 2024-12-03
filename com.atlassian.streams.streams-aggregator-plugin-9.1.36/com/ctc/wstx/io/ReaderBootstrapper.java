/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.exc.WstxEOFException;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.MergedReader;
import com.ctc.wstx.io.WstxInputLocation;
import com.ctc.wstx.util.StringUtil;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationProblem;

public final class ReaderBootstrapper
extends InputBootstrapper {
    static final char CHAR_BOM_MARKER = '\ufeff';
    final Reader mIn;
    final String mInputEncoding;
    private char[] mCharBuffer;
    private int mInputPtr;
    private int mInputEnd;

    private ReaderBootstrapper(String pubId, String sysId, Reader r, String appEncoding) {
        super(pubId, sysId);
        this.mIn = r;
        if (appEncoding == null && r instanceof InputStreamReader) {
            appEncoding = ((InputStreamReader)r).getEncoding();
        }
        this.mInputEncoding = appEncoding;
    }

    public static ReaderBootstrapper getInstance(String pubId, String sysId, Reader r, String appEncoding) {
        return new ReaderBootstrapper(pubId, sysId, r, appEncoding);
    }

    public Reader bootstrapInput(ReaderConfig cfg, boolean mainDoc, int xmlVersion) throws IOException, XMLStreamException {
        this.mCharBuffer = cfg == null ? new char[128] : cfg.allocSmallCBuffer(128);
        this.initialLoad(7);
        if (this.mInputEnd >= 7) {
            char c = this.mCharBuffer[this.mInputPtr];
            if (c == '\ufeff') {
                c = this.mCharBuffer[++this.mInputPtr];
            }
            if (c == '<') {
                if (this.mCharBuffer[this.mInputPtr + 1] == '?' && this.mCharBuffer[this.mInputPtr + 2] == 'x' && this.mCharBuffer[this.mInputPtr + 3] == 'm' && this.mCharBuffer[this.mInputPtr + 4] == 'l' && this.mCharBuffer[this.mInputPtr + 5] <= ' ') {
                    this.mInputPtr += 6;
                    this.readXmlDecl(mainDoc, xmlVersion);
                    if (this.mFoundEncoding != null && this.mInputEncoding != null) {
                        this.verifyXmlEncoding(cfg);
                    }
                }
            } else if (c == '\u00ef') {
                throw new WstxIOException("Unexpected first character (char code 0xEF), not valid in xml document: could be mangled UTF-8 BOM marker. Make sure that the Reader uses correct encoding or pass an InputStream instead");
            }
        }
        if (this.mInputPtr < this.mInputEnd) {
            return new MergedReader(cfg, this.mIn, this.mCharBuffer, this.mInputPtr, this.mInputEnd);
        }
        return this.mIn;
    }

    public String getInputEncoding() {
        return this.mInputEncoding;
    }

    public int getInputTotal() {
        return this.mInputProcessed + this.mInputPtr;
    }

    public int getInputColumn() {
        return this.mInputPtr - this.mInputRowStart;
    }

    protected void verifyXmlEncoding(ReaderConfig cfg) throws XMLStreamException {
        String inputEnc = this.mInputEncoding;
        if (StringUtil.equalEncodings(inputEnc, this.mFoundEncoding)) {
            return;
        }
        XMLReporter rep = cfg.getXMLReporter();
        if (rep != null) {
            Location loc = this.getLocation();
            String msg = MessageFormat.format(ErrorConsts.W_MIXED_ENCODINGS, this.mFoundEncoding, inputEnc);
            String type = ErrorConsts.WT_XML_DECL;
            XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 1, type);
            rep.report(msg, type, prob, loc);
        }
    }

    protected boolean initialLoad(int minimum) throws IOException {
        this.mInputPtr = 0;
        this.mInputEnd = 0;
        while (this.mInputEnd < minimum) {
            int count = this.mIn.read(this.mCharBuffer, this.mInputEnd, this.mCharBuffer.length - this.mInputEnd);
            if (count < 1) {
                return false;
            }
            this.mInputEnd += count;
        }
        return true;
    }

    protected void loadMore() throws IOException, WstxException {
        this.mInputProcessed += this.mInputEnd;
        this.mInputRowStart -= this.mInputEnd;
        this.mInputPtr = 0;
        this.mInputEnd = this.mIn.read(this.mCharBuffer, 0, this.mCharBuffer.length);
        if (this.mInputEnd < 1) {
            throw new WstxEOFException(" in xml declaration", this.getLocation());
        }
    }

    protected void pushback() {
        --this.mInputPtr;
    }

    protected int getNext() throws IOException, WstxException {
        return this.mInputPtr < this.mInputEnd ? this.mCharBuffer[this.mInputPtr++] : this.nextChar();
    }

    protected int getNextAfterWs(boolean reqWs) throws IOException, WstxException {
        int count = 0;
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mCharBuffer[this.mInputPtr++] : this.nextChar();
            if (c > ' ') {
                if (reqWs && count == 0) {
                    this.reportUnexpectedChar(c, "; expected a white space");
                }
                return c;
            }
            if (c == '\r' || c == '\n') {
                this.skipCRLF(c);
            } else if (c == '\u0000') {
                this.reportNull();
            }
            ++count;
        }
    }

    protected int checkKeyword(String exp) throws IOException, WstxException {
        int len = exp.length();
        for (int ptr = 1; ptr < len; ++ptr) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mCharBuffer[this.mInputPtr++] : this.nextChar();
            if (c != exp.charAt(ptr)) {
                return c;
            }
            if (c != '\u0000') continue;
            this.reportNull();
        }
        return 0;
    }

    protected int readQuotedValue(char[] kw, int quoteChar) throws IOException, WstxException {
        int i = 0;
        int len = kw.length;
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mCharBuffer[this.mInputPtr++] : this.nextChar();
            if (c == '\r' || c == '\n') {
                this.skipCRLF(c);
            } else if (c == '\u0000') {
                this.reportNull();
            }
            if (c == quoteChar) {
                return i < len ? i : -1;
            }
            if (i >= len) continue;
            kw[i++] = c;
        }
    }

    protected Location getLocation() {
        return new WstxInputLocation(null, this.mPublicId, this.mSystemId, this.mInputProcessed + this.mInputPtr - 1, this.mInputRow, this.mInputPtr - this.mInputRowStart);
    }

    protected char nextChar() throws IOException, WstxException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMore();
        }
        return this.mCharBuffer[this.mInputPtr++];
    }

    protected void skipCRLF(char lf) throws IOException, WstxException {
        if (lf == '\r') {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mCharBuffer[this.mInputPtr++] : this.nextChar();
            if (c != '\n') {
                --this.mInputPtr;
            }
        }
        ++this.mInputRow;
        this.mInputRowStart = this.mInputPtr;
    }
}

