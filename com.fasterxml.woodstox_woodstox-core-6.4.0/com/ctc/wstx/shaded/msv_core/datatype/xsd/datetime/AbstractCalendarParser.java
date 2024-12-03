/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.TimeZone;
import java.math.BigInteger;
import java.util.SimpleTimeZone;

abstract class AbstractCalendarParser {
    private final String format;
    private final String value;
    private final int flen;
    private final int vlen;
    private int fidx;
    protected int vidx;

    protected AbstractCalendarParser(String format, String value) {
        this.format = format;
        this.value = value;
        this.flen = format.length();
        this.vlen = value.length();
    }

    public void parse() throws IllegalArgumentException {
        block9: while (this.fidx < this.flen) {
            char fch;
            if ((fch = this.format.charAt(this.fidx++)) != '%') {
                this.skip(fch);
                continue;
            }
            switch (this.format.charAt(this.fidx++)) {
                case 'Y': {
                    int sign = 1;
                    if (this.peek() == '-') {
                        ++this.vidx;
                        sign = -1;
                    }
                    this.setYear(sign * this.parseInt(4, Integer.MAX_VALUE));
                    continue block9;
                }
                case 'M': {
                    this.setMonth(this.parseInt(2, 2));
                    continue block9;
                }
                case 'D': {
                    this.setDay(this.parseInt(2, 2));
                    continue block9;
                }
                case 'h': {
                    this.setHours(this.parseInt(2, 2));
                    continue block9;
                }
                case 'm': {
                    this.setMinutes(this.parseInt(2, 2));
                    continue block9;
                }
                case 's': {
                    this.setSeconds(this.parseInt(2, 2));
                    if (this.peek() != '.') continue block9;
                    ++this.vidx;
                    this.parseFractionSeconds();
                    continue block9;
                }
                case 'z': {
                    char vch = this.peek();
                    if (vch == 'Z') {
                        ++this.vidx;
                        this.setTimeZone(TimeZone.ZERO);
                        continue block9;
                    }
                    if (vch == '+' || vch == '-') {
                        ++this.vidx;
                        int h = this.parseInt(2, 2);
                        this.skip(':');
                        int m = this.parseInt(2, 2);
                        this.setTimeZone(new SimpleTimeZone((h * 60 + m) * (vch == '+' ? 1 : -1) * 60 * 1000, ""));
                        continue block9;
                    }
                    this.setTimeZone(TimeZone.MISSING);
                    continue block9;
                }
            }
            throw new InternalError();
        }
        if (this.vidx != this.vlen) {
            throw new IllegalArgumentException(this.value);
        }
    }

    private char peek() throws IllegalArgumentException {
        if (this.vidx == this.vlen) {
            return '\uffff';
        }
        return this.value.charAt(this.vidx);
    }

    private char read() throws IllegalArgumentException {
        if (this.vidx == this.vlen) {
            throw new IllegalArgumentException(this.value);
        }
        return this.value.charAt(this.vidx++);
    }

    private void skip(char ch) throws IllegalArgumentException {
        if (this.read() != ch) {
            throw new IllegalArgumentException(this.value);
        }
    }

    protected final void skipDigits() {
        while (AbstractCalendarParser.isDigit(this.peek())) {
            ++this.vidx;
        }
    }

    protected final int parseInt(int minDigits, int maxDigits) throws IllegalArgumentException {
        int vstart = this.vidx;
        while (AbstractCalendarParser.isDigit(this.peek()) && this.vidx - vstart < maxDigits) {
            ++this.vidx;
        }
        if (this.vidx - vstart < minDigits) {
            throw new IllegalArgumentException(this.value);
        }
        return Integer.parseInt(this.value.substring(vstart, this.vidx));
    }

    protected final BigInteger parseBigInteger(int minDigits, int maxDigits) throws IllegalArgumentException {
        int vstart = this.vidx;
        while (AbstractCalendarParser.isDigit(this.peek()) && this.vidx - vstart <= maxDigits) {
            ++this.vidx;
        }
        if (this.vidx - vstart < minDigits) {
            throw new IllegalArgumentException(this.value);
        }
        return new BigInteger(this.value.substring(vstart, this.vidx));
    }

    private static boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }

    protected abstract void parseFractionSeconds();

    protected abstract void setTimeZone(java.util.TimeZone var1);

    protected abstract void setSeconds(int var1);

    protected abstract void setMinutes(int var1);

    protected abstract void setHours(int var1);

    protected abstract void setDay(int var1);

    protected abstract void setMonth(int var1);

    protected abstract void setYear(int var1);
}

