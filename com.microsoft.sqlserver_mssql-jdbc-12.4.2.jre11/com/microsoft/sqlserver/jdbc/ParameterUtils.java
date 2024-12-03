/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;

final class ParameterUtils {
    private ParameterUtils() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static byte[] hexToBin(String hexV) throws SQLServerException {
        int len = hexV.length();
        char[] orig = hexV.toCharArray();
        if (len % 2 != 0) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_stringNotInHex"), null, false);
        }
        byte[] bin = new byte[len / 2];
        for (int i = 0; i < len / 2; ++i) {
            bin[i] = (byte)((ParameterUtils.charToHex(orig[2 * i]) << 4) + (ParameterUtils.charToHex(orig[2 * i + 1]) & 0xFF));
        }
        return bin;
    }

    static byte charToHex(char ctx) throws SQLServerException {
        byte ret = 0;
        if (ctx >= 'A' && ctx <= 'F') {
            ret = (byte)(ctx - 65 + 10);
        } else if (ctx >= 'a' && ctx <= 'f') {
            ret = (byte)(ctx - 97 + 10);
        } else if (ctx >= '0' && ctx <= '9') {
            ret = (byte)(ctx - 48);
        } else {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_stringNotInHex"), null, false);
        }
        return ret;
    }

    static int scanSQLForChar(char ch, String sql, int offset) {
        int len = sql.length();
        block6: while (offset < len) {
            char chTmp = sql.charAt(offset++);
            switch (chTmp) {
                case '[': {
                    chTmp = ']';
                }
                case '\"': 
                case '\'': {
                    char chQuote = chTmp;
                    while (offset < len) {
                        if (sql.charAt(offset++) != chQuote) continue;
                        if (len == offset || sql.charAt(offset) != chQuote) continue block6;
                        ++offset;
                    }
                    continue block6;
                }
                case '/': {
                    if (offset == len) continue block6;
                    if (sql.charAt(offset) == '*') {
                        while (++offset < len) {
                            if (sql.charAt(offset) != '*' || offset + 1 >= len || sql.charAt(offset + 1) != '/') continue;
                            offset += 2;
                            continue block6;
                        }
                        continue block6;
                    }
                    if (sql.charAt(offset) == '-') continue block6;
                }
                case '-': {
                    if (offset < 0 || offset >= sql.length() || sql.charAt(offset) != '-') break;
                    while (++offset < len) {
                        if (sql.charAt(offset) != '\n' && sql.charAt(offset) != '\r') continue;
                        ++offset;
                        continue block6;
                    }
                    continue block6;
                }
            }
            if (ch != chTmp) continue;
            return offset - 1;
        }
        return len;
    }
}

