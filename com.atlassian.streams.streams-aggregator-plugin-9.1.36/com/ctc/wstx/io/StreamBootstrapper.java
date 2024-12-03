/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.exc.WstxEOFException;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.AsciiReader;
import com.ctc.wstx.io.BaseReader;
import com.ctc.wstx.io.CharsetNames;
import com.ctc.wstx.io.EBCDICCodec;
import com.ctc.wstx.io.ISOLatinReader;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.MergedStream;
import com.ctc.wstx.io.UTF32Reader;
import com.ctc.wstx.io.UTF8Reader;
import com.ctc.wstx.io.WstxInputLocation;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public final class StreamBootstrapper
extends InputBootstrapper {
    static final int MIN_BUF_SIZE = 128;
    final InputStream mIn;
    private byte[] mByteBuffer;
    private final boolean mRecycleBuffer;
    private int mInputPtr;
    private int mInputEnd;
    boolean mBigEndian = true;
    boolean mHadBOM = false;
    boolean mByteSizeFound = false;
    int mBytesPerChar;
    boolean mEBCDIC = false;
    String mInputEncoding = null;
    int[] mSingleByteTranslation = null;

    private StreamBootstrapper(String pubId, String sysId, InputStream in) {
        super(pubId, sysId);
        this.mIn = in;
        this.mInputEnd = 0;
        this.mInputPtr = 0;
        this.mRecycleBuffer = true;
    }

    private StreamBootstrapper(String pubId, String sysId, byte[] data, int start, int end) {
        super(pubId, sysId);
        this.mIn = null;
        this.mRecycleBuffer = false;
        this.mByteBuffer = data;
        this.mInputPtr = start;
        this.mInputEnd = end;
    }

    public static StreamBootstrapper getInstance(String pubId, String sysId, InputStream in) {
        return new StreamBootstrapper(pubId, sysId, in);
    }

    public static StreamBootstrapper getInstance(String pubId, String sysId, byte[] data, int start, int end) {
        return new StreamBootstrapper(pubId, sysId, data, start, end);
    }

    public Reader bootstrapInput(ReaderConfig cfg, boolean mainDoc, int xmlVersion) throws IOException, XMLStreamException {
        BaseReader r;
        String normEnc = null;
        int bufSize = cfg.getInputBufferLength();
        if (bufSize < 128) {
            bufSize = 128;
        }
        if (this.mByteBuffer == null) {
            this.mByteBuffer = cfg.allocFullBBuffer(bufSize);
        }
        this.resolveStreamEncoding();
        if (this.hasXmlDecl()) {
            this.readXmlDecl(mainDoc, xmlVersion);
            if (this.mFoundEncoding != null) {
                normEnc = this.verifyXmlEncoding(this.mFoundEncoding);
            }
        } else {
            boolean bl = this.mXml11Handling = 272 == xmlVersion;
        }
        if (normEnc == null) {
            if (this.mEBCDIC) {
                if (this.mFoundEncoding == null || this.mFoundEncoding.length() == 0) {
                    this.reportXmlProblem("Missing encoding declaration: underlying encoding looks like an EBCDIC variant, but no xml encoding declaration found");
                }
                normEnc = this.mFoundEncoding;
            } else {
                normEnc = this.mBytesPerChar == 2 ? (this.mBigEndian ? "UTF-16BE" : "UTF-16LE") : (this.mBytesPerChar == 4 ? (this.mBigEndian ? "UTF-32BE" : "UTF-32LE") : "UTF-8");
            }
        }
        this.mInputEncoding = normEnc;
        if (normEnc == "UTF-8") {
            r = new UTF8Reader(cfg, this.mIn, this.mByteBuffer, this.mInputPtr, this.mInputEnd, this.mRecycleBuffer);
        } else if (normEnc == "ISO-8859-1") {
            r = new ISOLatinReader(cfg, this.mIn, this.mByteBuffer, this.mInputPtr, this.mInputEnd, this.mRecycleBuffer);
        } else if (normEnc == "US-ASCII") {
            r = new AsciiReader(cfg, this.mIn, this.mByteBuffer, this.mInputPtr, this.mInputEnd, this.mRecycleBuffer);
        } else if (normEnc.startsWith("UTF-32")) {
            if (normEnc == "UTF-32") {
                this.mInputEncoding = this.mBigEndian ? "UTF-32BE" : "UTF-32LE";
            }
            r = new UTF32Reader(cfg, this.mIn, this.mByteBuffer, this.mInputPtr, this.mInputEnd, this.mRecycleBuffer, this.mBigEndian);
        } else {
            InputStream in = this.mIn;
            if (this.mInputPtr < this.mInputEnd) {
                in = new MergedStream(cfg, in, this.mByteBuffer, this.mInputPtr, this.mInputEnd);
            }
            if (normEnc == "UTF-16") {
                normEnc = this.mBigEndian ? "UTF-16BE" : "UTF-16LE";
                this.mInputEncoding = normEnc;
            }
            try {
                return new InputStreamReader(in, normEnc);
            }
            catch (UnsupportedEncodingException usex) {
                throw new WstxIOException("Unsupported encoding: " + usex.getMessage());
            }
        }
        if (this.mXml11Handling) {
            ((BaseReader)r).setXmlCompliancy(272);
        }
        return r;
    }

    public String getInputEncoding() {
        return this.mInputEncoding;
    }

    public int getInputTotal() {
        int total = this.mInputProcessed + this.mInputPtr;
        if (this.mBytesPerChar > 1) {
            total /= this.mBytesPerChar;
        }
        return total;
    }

    public int getInputColumn() {
        int col = this.mInputPtr - this.mInputRowStart;
        if (this.mBytesPerChar > 1) {
            col /= this.mBytesPerChar;
        }
        return col;
    }

    protected void resolveStreamEncoding() throws IOException, WstxException {
        this.mBytesPerChar = 0;
        this.mBigEndian = true;
        if (this.ensureLoaded(4)) {
            int quartet = this.mByteBuffer[0] << 24 | (this.mByteBuffer[1] & 0xFF) << 16 | (this.mByteBuffer[2] & 0xFF) << 8 | this.mByteBuffer[3] & 0xFF;
            block0 : switch (quartet) {
                case 65279: {
                    this.mBigEndian = true;
                    this.mBytesPerChar = 4;
                    this.mInputPtr = 4;
                    break;
                }
                case -131072: {
                    this.mBytesPerChar = 4;
                    this.mInputPtr = 4;
                    this.mBigEndian = false;
                    break;
                }
                case 65534: {
                    this.reportWeirdUCS4("2143");
                    break;
                }
                case -16842752: {
                    this.reportWeirdUCS4("3412");
                    break;
                }
                default: {
                    int msw = quartet >>> 16;
                    if (msw == 65279) {
                        this.mBytesPerChar = 2;
                        this.mInputPtr = 2;
                        this.mBigEndian = true;
                        break;
                    }
                    if (msw == 65534) {
                        this.mBytesPerChar = 2;
                        this.mInputPtr = 2;
                        this.mBigEndian = false;
                        break;
                    }
                    if (quartet >>> 8 == 0xEFBBBF) {
                        this.mInputPtr = 3;
                        this.mBytesPerChar = 1;
                        this.mBigEndian = true;
                        break;
                    }
                    switch (quartet) {
                        case 60: {
                            this.mBigEndian = true;
                            this.mBytesPerChar = 4;
                            break block0;
                        }
                        case 0x3C000000: {
                            this.mBytesPerChar = 4;
                            this.mBigEndian = false;
                            break block0;
                        }
                        case 15360: {
                            this.reportWeirdUCS4("2143");
                            break block0;
                        }
                        case 0x3C0000: {
                            this.reportWeirdUCS4("3412");
                            break block0;
                        }
                        case 3932223: {
                            this.mBytesPerChar = 2;
                            this.mBigEndian = true;
                            break block0;
                        }
                        case 1006649088: {
                            this.mBytesPerChar = 2;
                            this.mBigEndian = false;
                            break block0;
                        }
                        case 1010792557: {
                            this.mBytesPerChar = 1;
                            this.mBigEndian = true;
                            break block0;
                        }
                        case 1282385812: {
                            this.mBytesPerChar = -1;
                            this.mEBCDIC = true;
                            this.mSingleByteTranslation = EBCDICCodec.getCp037Mapping();
                            break block0;
                        }
                    }
                }
            }
            this.mHadBOM = this.mInputPtr > 0;
            this.mInputProcessed = -this.mInputPtr;
            this.mInputRowStart = this.mInputPtr;
        }
        boolean bl = this.mByteSizeFound = this.mBytesPerChar != 0;
        if (!this.mByteSizeFound) {
            this.mBytesPerChar = 1;
            this.mBigEndian = true;
        }
    }

    protected String verifyXmlEncoding(String enc) throws WstxException {
        if ((enc = CharsetNames.normalize(enc)) == "UTF-8") {
            this.verifyEncoding(enc, 1);
        } else if (enc == "ISO-8859-1") {
            this.verifyEncoding(enc, 1);
        } else if (enc == "US-ASCII") {
            this.verifyEncoding(enc, 1);
        } else if (enc == "UTF-16") {
            this.verifyEncoding(enc, 2);
        } else if (enc == "UTF-16LE") {
            this.verifyEncoding(enc, 2, false);
        } else if (enc == "UTF-16BE") {
            this.verifyEncoding(enc, 2, true);
        } else if (enc == "UTF-32") {
            this.verifyEncoding(enc, 4);
        } else if (enc == "UTF-32LE") {
            this.verifyEncoding(enc, 4, false);
        } else if (enc == "UTF-32BE") {
            this.verifyEncoding(enc, 4, true);
        }
        return enc;
    }

    protected boolean ensureLoaded(int minimum) throws IOException {
        int count;
        for (int gotten = this.mInputEnd - this.mInputPtr; gotten < minimum; gotten += count) {
            int n = count = this.mIn == null ? -1 : this.mIn.read(this.mByteBuffer, this.mInputEnd, this.mByteBuffer.length - this.mInputEnd);
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
        int n = this.mInputEnd = this.mIn == null ? -1 : this.mIn.read(this.mByteBuffer, 0, this.mByteBuffer.length);
        if (this.mInputEnd < 1) {
            throw new WstxEOFException(" in xml declaration", this.getLocation());
        }
    }

    protected void pushback() {
        this.mInputPtr = this.mBytesPerChar < 0 ? (this.mInputPtr += this.mBytesPerChar) : (this.mInputPtr -= this.mBytesPerChar);
    }

    protected int getNext() throws IOException, WstxException {
        if (this.mBytesPerChar != 1) {
            if (this.mBytesPerChar == -1) {
                return this.nextTranslated();
            }
            return this.nextMultiByte();
        }
        byte b = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
        return b & 0xFF;
    }

    protected int getNextAfterWs(boolean reqWs) throws IOException, WstxException {
        int count = this.mBytesPerChar == 1 ? this.skipSbWs() : (this.mBytesPerChar == -1 ? this.skipTranslatedWs() : this.skipMbWs());
        if (reqWs && count == 0) {
            this.reportUnexpectedChar(this.getNext(), "; expected a white space");
        }
        if (this.mBytesPerChar != 1) {
            if (this.mBytesPerChar == -1) {
                return this.nextTranslated();
            }
            return this.nextMultiByte();
        }
        byte b = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
        return b & 0xFF;
    }

    protected int checkKeyword(String exp) throws IOException, WstxException {
        if (this.mBytesPerChar != 1) {
            if (this.mBytesPerChar == -1) {
                return this.checkTranslatedKeyword(exp);
            }
            return this.checkMbKeyword(exp);
        }
        return this.checkSbKeyword(exp);
    }

    protected int readQuotedValue(char[] kw, int quoteChar) throws IOException, WstxException {
        boolean mb;
        int i = 0;
        int len = kw.length;
        boolean simple = this.mBytesPerChar == 1;
        boolean bl = mb = !simple && this.mBytesPerChar > 1;
        while (i < len) {
            int c;
            if (simple) {
                int b;
                int n = b = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
                if (b == 0) {
                    this.reportNull();
                }
                if (b == 13 || b == 10) {
                    this.skipSbLF((byte)b);
                    b = 10;
                }
                c = b & 0xFF;
            } else if (mb) {
                c = this.nextMultiByte();
                if (c == 13 || c == 10) {
                    this.skipMbLF(c);
                    c = 10;
                }
            } else {
                c = this.nextTranslated();
                if (c == 13 || c == 10) {
                    this.skipTranslatedLF(c);
                    c = 10;
                }
            }
            if (c == quoteChar) {
                return i < len ? i : -1;
            }
            if (i >= len) continue;
            kw[i++] = (char)c;
        }
        return -1;
    }

    protected boolean hasXmlDecl() throws IOException, WstxException {
        if (this.mBytesPerChar == 1) {
            if (this.ensureLoaded(6) && this.mByteBuffer[this.mInputPtr] == 60 && this.mByteBuffer[this.mInputPtr + 1] == 63 && this.mByteBuffer[this.mInputPtr + 2] == 120 && this.mByteBuffer[this.mInputPtr + 3] == 109 && this.mByteBuffer[this.mInputPtr + 4] == 108 && (this.mByteBuffer[this.mInputPtr + 5] & 0xFF) <= 32) {
                this.mInputPtr += 6;
                return true;
            }
        } else if (this.mBytesPerChar == -1) {
            if (this.ensureLoaded(6)) {
                int start = this.mInputPtr;
                if (this.nextTranslated() == 60 && this.nextTranslated() == 63 && this.nextTranslated() == 120 && this.nextTranslated() == 109 && this.nextTranslated() == 108 && this.nextTranslated() <= 32) {
                    return true;
                }
                this.mInputPtr = start;
            }
        } else if (this.ensureLoaded(6 * this.mBytesPerChar)) {
            int start = this.mInputPtr;
            if (this.nextMultiByte() == 60 && this.nextMultiByte() == 63 && this.nextMultiByte() == 120 && this.nextMultiByte() == 109 && this.nextMultiByte() == 108 && this.nextMultiByte() <= 32) {
                return true;
            }
            this.mInputPtr = start;
        }
        return false;
    }

    protected Location getLocation() {
        int total = this.mInputProcessed + this.mInputPtr;
        int col = this.mInputPtr - this.mInputRowStart;
        if (this.mBytesPerChar > 1) {
            total /= this.mBytesPerChar;
            col /= this.mBytesPerChar;
        }
        return new WstxInputLocation(null, this.mPublicId, this.mSystemId, total - 1, this.mInputRow, col);
    }

    protected byte nextByte() throws IOException, WstxException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMore();
        }
        return this.mByteBuffer[this.mInputPtr++];
    }

    protected int skipSbWs() throws IOException, WstxException {
        int count = 0;
        while (true) {
            byte b;
            byte by = b = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
            if ((b & 0xFF) > 32) {
                --this.mInputPtr;
                break;
            }
            if (b == 13 || b == 10) {
                this.skipSbLF(b);
            } else if (b == 0) {
                this.reportNull();
            }
            ++count;
        }
        return count;
    }

    protected void skipSbLF(byte lfByte) throws IOException, WstxException {
        if (lfByte == 13) {
            byte b;
            byte by = b = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
            if (b != 10) {
                --this.mInputPtr;
            }
        }
        ++this.mInputRow;
        this.mInputRowStart = this.mInputPtr;
    }

    protected int checkSbKeyword(String expected) throws IOException, WstxException {
        int len = expected.length();
        for (int ptr = 1; ptr < len; ++ptr) {
            byte b;
            byte by = b = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
            if (b == 0) {
                this.reportNull();
            }
            if ((b & 0xFF) == expected.charAt(ptr)) continue;
            return b & 0xFF;
        }
        return 0;
    }

    protected int nextMultiByte() throws IOException, WstxException {
        int c;
        byte b2;
        byte b = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
        byte by = b2 = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
        if (this.mBytesPerChar == 2) {
            c = this.mBigEndian ? (b & 0xFF) << 8 | b2 & 0xFF : b & 0xFF | (b2 & 0xFF) << 8;
        } else {
            byte b3 = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
            byte b4 = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
            c = this.mBigEndian ? b << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | b4 & 0xFF : b4 << 24 | (b3 & 0xFF) << 16 | (b2 & 0xFF) << 8 | b & 0xFF;
        }
        if (c == 0) {
            this.reportNull();
        }
        return c;
    }

    protected int nextTranslated() throws IOException, WstxException {
        byte b = this.mInputPtr < this.mInputEnd ? this.mByteBuffer[this.mInputPtr++] : this.nextByte();
        int ch = this.mSingleByteTranslation[b & 0xFF];
        if (ch < 0) {
            ch = -ch;
        }
        return ch;
    }

    protected int skipMbWs() throws IOException, WstxException {
        int count = 0;
        while (true) {
            int c;
            if ((c = this.nextMultiByte()) > 32) {
                this.mInputPtr -= this.mBytesPerChar;
                break;
            }
            if (c == 13 || c == 10) {
                this.skipMbLF(c);
            } else if (c == 0) {
                this.reportNull();
            }
            ++count;
        }
        return count;
    }

    protected int skipTranslatedWs() throws IOException, WstxException {
        int count = 0;
        while (true) {
            int c;
            if ((c = this.nextTranslated()) > 32 && c != 133) {
                --this.mInputPtr;
                break;
            }
            if (c == 13 || c == 10) {
                this.skipTranslatedLF(c);
            } else if (c == 0) {
                this.reportNull();
            }
            ++count;
        }
        return count;
    }

    protected void skipMbLF(int lf) throws IOException, WstxException {
        int c;
        if (lf == 13 && (c = this.nextMultiByte()) != 10) {
            this.mInputPtr -= this.mBytesPerChar;
        }
        ++this.mInputRow;
        this.mInputRowStart = this.mInputPtr;
    }

    protected void skipTranslatedLF(int lf) throws IOException, WstxException {
        int c;
        if (lf == 13 && (c = this.nextTranslated()) != 10) {
            --this.mInputPtr;
        }
        ++this.mInputRow;
        this.mInputRowStart = this.mInputPtr;
    }

    protected int checkMbKeyword(String expected) throws IOException, WstxException {
        int len = expected.length();
        for (int ptr = 1; ptr < len; ++ptr) {
            int c = this.nextMultiByte();
            if (c == 0) {
                this.reportNull();
            }
            if (c == expected.charAt(ptr)) continue;
            return c;
        }
        return 0;
    }

    protected int checkTranslatedKeyword(String expected) throws IOException, WstxException {
        int len = expected.length();
        for (int ptr = 1; ptr < len; ++ptr) {
            int c = this.nextTranslated();
            if (c == 0) {
                this.reportNull();
            }
            if (c == expected.charAt(ptr)) continue;
            return c;
        }
        return 0;
    }

    private void verifyEncoding(String id, int bpc) throws WstxException {
        if (this.mByteSizeFound && bpc != this.mBytesPerChar) {
            if (this.mEBCDIC) {
                this.reportXmlProblem("Declared encoding '" + id + "' incompatible with auto-detected physical encoding (EBCDIC variant), can not decode input since actual code page not known");
            }
            this.reportXmlProblem("Declared encoding '" + id + "' uses " + bpc + " bytes per character; but physical encoding appeared to use " + this.mBytesPerChar + "; cannot decode");
        }
    }

    private void verifyEncoding(String id, int bpc, boolean bigEndian) throws WstxException {
        if (this.mByteSizeFound) {
            this.verifyEncoding(id, bpc);
            if (bigEndian != this.mBigEndian) {
                String bigStr = bigEndian ? "big" : "little";
                this.reportXmlProblem("Declared encoding '" + id + "' has different endianness (" + bigStr + " endian) than what physical ordering appeared to be; cannot decode");
            }
        }
    }

    private void reportWeirdUCS4(String type) throws IOException {
        throw new CharConversionException("Unsupported UCS-4 endianness (" + type + ") detected");
    }
}

