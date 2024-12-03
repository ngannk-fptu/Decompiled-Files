/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import com.ibm.icu.math.BigDecimal;
import com.ibm.icu.message2.FormattedPlaceholder;
import com.ibm.icu.message2.Formatter;
import com.ibm.icu.message2.FormatterFactory;
import com.ibm.icu.message2.OptUtils;
import com.ibm.icu.message2.PlainStringFormattedValue;
import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;
import com.ibm.icu.number.UnlocalizedNumberFormatter;
import com.ibm.icu.text.FormattedValue;
import com.ibm.icu.util.CurrencyAmount;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

class NumberFormatterFactory
implements FormatterFactory {
    NumberFormatterFactory() {
    }

    @Override
    public Formatter createFormatter(Locale locale, Map<String, Object> fixedOptions) {
        return new NumberFormatterImpl(locale, fixedOptions);
    }

    static class NumberFormatterImpl
    implements Formatter {
        private final Locale locale;
        private final Map<String, Object> fixedOptions;
        private final LocalizedNumberFormatter icuFormatter;
        final boolean advanced;

        private static LocalizedNumberFormatter formatterForOptions(Locale locale, Map<String, Object> fixedOptions) {
            UnlocalizedNumberFormatter nf;
            String skeleton = OptUtils.getString(fixedOptions, "skeleton");
            if (skeleton != null) {
                nf = NumberFormatter.forSkeleton(skeleton);
            } else {
                nf = NumberFormatter.with();
                Integer minFractionDigits = OptUtils.getInteger(fixedOptions, "minimumFractionDigits");
                if (minFractionDigits != null) {
                    nf = (UnlocalizedNumberFormatter)nf.precision(Precision.minFraction(minFractionDigits));
                }
            }
            return nf.locale(locale);
        }

        NumberFormatterImpl(Locale locale, Map<String, Object> fixedOptions) {
            this.locale = locale;
            this.fixedOptions = new HashMap<String, Object>(fixedOptions);
            String skeleton = OptUtils.getString(fixedOptions, "skeleton");
            boolean fancy = skeleton != null;
            this.icuFormatter = NumberFormatterImpl.formatterForOptions(locale, fixedOptions);
            this.advanced = fancy;
        }

        LocalizedNumberFormatter getIcuFormatter() {
            return this.icuFormatter;
        }

        @Override
        public String formatToString(Object toFormat, Map<String, Object> variableOptions) {
            return this.format(toFormat, variableOptions).toString();
        }

        @Override
        public FormattedPlaceholder format(Object toFormat, Map<String, Object> variableOptions) {
            LocalizedNumberFormatter realFormatter;
            if (variableOptions.isEmpty()) {
                realFormatter = this.icuFormatter;
            } else {
                HashMap<String, Object> mergedOptions = new HashMap<String, Object>(this.fixedOptions);
                mergedOptions.putAll(variableOptions);
                realFormatter = NumberFormatterImpl.formatterForOptions(this.locale, mergedOptions);
            }
            Integer offset = OptUtils.getInteger(variableOptions, "offset");
            if (offset == null && this.fixedOptions != null) {
                offset = OptUtils.getInteger(this.fixedOptions, "offset");
            }
            if (offset == null) {
                offset = 0;
            }
            FormattedValue result = null;
            if (toFormat == null) {
                throw new NullPointerException("Argument to format can't be null");
            }
            if (toFormat instanceof Double) {
                result = realFormatter.format((Double)toFormat - (double)offset.intValue());
            } else if (toFormat instanceof Long) {
                result = realFormatter.format((Long)toFormat - (long)offset.intValue());
            } else if (toFormat instanceof Integer) {
                result = realFormatter.format((Integer)toFormat - offset);
            } else if (toFormat instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal)toFormat;
                result = realFormatter.format(bd.subtract(BigDecimal.valueOf(offset.intValue())));
            } else {
                String strValue;
                Number nrValue;
                result = toFormat instanceof Number ? realFormatter.format(((Number)toFormat).doubleValue() - (double)offset.intValue()) : (toFormat instanceof CurrencyAmount ? realFormatter.format((CurrencyAmount)toFormat) : ((nrValue = OptUtils.asNumber(strValue = Objects.toString(toFormat))) != null ? realFormatter.format(nrValue.doubleValue() - (double)offset.intValue()) : new PlainStringFormattedValue("NaN")));
            }
            return new FormattedPlaceholder(toFormat, result);
        }
    }
}

