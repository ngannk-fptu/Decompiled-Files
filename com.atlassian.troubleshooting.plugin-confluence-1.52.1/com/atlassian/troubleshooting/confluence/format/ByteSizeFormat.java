/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.confluence.format;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class ByteSizeFormat
extends NumberFormat {
    @Override
    public StringBuffer format(double arg0, StringBuffer arg1, FieldPosition arg2) {
        DecimalFormat decimalFormatter = new DecimalFormat("#,###.#");
        if (arg0 < 1024.0) {
            arg1.append(arg0 + "B");
        } else if (arg0 < 1048576.0) {
            arg1.append(decimalFormatter.format(arg0 / 1024.0) + "KB");
        } else if (arg0 < 1.073741824E9) {
            arg1.append(decimalFormatter.format(arg0 / 1048576.0) + "MB");
        } else if (arg0 < 1.073741824E9) {
            arg1.append(decimalFormatter.format(arg0 / 1.073741824E9) + "GB");
        }
        return arg1;
    }

    @Override
    public StringBuffer format(long arg0, StringBuffer arg1, FieldPosition arg2) {
        return this.format((double)arg0, arg1, arg2);
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        return null;
    }
}

