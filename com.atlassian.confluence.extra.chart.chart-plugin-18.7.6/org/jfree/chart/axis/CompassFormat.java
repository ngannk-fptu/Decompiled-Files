/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class CompassFormat
extends NumberFormat {
    private static final String N = "N";
    private static final String E = "E";
    private static final String S = "S";
    private static final String W = "W";
    public static final String[] DIRECTIONS = new String[]{"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"};

    public String getDirectionCode(double direction) {
        if ((direction %= 360.0) < 0.0) {
            direction += 360.0;
        }
        int index = ((int)Math.floor(direction / 11.25) + 1) / 2;
        return DIRECTIONS[index];
    }

    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        return toAppendTo.append(this.getDirectionCode(number));
    }

    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        return toAppendTo.append(this.getDirectionCode(number));
    }

    public Number parse(String source, ParsePosition parsePosition) {
        return null;
    }
}

