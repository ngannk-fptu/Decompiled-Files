/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.factories;

import com.lowagie.text.error_messages.MessageLocalization;

public class RomanAlphabetFactory {
    public static final String getString(int index) {
        if (index < 1) {
            throw new NumberFormatException(MessageLocalization.getComposedMessage("you.can.t.translate.a.negative.number.into.an.alphabetical.value"));
        }
        --index;
        int bytes = 1;
        int start = 0;
        int symbols = 26;
        while (index >= symbols + start) {
            ++bytes;
            start += symbols;
            symbols *= 26;
        }
        int c = index - start;
        char[] value = new char[bytes];
        while (bytes > 0) {
            value[--bytes] = (char)(97 + c % 26);
            c /= 26;
        }
        return new String(value);
    }

    public static final String getLowerCaseString(int index) {
        return RomanAlphabetFactory.getString(index);
    }

    public static final String getUpperCaseString(int index) {
        return RomanAlphabetFactory.getString(index).toUpperCase();
    }

    public static final String getString(int index, boolean lowercase) {
        if (lowercase) {
            return RomanAlphabetFactory.getLowerCaseString(index);
        }
        return RomanAlphabetFactory.getUpperCaseString(index);
    }
}

