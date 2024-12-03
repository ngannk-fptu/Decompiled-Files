/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format.number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.format.number.AbstractNumberFormatter;

public class PercentStyleFormatter
extends AbstractNumberFormatter {
    @Override
    protected NumberFormat getNumberFormat(Locale locale) {
        NumberFormat format = NumberFormat.getPercentInstance(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat)format).setParseBigDecimal(true);
        }
        return format;
    }
}

