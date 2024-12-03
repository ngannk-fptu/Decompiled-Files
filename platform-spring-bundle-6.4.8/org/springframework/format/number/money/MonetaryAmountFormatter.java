/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.money.MonetaryAmount
 *  javax.money.format.MonetaryAmountFormat
 *  javax.money.format.MonetaryFormats
 */
package org.springframework.format.number.money;

import java.util.Locale;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import org.springframework.format.Formatter;
import org.springframework.lang.Nullable;

public class MonetaryAmountFormatter
implements Formatter<MonetaryAmount> {
    @Nullable
    private String formatName;

    public MonetaryAmountFormatter() {
    }

    public MonetaryAmountFormatter(String formatName) {
        this.formatName = formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    @Override
    public String print(MonetaryAmount object, Locale locale) {
        return this.getMonetaryAmountFormat(locale).format(object);
    }

    @Override
    public MonetaryAmount parse(String text, Locale locale) {
        return this.getMonetaryAmountFormat(locale).parse((CharSequence)text);
    }

    protected MonetaryAmountFormat getMonetaryAmountFormat(Locale locale) {
        if (this.formatName != null) {
            return MonetaryFormats.getAmountFormat((String)this.formatName, (String[])new String[0]);
        }
        return MonetaryFormats.getAmountFormat((Locale)locale, (String[])new String[0]);
    }
}

