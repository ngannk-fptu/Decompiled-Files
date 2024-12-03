/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.util.Time;
import com.twelvemonkeys.util.TimeFormatter;

class SecondsFormatter
extends TimeFormatter {
    SecondsFormatter(int n) {
        this.digits = n;
    }

    @Override
    String format(Time time) {
        if (this.digits < 0) {
            return Integer.toString(time.getTime());
        }
        if ((double)time.getSeconds() >= Math.pow(10.0, this.digits)) {
            return Integer.toString(time.getSeconds());
        }
        return StringUtil.pad(String.valueOf(time.getSeconds()), this.digits, "0", true);
    }
}

