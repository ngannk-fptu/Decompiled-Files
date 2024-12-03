/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.util.XMLChar;

public class ISO9075 {
    private static final Pattern ENCODE_PATTERN = Pattern.compile("_x\\p{XDigit}{4}_");
    private static final char[] PADDING = new char[]{'0', '0', '0'};
    private static final String HEX_DIGITS = "0123456789abcdefABCDEF";

    private ISO9075() {
    }

    public static String encode(String name) {
        if (name.length() == 0) {
            return name;
        }
        if (XMLChar.isValidName(name) && name.indexOf("_x") < 0) {
            return name;
        }
        StringBuffer encoded = new StringBuffer();
        for (int i = 0; i < name.length(); ++i) {
            if (i == 0) {
                if (XMLChar.isNameStart(name.charAt(i))) {
                    if (ISO9075.needsEscaping(name, i)) {
                        ISO9075.encode('_', encoded);
                        continue;
                    }
                    encoded.append(name.charAt(i));
                    continue;
                }
                ISO9075.encode(name.charAt(i), encoded);
                continue;
            }
            if (!XMLChar.isName(name.charAt(i))) {
                ISO9075.encode(name.charAt(i), encoded);
                continue;
            }
            if (ISO9075.needsEscaping(name, i)) {
                ISO9075.encode('_', encoded);
                continue;
            }
            encoded.append(name.charAt(i));
        }
        return encoded.toString();
    }

    public static String encodePath(String path) {
        String[] names = Text.explode(path, 47, true);
        StringBuffer encoded = new StringBuffer(path.length());
        for (int i = 0; i < names.length; ++i) {
            String index = null;
            int idx = names[i].indexOf(91);
            if (idx != -1) {
                index = names[i].substring(idx);
                names[i] = names[i].substring(0, idx);
            }
            encoded.append(ISO9075.encode(names[i]));
            if (index != null) {
                encoded.append(index);
            }
            if (i >= names.length - 1) continue;
            encoded.append('/');
        }
        return encoded.toString();
    }

    public static String decode(String name) {
        if (name.indexOf("_x") < 0) {
            return name;
        }
        StringBuffer decoded = new StringBuffer();
        Matcher m = ENCODE_PATTERN.matcher(name);
        while (m.find()) {
            char ch = (char)Integer.parseInt(m.group().substring(2, 6), 16);
            if (ch == '$' || ch == '\\') {
                m.appendReplacement(decoded, "\\" + ch);
                continue;
            }
            m.appendReplacement(decoded, Character.toString(ch));
        }
        m.appendTail(decoded);
        return decoded.toString();
    }

    private static void encode(char c, StringBuffer b) {
        b.append("_x");
        String hex = Integer.toHexString(c);
        b.append(PADDING, 0, 4 - hex.length());
        b.append(hex);
        b.append("_");
    }

    private static boolean needsEscaping(String name, int location) throws ArrayIndexOutOfBoundsException {
        if (name.charAt(location) == '_' && name.length() >= location + 6) {
            return name.charAt(location + 1) == 'x' && HEX_DIGITS.indexOf(name.charAt(location + 2)) != -1 && HEX_DIGITS.indexOf(name.charAt(location + 3)) != -1 && HEX_DIGITS.indexOf(name.charAt(location + 4)) != -1 && HEX_DIGITS.indexOf(name.charAt(location + 5)) != -1;
        }
        return false;
    }
}

