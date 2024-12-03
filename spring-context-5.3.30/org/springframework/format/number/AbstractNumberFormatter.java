/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.number;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;
import org.springframework.format.Formatter;

public abstract class AbstractNumberFormatter
implements Formatter<Number> {
    private boolean lenient = false;

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    @Override
    public String print(Number number, Locale locale) {
        return this.getNumberFormat(locale).format(number);
    }

    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        NumberFormat format = this.getNumberFormat(locale);
        ParsePosition position = new ParsePosition(0);
        Number number = format.parse(text, position);
        if (position.getErrorIndex() != -1) {
            throw new ParseException(text, position.getIndex());
        }
        if (!this.lenient && text.length() != position.getIndex()) {
            throw new ParseException(text, position.getIndex());
        }
        return number;
    }

    protected abstract NumberFormat getNumberFormat(Locale var1);
}

