/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.Time;

abstract class TimeFormatter {
    int digits = 0;

    TimeFormatter() {
    }

    abstract String format(Time var1);
}

