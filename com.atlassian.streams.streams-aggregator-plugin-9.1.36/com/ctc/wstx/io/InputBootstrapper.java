/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.exc.WstxUnexpectedCharException;
import java.io.IOException;
import java.io.Reader;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public abstract class InputBootstrapper {
    protected static final String ERR_XMLDECL_KW_VERSION = "; expected keyword 'version'";
    protected static final String ERR_XMLDECL_KW_ENCODING = "; expected keyword 'encoding'";
    protected static final String ERR_XMLDECL_KW_STANDALONE = "; expected keyword 'standalone'";
    protected static final String ERR_XMLDECL_END_MARKER = "; expected \"?>\" end marker";
    protected static final String ERR_XMLDECL_EXP_SPACE = "; expected a white space";
    protected static final String ERR_XMLDECL_EXP_EQ = "; expected '=' after ";
    protected static final String ERR_XMLDECL_EXP_ATTRVAL = "; expected a quote character enclosing value for ";
    public static final char CHAR_NULL = '\u0000';
    public static final char CHAR_SPACE = ' ';
    public static final char CHAR_NEL = '\u0085';
    public static final byte CHAR_CR = 13;
    public static final byte CHAR_LF = 10;
    public static final byte BYTE_NULL = 0;
    public static final byte BYTE_CR = 13;
    public static final byte BYTE_LF = 10;
    protected final String mPublicId;
    protected final String mSystemId;
    protected int mInputProcessed = 0;
    protected int mInputRow = 1;
    protected int mInputRowStart = 0;
    int mDeclaredXmlVersion = 0;
    String mFoundEncoding;
    String mStandalone;
    boolean mXml11Handling = false;
    final char[] mKeyword = new char[60];

    protected InputBootstrapper(String pubId, String sysId) {
        this.mPublicId = pubId;
        this.mSystemId = sysId;
    }

    protected void initFrom(InputBootstrapper src) {
        this.mInputProcessed = src.mInputProcessed;
        this.mInputRow = src.mInputRow;
        this.mInputRowStart = src.mInputRowStart;
        this.mDeclaredXmlVersion = src.mDeclaredXmlVersion;
        this.mFoundEncoding = src.mFoundEncoding;
        this.mStandalone = src.mStandalone;
        this.mXml11Handling = src.mXml11Handling;
    }

    public abstract Reader bootstrapInput(ReaderConfig var1, boolean var2, int var3) throws IOException, XMLStreamException;

    public String getPublicId() {
        return this.mPublicId;
    }

    public String getSystemId() {
        return this.mSystemId;
    }

    public int getDeclaredVersion() {
        return this.mDeclaredXmlVersion;
    }

    public boolean declaredXml11() {
        return this.mDeclaredXmlVersion == 272;
    }

    public String getStandalone() {
        return this.mStandalone;
    }

    public String getDeclaredEncoding() {
        return this.mFoundEncoding;
    }

    public abstract int getInputTotal();

    public int getInputRow() {
        return this.mInputRow;
    }

    public abstract int getInputColumn();

    public abstract String getInputEncoding();

    protected void readXmlDecl(boolean isMainDoc, int xmlVersion) throws IOException, WstxException {
        boolean thisIs11;
        int c = this.getNextAfterWs(false);
        if (c != 118) {
            if (isMainDoc) {
                this.reportUnexpectedChar(c, ERR_XMLDECL_KW_VERSION);
            }
        } else {
            this.mDeclaredXmlVersion = this.readXmlVersion();
            c = this.getWsOrChar(63);
        }
        boolean bl = thisIs11 = this.mDeclaredXmlVersion == 272;
        if (xmlVersion != 0) {
            boolean bl2 = this.mXml11Handling = 272 == xmlVersion;
            if (thisIs11 && !this.mXml11Handling) {
                this.reportXmlProblem(ErrorConsts.ERR_XML_10_VS_11);
            }
        } else {
            this.mXml11Handling = thisIs11;
        }
        if (c != 101) {
            if (!isMainDoc) {
                this.reportUnexpectedChar(c, ERR_XMLDECL_KW_ENCODING);
            }
        } else {
            this.mFoundEncoding = this.readXmlEncoding();
            c = this.getWsOrChar(63);
        }
        if (isMainDoc && c == 115) {
            this.mStandalone = this.readXmlStandalone();
            c = this.getWsOrChar(63);
        }
        if (c != 63) {
            this.reportUnexpectedChar(c, ERR_XMLDECL_END_MARKER);
        }
        if ((c = this.getNext()) != 62) {
            this.reportUnexpectedChar(c, ERR_XMLDECL_END_MARKER);
        }
    }

    private final int readXmlVersion() throws IOException, WstxException {
        int len;
        int c = this.checkKeyword("version");
        if (c != 0) {
            this.reportUnexpectedChar(c, "version");
        }
        if ((len = this.readQuotedValue(this.mKeyword, c = this.handleEq("version"))) == 3 && this.mKeyword[0] == '1' && this.mKeyword[1] == '.') {
            c = this.mKeyword[2];
            if (c == 48) {
                return 256;
            }
            if (c == 49) {
                return 272;
            }
        }
        String got = len < 0 ? "'" + new String(this.mKeyword) + "[..]'" : (len == 0 ? "<empty>" : "'" + new String(this.mKeyword, 0, len) + "'");
        this.reportPseudoAttrProblem("version", got, "1.0", "1.1");
        return 0;
    }

    private final String readXmlEncoding() throws IOException, WstxException {
        int len;
        int c = this.checkKeyword("encoding");
        if (c != 0) {
            this.reportUnexpectedChar(c, "encoding");
        }
        if ((len = this.readQuotedValue(this.mKeyword, c = this.handleEq("encoding"))) == 0) {
            this.reportPseudoAttrProblem("encoding", null, null, null);
        }
        if (len < 0) {
            return new String(this.mKeyword);
        }
        return new String(this.mKeyword, 0, len);
    }

    private final String readXmlStandalone() throws IOException, WstxException {
        int len;
        int c = this.checkKeyword("standalone");
        if (c != 0) {
            this.reportUnexpectedChar(c, "standalone");
        }
        if ((len = this.readQuotedValue(this.mKeyword, c = this.handleEq("standalone"))) == 2) {
            if (this.mKeyword[0] == 'n' && this.mKeyword[1] == 'o') {
                return "no";
            }
        } else if (len == 3 && this.mKeyword[0] == 'y' && this.mKeyword[1] == 'e' && this.mKeyword[2] == 's') {
            return "yes";
        }
        String got = len < 0 ? "'" + new String(this.mKeyword) + "[..]'" : (len == 0 ? "<empty>" : "'" + new String(this.mKeyword, 0, len) + "'");
        this.reportPseudoAttrProblem("standalone", got, "yes", "no");
        return got;
    }

    private final int handleEq(String attr) throws IOException, WstxException {
        int c = this.getNextAfterWs(false);
        if (c != 61) {
            this.reportUnexpectedChar(c, "; expected '=' after '" + attr + "'");
        }
        if ((c = this.getNextAfterWs(false)) != 34 && c != 39) {
            this.reportUnexpectedChar(c, "; expected a quote character enclosing value for '" + attr + "'");
        }
        return c;
    }

    private final int getWsOrChar(int ok) throws IOException, WstxException {
        int c = this.getNext();
        if (c == ok) {
            return c;
        }
        if (c > 32) {
            this.reportUnexpectedChar(c, "; expected either '" + (char)ok + "' or white space");
        }
        if (c == 10 || c == 13) {
            this.pushback();
        }
        return this.getNextAfterWs(false);
    }

    protected abstract void pushback();

    protected abstract int getNext() throws IOException, WstxException;

    protected abstract int getNextAfterWs(boolean var1) throws IOException, WstxException;

    protected abstract int checkKeyword(String var1) throws IOException, WstxException;

    protected abstract int readQuotedValue(char[] var1, int var2) throws IOException, WstxException;

    protected abstract Location getLocation();

    protected void reportNull() throws WstxException {
        throw new WstxException("Illegal null byte in input stream", this.getLocation());
    }

    protected void reportXmlProblem(String msg) throws WstxException {
        throw new WstxParsingException(msg, this.getLocation());
    }

    protected void reportUnexpectedChar(int i, String msg) throws WstxException {
        char c = (char)i;
        String excMsg = Character.isISOControl(c) ? "Unexpected character (CTRL-CHAR, code " + i + ")" + msg : "Unexpected character '" + c + "' (code " + i + ")" + msg;
        Location loc = this.getLocation();
        throw new WstxUnexpectedCharException(excMsg, loc, c);
    }

    private final void reportPseudoAttrProblem(String attrName, String got, String expVal1, String expVal2) throws WstxException {
        String expStr;
        String string = expStr = expVal1 == null ? "" : "; expected \"" + expVal1 + "\" or \"" + expVal2 + "\"";
        if (got == null || got.length() == 0) {
            throw new WstxParsingException("Missing XML pseudo-attribute '" + attrName + "' value" + expStr, this.getLocation());
        }
        throw new WstxParsingException("Invalid XML pseudo-attribute '" + attrName + "' value " + got + expStr, this.getLocation());
    }
}

