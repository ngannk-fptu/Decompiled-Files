/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.factories;

import com.lowagie.text.SpecialSymbol;

public class GreekAlphabetFactory {
    public static final String getString(int index) {
        return GreekAlphabetFactory.getString(index, true);
    }

    public static final String getLowerCaseString(int index) {
        return GreekAlphabetFactory.getString(index);
    }

    public static final String getUpperCaseString(int index) {
        return GreekAlphabetFactory.getString(index).toUpperCase();
    }

    public static final String getString(int index, boolean lowercase) {
        if (index < 1) {
            return "";
        }
        --index;
        int bytes = 1;
        int start = 0;
        int symbols = 24;
        while (index >= symbols + start) {
            ++bytes;
            start += symbols;
            symbols *= 24;
        }
        int c = index - start;
        char[] value = new char[bytes];
        while (bytes > 0) {
            value[--bytes] = (char)(c % 24);
            if (value[bytes] > '\u0010') {
                int n = bytes;
                value[n] = (char)(value[n] + '\u0001');
            }
            int n = bytes;
            value[n] = (char)(value[n] + (lowercase ? 945 : 913));
            value[bytes] = SpecialSymbol.getCorrespondingSymbol(value[bytes]);
            c /= 24;
        }
        return String.valueOf(value);
    }
}

