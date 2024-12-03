/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.support;

import java.util.Base64;
import org.springframework.ldap.BadLdapGrammarException;
import org.springframework.util.Assert;

public final class LdapEncoder {
    private static final int HEX = 16;
    private static String[] NAME_ESCAPE_TABLE;
    private static String[] FILTER_ESCAPE_TABLE;
    private static final int RFC2849_MAX_BASE64_CHARS_PER_LINE = 76;

    private LdapEncoder() {
    }

    protected static String toTwoCharHex(char c) {
        String raw = Integer.toHexString(c).toUpperCase();
        if (raw.length() > 1) {
            return raw;
        }
        return "0" + raw;
    }

    public static String filterEncode(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder encodedValue = new StringBuilder(value.length() * 2);
        int length = value.length();
        for (int i = 0; i < length; ++i) {
            char c = value.charAt(i);
            if (c < FILTER_ESCAPE_TABLE.length) {
                encodedValue.append(FILTER_ESCAPE_TABLE[c]);
                continue;
            }
            encodedValue.append(c);
        }
        return encodedValue.toString();
    }

    public static String nameEncode(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder encodedValue = new StringBuilder(value.length() * 2);
        int length = value.length();
        int last = length - 1;
        for (int i = 0; i < length; ++i) {
            String esc;
            char c = value.charAt(i);
            if (c == ' ' && (i == 0 || i == last)) {
                encodedValue.append("\\ ");
                continue;
            }
            if (c < NAME_ESCAPE_TABLE.length && (esc = NAME_ESCAPE_TABLE[c]) != null) {
                encodedValue.append(esc);
                continue;
            }
            encodedValue.append(c);
        }
        return encodedValue.toString();
    }

    public static String nameDecode(String value) throws BadLdapGrammarException {
        if (value == null) {
            return null;
        }
        StringBuilder decoded = new StringBuilder(value.length());
        int i = 0;
        while (i < value.length()) {
            char currentChar = value.charAt(i);
            if (currentChar == '\\') {
                if (value.length() <= i + 1) {
                    throw new BadLdapGrammarException("Unexpected end of value unterminated '\\'");
                }
                char nextChar = value.charAt(i + 1);
                if (nextChar == ',' || nextChar == '=' || nextChar == '+' || nextChar == '<' || nextChar == '>' || nextChar == '#' || nextChar == ';' || nextChar == '\\' || nextChar == '\"' || nextChar == ' ') {
                    decoded.append(nextChar);
                    i += 2;
                    continue;
                }
                if (value.length() <= i + 2) {
                    throw new BadLdapGrammarException("Unexpected end of value expected special or hex, found '" + nextChar + "'");
                }
                String hexString = "" + nextChar + value.charAt(i + 2);
                decoded.append((char)Integer.parseInt(hexString, 16));
                i += 3;
                continue;
            }
            decoded.append(currentChar);
            ++i;
        }
        return decoded.toString();
    }

    public static String printBase64Binary(byte[] val) {
        Assert.notNull((Object)val, (String)"val must not be null!");
        String encoded = LdapEncoder.encode(val);
        int length = encoded.length();
        StringBuilder sb = new StringBuilder(length + length / 76);
        int len = length;
        for (int i = 0; i < len; ++i) {
            sb.append(encoded.charAt(i));
            if ((i + 1) % 76 != 0) continue;
            sb.append('\n');
            sb.append(' ');
        }
        return sb.toString();
    }

    public static byte[] parseBase64Binary(String val) {
        Assert.notNull((Object)val, (String)"val must not be null!");
        int length = val.length();
        StringBuilder sb = new StringBuilder(length);
        int len = length;
        for (int i = 0; i < len; ++i) {
            char c = val.charAt(i);
            if (c == '\n') {
                if (i + 1 >= len || val.charAt(i + 1) != ' ') continue;
                ++i;
                continue;
            }
            sb.append(c);
        }
        return LdapEncoder.decode(sb.toString());
    }

    private static String encode(byte[] decoded) {
        return Base64.getEncoder().encodeToString(decoded);
    }

    private static byte[] decode(String encoded) {
        return Base64.getDecoder().decode(encoded);
    }

    static {
        char c;
        NAME_ESCAPE_TABLE = new String[96];
        FILTER_ESCAPE_TABLE = new String[93];
        for (c = '\u0000'; c < ' '; c = (char)(c + '\u0001')) {
            LdapEncoder.NAME_ESCAPE_TABLE[c] = "\\" + LdapEncoder.toTwoCharHex(c);
        }
        LdapEncoder.NAME_ESCAPE_TABLE[35] = "\\#";
        LdapEncoder.NAME_ESCAPE_TABLE[44] = "\\,";
        LdapEncoder.NAME_ESCAPE_TABLE[59] = "\\;";
        LdapEncoder.NAME_ESCAPE_TABLE[61] = "\\=";
        LdapEncoder.NAME_ESCAPE_TABLE[43] = "\\+";
        LdapEncoder.NAME_ESCAPE_TABLE[60] = "\\<";
        LdapEncoder.NAME_ESCAPE_TABLE[62] = "\\>";
        LdapEncoder.NAME_ESCAPE_TABLE[34] = "\\\"";
        LdapEncoder.NAME_ESCAPE_TABLE[92] = "\\\\";
        for (c = '\u0000'; c < FILTER_ESCAPE_TABLE.length; c = (char)(c + '\u0001')) {
            LdapEncoder.FILTER_ESCAPE_TABLE[c] = String.valueOf(c);
        }
        LdapEncoder.FILTER_ESCAPE_TABLE[42] = "\\2a";
        LdapEncoder.FILTER_ESCAPE_TABLE[40] = "\\28";
        LdapEncoder.FILTER_ESCAPE_TABLE[41] = "\\29";
        LdapEncoder.FILTER_ESCAPE_TABLE[92] = "\\5c";
        LdapEncoder.FILTER_ESCAPE_TABLE[0] = "\\00";
    }
}

