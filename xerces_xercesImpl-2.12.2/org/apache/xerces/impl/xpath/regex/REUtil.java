/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xpath.regex;

import java.text.CharacterIterator;
import org.apache.xerces.impl.xpath.regex.Match;
import org.apache.xerces.impl.xpath.regex.ParseException;
import org.apache.xerces.impl.xpath.regex.RegularExpression;

public final class REUtil {
    static final int CACHESIZE = 20;
    static final RegularExpression[] regexCache = new RegularExpression[20];

    private REUtil() {
    }

    static final int composeFromSurrogates(int n, int n2) {
        return 65536 + (n - 55296 << 10) + n2 - 56320;
    }

    static final boolean isLowSurrogate(int n) {
        return (n & 0xFC00) == 56320;
    }

    static final boolean isHighSurrogate(int n) {
        return (n & 0xFC00) == 55296;
    }

    static final String decomposeToSurrogates(int n) {
        char[] cArray = new char[]{(char)(((n -= 65536) >> 10) + 55296), (char)((n & 0x3FF) + 56320)};
        return new String(cArray);
    }

    static final String substring(CharacterIterator characterIterator, int n, int n2) {
        char[] cArray = new char[n2 - n];
        for (int i = 0; i < cArray.length; ++i) {
            cArray[i] = characterIterator.setIndex(i + n);
        }
        return new String(cArray);
    }

    static final int getOptionValue(int n) {
        int n2 = 0;
        switch (n) {
            case 105: {
                n2 = 2;
                break;
            }
            case 109: {
                n2 = 8;
                break;
            }
            case 115: {
                n2 = 4;
                break;
            }
            case 120: {
                n2 = 16;
                break;
            }
            case 117: {
                n2 = 32;
                break;
            }
            case 119: {
                n2 = 64;
                break;
            }
            case 70: {
                n2 = 256;
                break;
            }
            case 72: {
                n2 = 128;
                break;
            }
            case 88: {
                n2 = 512;
                break;
            }
            case 44: {
                n2 = 1024;
                break;
            }
        }
        return n2;
    }

    static final int parseOptions(String string) throws ParseException {
        if (string == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < string.length(); ++i) {
            int n2 = REUtil.getOptionValue(string.charAt(i));
            if (n2 == 0) {
                throw new ParseException("Unknown Option: " + string.substring(i), -1);
            }
            n |= n2;
        }
        return n;
    }

    static final String createOptionString(int n) {
        StringBuffer stringBuffer = new StringBuffer(9);
        if ((n & 0x100) != 0) {
            stringBuffer.append('F');
        }
        if ((n & 0x80) != 0) {
            stringBuffer.append('H');
        }
        if ((n & 0x200) != 0) {
            stringBuffer.append('X');
        }
        if ((n & 2) != 0) {
            stringBuffer.append('i');
        }
        if ((n & 8) != 0) {
            stringBuffer.append('m');
        }
        if ((n & 4) != 0) {
            stringBuffer.append('s');
        }
        if ((n & 0x20) != 0) {
            stringBuffer.append('u');
        }
        if ((n & 0x40) != 0) {
            stringBuffer.append('w');
        }
        if ((n & 0x10) != 0) {
            stringBuffer.append('x');
        }
        if ((n & 0x400) != 0) {
            stringBuffer.append(',');
        }
        return stringBuffer.toString().intern();
    }

    static String stripExtendedComment(String string) {
        int n = string.length();
        StringBuffer stringBuffer = new StringBuffer(n);
        int n2 = 0;
        int n3 = 0;
        while (n2 < n) {
            char c;
            char c2;
            if ((c2 = string.charAt(n2++)) == '\t' || c2 == '\n' || c2 == '\f' || c2 == '\r' || c2 == ' ') {
                if (n3 <= 0) continue;
                stringBuffer.append(c2);
                continue;
            }
            if (c2 == '#') {
                while (n2 < n && (c2 = string.charAt(n2++)) != '\r' && c2 != '\n') {
                }
                continue;
            }
            if (c2 == '\\' && n2 < n) {
                c = string.charAt(n2);
                if (c == '#' || c == '\t' || c == '\n' || c == '\f' || c == '\r' || c == ' ') {
                    stringBuffer.append(c);
                    ++n2;
                    continue;
                }
                stringBuffer.append('\\');
                stringBuffer.append(c);
                ++n2;
                continue;
            }
            if (c2 == '[') {
                ++n3;
                stringBuffer.append(c2);
                if (n2 >= n) continue;
                c = string.charAt(n2);
                if (c == '[' || c == ']') {
                    stringBuffer.append(c);
                    ++n2;
                    continue;
                }
                if (c != '^' || n2 + 1 >= n || (c = string.charAt(n2 + 1)) != '[' && c != ']') continue;
                stringBuffer.append('^');
                stringBuffer.append(c);
                n2 += 2;
                continue;
            }
            if (n3 > 0 && c2 == ']') {
                --n3;
            }
            stringBuffer.append(c2);
        }
        return stringBuffer.toString();
    }

    public static void main(String[] stringArray) {
        String string = null;
        try {
            String string2 = "";
            String string3 = null;
            if (stringArray.length == 0) {
                System.out.println("Error:Usage: java REUtil -i|-m|-s|-u|-w|-X regularExpression String");
                System.exit(0);
            }
            for (int i = 0; i < stringArray.length; ++i) {
                if (stringArray[i].length() == 0 || stringArray[i].charAt(0) != '-') {
                    if (string == null) {
                        string = stringArray[i];
                        continue;
                    }
                    if (string3 == null) {
                        string3 = stringArray[i];
                        continue;
                    }
                    System.err.println("Unnecessary: " + stringArray[i]);
                    continue;
                }
                if (stringArray[i].equals("-i")) {
                    string2 = string2 + "i";
                    continue;
                }
                if (stringArray[i].equals("-m")) {
                    string2 = string2 + "m";
                    continue;
                }
                if (stringArray[i].equals("-s")) {
                    string2 = string2 + "s";
                    continue;
                }
                if (stringArray[i].equals("-u")) {
                    string2 = string2 + "u";
                    continue;
                }
                if (stringArray[i].equals("-w")) {
                    string2 = string2 + "w";
                    continue;
                }
                if (stringArray[i].equals("-X")) {
                    string2 = string2 + "X";
                    continue;
                }
                System.err.println("Unknown option: " + stringArray[i]);
            }
            RegularExpression regularExpression = new RegularExpression(string, string2);
            System.out.println("RegularExpression: " + regularExpression);
            Match match = new Match();
            regularExpression.matches(string3, match);
            for (int i = 0; i < match.getNumberOfGroups(); ++i) {
                if (i == 0) {
                    System.out.print("Matched range for the whole pattern: ");
                } else {
                    System.out.print("[" + i + "]: ");
                }
                if (match.getBeginning(i) < 0) {
                    System.out.println("-1");
                    continue;
                }
                System.out.print(match.getBeginning(i) + ", " + match.getEnd(i) + ", ");
                System.out.println("\"" + match.getCapturedText(i) + "\"");
            }
        }
        catch (ParseException parseException) {
            if (string == null) {
                parseException.printStackTrace();
            } else {
                System.err.println("org.apache.xerces.utils.regex.ParseException: " + parseException.getMessage());
                String string4 = "        ";
                System.err.println(string4 + string);
                int n = parseException.getLocation();
                if (n >= 0) {
                    System.err.print(string4);
                    for (int i = 0; i < n; ++i) {
                        System.err.print("-");
                    }
                    System.err.println("^");
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RegularExpression createRegex(String string, String string2) throws ParseException {
        RegularExpression regularExpression = null;
        int n = REUtil.parseOptions(string2);
        RegularExpression[] regularExpressionArray = regexCache;
        synchronized (regexCache) {
            int n2;
            for (n2 = 0; n2 < 20; ++n2) {
                RegularExpression regularExpression2 = regexCache[n2];
                if (regularExpression2 == null) {
                    n2 = -1;
                    break;
                }
                if (!regularExpression2.equals(string, n)) continue;
                regularExpression = regularExpression2;
                break;
            }
            if (regularExpression != null) {
                if (n2 != 0) {
                    System.arraycopy(regexCache, 0, regexCache, 1, n2);
                    REUtil.regexCache[0] = regularExpression;
                }
            } else {
                regularExpression = new RegularExpression(string, string2);
                System.arraycopy(regexCache, 0, regexCache, 1, 19);
                REUtil.regexCache[0] = regularExpression;
            }
            // ** MonitorExit[var4_4] (shouldn't be in output)
            return regularExpression;
        }
    }

    public static boolean matches(String string, String string2) throws ParseException {
        return REUtil.createRegex(string, null).matches(string2);
    }

    public static boolean matches(String string, String string2, String string3) throws ParseException {
        return REUtil.createRegex(string, string2).matches(string3);
    }

    public static String quoteMeta(String string) {
        int n = string.length();
        StringBuffer stringBuffer = null;
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (".*+?{[()|\\^$".indexOf(c) >= 0) {
                if (stringBuffer == null) {
                    stringBuffer = new StringBuffer(i + (n - i) * 2);
                    if (i > 0) {
                        stringBuffer.append(string.substring(0, i));
                    }
                }
                stringBuffer.append('\\');
                stringBuffer.append(c);
                continue;
            }
            if (stringBuffer == null) continue;
            stringBuffer.append(c);
        }
        return stringBuffer != null ? stringBuffer.toString() : string;
    }

    static void dumpString(String string) {
        for (int i = 0; i < string.length(); ++i) {
            System.out.print(Integer.toHexString(string.charAt(i)));
            System.out.print(" ");
        }
        System.out.println();
    }
}

