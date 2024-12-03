/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.util.XmlChars;

public class WstxInputData {
    public static final char CHAR_NULL = '\u0000';
    public static final char INT_NULL = '\u0000';
    public static final char CHAR_SPACE = ' ';
    public static final char INT_SPACE = ' ';
    public static final int MAX_UNICODE_CHAR = 0x10FFFF;
    private static final int VALID_CHAR_COUNT = 256;
    private static final byte NAME_CHAR_INVALID_B = 0;
    private static final byte NAME_CHAR_ALL_VALID_B = 1;
    private static final byte NAME_CHAR_VALID_NONFIRST_B = -1;
    private static final byte[] sCharValidity;
    private static final int VALID_PUBID_CHAR_COUNT = 128;
    private static final byte[] sPubidValidity;
    private static final byte PUBID_CHAR_VALID_B = 1;
    protected boolean mXml11 = false;
    protected char[] mInputBuffer;
    protected int mInputPtr = 0;
    protected int mInputEnd = 0;
    protected long mCurrInputProcessed = 0L;
    protected int mCurrInputRow = 1;
    protected int mCurrInputRowStart = 0;

    protected WstxInputData() {
    }

    public void copyBufferStateFrom(WstxInputData src) {
        this.mInputBuffer = src.mInputBuffer;
        this.mInputPtr = src.mInputPtr;
        this.mInputEnd = src.mInputEnd;
        this.mCurrInputProcessed = src.mCurrInputProcessed;
        this.mCurrInputRow = src.mCurrInputRow;
        this.mCurrInputRowStart = src.mCurrInputRowStart;
    }

    protected final boolean isNameStartChar(char c) {
        if (c <= 'z') {
            if (c >= 'a') {
                return true;
            }
            if (c < 'A') {
                return false;
            }
            return c <= 'Z' || c == '_';
        }
        return this.mXml11 ? XmlChars.is11NameStartChar(c) : XmlChars.is10NameStartChar(c);
    }

    protected final boolean isNameChar(char c) {
        if (c <= 'z') {
            if (c >= 'a') {
                return true;
            }
            if (c <= 'Z') {
                if (c >= 'A') {
                    return true;
                }
                return c >= '0' && c <= '9' || c == '.' || c == '-';
            }
            return c == '_';
        }
        return this.mXml11 ? XmlChars.is11NameChar(c) : XmlChars.is10NameChar(c);
    }

    public static final boolean isNameStartChar(char c, boolean nsAware, boolean xml11) {
        if (c <= 'z') {
            if (c >= 'a') {
                return true;
            }
            if (c < 'A') {
                return c == ':' && !nsAware;
            }
            return c <= 'Z' || c == '_';
        }
        return xml11 ? XmlChars.is11NameStartChar(c) : XmlChars.is10NameStartChar(c);
    }

    public static final boolean isNameChar(char c, boolean nsAware, boolean xml11) {
        if (c <= 'z') {
            if (c >= 'a') {
                return true;
            }
            if (c <= 'Z') {
                if (c >= 'A') {
                    return true;
                }
                return c >= '0' && c <= '9' || c == '.' || c == '-' || c == ':' && !nsAware;
            }
            return c == '_';
        }
        return xml11 ? XmlChars.is11NameChar(c) : XmlChars.is10NameChar(c);
    }

    public static final int findIllegalNameChar(String name, boolean nsAware, boolean xml11) {
        int len = name.length();
        if (len < 1) {
            return -1;
        }
        char c = name.charAt(0);
        if (c <= 'z' ? c < 'a' && (c < 'A' ? c != ':' || nsAware : c > 'Z' && c != '_') : (xml11 ? !XmlChars.is11NameStartChar(c) : !XmlChars.is10NameStartChar(c))) {
            return 0;
        }
        for (int i = 1; i < len; ++i) {
            c = name.charAt(i);
            if (c <= 'z' ? c >= 'a' || (c <= 'Z' ? c >= 'A' || c >= '0' && c <= '9' || c == '.' || c == '-' || c == ':' && !nsAware : c == '_') : (xml11 ? XmlChars.is11NameChar(c) : XmlChars.is10NameChar(c))) continue;
            return i;
        }
        return -1;
    }

    public static final int findIllegalNmtokenChar(String nmtoken, boolean nsAware, boolean xml11) {
        int len = nmtoken.length();
        for (int i = 1; i < len; ++i) {
            char c = nmtoken.charAt(i);
            if (c <= 'z' ? c >= 'a' || (c <= 'Z' ? c >= 'A' || c >= '0' && c <= '9' || c == '.' || c == '-' || c == ':' && !nsAware : c == '_') : (xml11 ? XmlChars.is11NameChar(c) : XmlChars.is10NameChar(c))) continue;
            return i;
        }
        return -1;
    }

    public static final boolean isSpaceChar(char c) {
        return c <= ' ';
    }

    public static String getCharDesc(char c) {
        char i = c;
        if (Character.isISOControl(c)) {
            return "(CTRL-CHAR, code " + i + ")";
        }
        if (i > '\u00ff') {
            return "'" + c + "' (code " + i + " / 0x" + Integer.toHexString(i) + ")";
        }
        return "'" + c + "' (code " + i + ")";
    }

    static {
        int i;
        sCharValidity = new byte[256];
        WstxInputData.sCharValidity[95] = 1;
        int last = 25;
        for (i = 0; i <= last; ++i) {
            WstxInputData.sCharValidity[65 + i] = 1;
            WstxInputData.sCharValidity[97 + i] = 1;
        }
        for (i = 192; i < 256; ++i) {
            WstxInputData.sCharValidity[i] = 1;
        }
        WstxInputData.sCharValidity[215] = 0;
        WstxInputData.sCharValidity[247] = 0;
        WstxInputData.sCharValidity[45] = -1;
        WstxInputData.sCharValidity[46] = -1;
        WstxInputData.sCharValidity[183] = -1;
        for (i = 48; i <= 57; ++i) {
            WstxInputData.sCharValidity[i] = -1;
        }
        sPubidValidity = new byte[128];
        last = 25;
        for (i = 0; i <= last; ++i) {
            WstxInputData.sPubidValidity[65 + i] = 1;
            WstxInputData.sPubidValidity[97 + i] = 1;
        }
        for (i = 48; i <= 57; ++i) {
            WstxInputData.sPubidValidity[i] = 1;
        }
        WstxInputData.sPubidValidity[10] = 1;
        WstxInputData.sPubidValidity[13] = 1;
        WstxInputData.sPubidValidity[32] = 1;
        WstxInputData.sPubidValidity[45] = 1;
        WstxInputData.sPubidValidity[39] = 1;
        WstxInputData.sPubidValidity[40] = 1;
        WstxInputData.sPubidValidity[41] = 1;
        WstxInputData.sPubidValidity[43] = 1;
        WstxInputData.sPubidValidity[44] = 1;
        WstxInputData.sPubidValidity[46] = 1;
        WstxInputData.sPubidValidity[47] = 1;
        WstxInputData.sPubidValidity[58] = 1;
        WstxInputData.sPubidValidity[61] = 1;
        WstxInputData.sPubidValidity[63] = 1;
        WstxInputData.sPubidValidity[59] = 1;
        WstxInputData.sPubidValidity[33] = 1;
        WstxInputData.sPubidValidity[42] = 1;
        WstxInputData.sPubidValidity[35] = 1;
        WstxInputData.sPubidValidity[64] = 1;
        WstxInputData.sPubidValidity[36] = 1;
        WstxInputData.sPubidValidity[95] = 1;
        WstxInputData.sPubidValidity[37] = 1;
    }
}

