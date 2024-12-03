/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.Time;
import com.twelvemonkeys.util.TimeFormatter;

class TextFormatter
extends TimeFormatter {
    String text = null;

    TextFormatter(String string) {
        this.text = string;
        if (string != null) {
            this.digits = string.length();
        }
    }

    @Override
    String format(Time time) {
        return this.text;
    }
}

