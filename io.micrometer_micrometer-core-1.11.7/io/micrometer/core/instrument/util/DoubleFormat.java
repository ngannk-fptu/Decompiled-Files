/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public final class DoubleFormat {
    private static final ThreadLocal<NumberFormat> DECIMAL_OR_NAN = ThreadLocal.withInitial(() -> {
        NumberFormat numberFormatter = NumberFormat.getInstance(Locale.US);
        numberFormatter.setGroupingUsed(false);
        numberFormatter.setMaximumFractionDigits(6);
        if (numberFormatter instanceof DecimalFormat) {
            DecimalFormat decimalFormat = (DecimalFormat)numberFormatter;
            DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
            symbols.setNaN("NaN");
            decimalFormat.setDecimalFormatSymbols(symbols);
        }
        return numberFormatter;
    });
    private static final ThreadLocal<DecimalFormat> WHOLE_OR_DECIMAL = ThreadLocal.withInitial(() -> {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("##0.######", otherSymbols);
    });
    private static final ThreadLocal<DecimalFormat> DECIMAL = ThreadLocal.withInitial(() -> {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("##0.0#####", otherSymbols);
    });

    private DoubleFormat() {
    }

    public static String decimalOrNan(double d) {
        return DECIMAL_OR_NAN.get().format(d);
    }

    @Deprecated
    public static String decimalOrWhole(double d) {
        return WHOLE_OR_DECIMAL.get().format(d);
    }

    public static String decimal(double d) {
        return DECIMAL.get().format(d);
    }

    public static String wholeOrDecimal(double d) {
        return WHOLE_OR_DECIMAL.get().format(d);
    }
}

