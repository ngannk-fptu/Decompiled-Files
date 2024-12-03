/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.jvm;

import com.mchange.v1.jvm.TypeFormatException;

public final class InternalNameUtils {
    public static String dottifySlashesAndDollarSigns(String string) {
        return InternalNameUtils._dottifySlashesAndDollarSigns(string).toString();
    }

    public static String decodeType(String string) throws TypeFormatException {
        return InternalNameUtils._decodeType(string).toString();
    }

    public static String decodeTypeList(String string) throws TypeFormatException {
        StringBuffer stringBuffer = new StringBuffer(64);
        InternalNameUtils._decodeTypeList(string, 0, stringBuffer);
        return stringBuffer.toString();
    }

    public static boolean isPrimitive(char c) {
        return c == 'Z' || c == 'B' || c == 'C' || c == 'S' || c == 'I' || c == 'J' || c == 'F' || c == 'D' || c == 'V';
    }

    private static void _decodeTypeList(String string, int n, StringBuffer stringBuffer) throws TypeFormatException {
        char c;
        if (stringBuffer.length() != 0) {
            stringBuffer.append(' ');
        }
        if (InternalNameUtils.isPrimitive(c = string.charAt(n))) {
            stringBuffer.append(InternalNameUtils._decodeType(string.substring(n, n + 1)));
            ++n;
        } else {
            int n2;
            if (c == '[') {
                int n3 = n + 1;
                while (string.charAt(n3) == '[') {
                    ++n3;
                }
                if (string.charAt(n3) == 'L') {
                    ++n3;
                    while (string.charAt(n3) != ';') {
                        ++n3;
                    }
                }
                n2 = n3;
            } else {
                n2 = string.indexOf(59, n);
                if (n2 < 0) {
                    throw new TypeFormatException(string.substring(n) + " is neither a primitive nor semicolon terminated!");
                }
            }
            int n4 = n;
            n = n2 + 1;
            stringBuffer.append(InternalNameUtils._decodeType(string.substring(n4, n)));
        }
        if (n < string.length()) {
            stringBuffer.append(',');
            InternalNameUtils._decodeTypeList(string, n, stringBuffer);
        }
    }

    private static StringBuffer _decodeType(String string) throws TypeFormatException {
        StringBuffer stringBuffer;
        int n = 0;
        char c = string.charAt(0);
        switch (c) {
            case 'Z': {
                stringBuffer = new StringBuffer("boolean");
                break;
            }
            case 'B': {
                stringBuffer = new StringBuffer("byte");
                break;
            }
            case 'C': {
                stringBuffer = new StringBuffer("char");
                break;
            }
            case 'S': {
                stringBuffer = new StringBuffer("short");
                break;
            }
            case 'I': {
                stringBuffer = new StringBuffer("int");
                break;
            }
            case 'J': {
                stringBuffer = new StringBuffer("long");
                break;
            }
            case 'F': {
                stringBuffer = new StringBuffer("float");
                break;
            }
            case 'D': {
                stringBuffer = new StringBuffer("double");
                break;
            }
            case 'V': {
                stringBuffer = new StringBuffer("void");
                break;
            }
            case '[': {
                ++n;
                stringBuffer = InternalNameUtils._decodeType(string.substring(1));
                break;
            }
            case 'L': {
                stringBuffer = InternalNameUtils._decodeSimpleClassType(string);
                break;
            }
            default: {
                throw new TypeFormatException(string + " is not a valid inernal type name.");
            }
        }
        for (int i = 0; i < n; ++i) {
            stringBuffer.append("[]");
        }
        return stringBuffer;
    }

    private static StringBuffer _decodeSimpleClassType(String string) throws TypeFormatException {
        int n = string.length();
        if (string.charAt(0) != 'L' || string.charAt(n - 1) != ';') {
            throw new TypeFormatException(string + " is not a valid representation of a simple class type.");
        }
        return InternalNameUtils._dottifySlashesAndDollarSigns(string.substring(1, n - 1));
    }

    private static StringBuffer _dottifySlashesAndDollarSigns(String string) {
        StringBuffer stringBuffer = new StringBuffer(string);
        int n = stringBuffer.length();
        for (int i = 0; i < n; ++i) {
            char c = stringBuffer.charAt(i);
            if (c != '/' && c != '$') continue;
            stringBuffer.setCharAt(i, '.');
        }
        return stringBuffer;
    }

    private InternalNameUtils() {
    }

    public static void main(String[] stringArray) {
        try {
            System.out.println(InternalNameUtils.decodeTypeList(stringArray[0]));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

