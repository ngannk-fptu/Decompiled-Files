/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.util.Time;
import com.twelvemonkeys.util.TimeFormatter;

class MinutesFormatter
extends TimeFormatter {
    MinutesFormatter(int n) {
        this.digits = n;
    }

    @Override
    String format(Time time) {
        if ((double)time.getMinutes() >= Math.pow(10.0, this.digits)) {
            return Integer.toString(time.getMinutes());
        }
        return StringUtil.pad(String.valueOf(time.getMinutes()), this.digits, "0", true);
    }
}

