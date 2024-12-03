/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import com.ibm.icu.impl.locale.AsciiUtil;
import com.ibm.icu.message2.FormattedPlaceholder;
import com.ibm.icu.message2.Formatter;
import com.ibm.icu.message2.FormatterFactory;
import com.ibm.icu.message2.PlainStringFormattedValue;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

class DateTimeFormatterFactory
implements FormatterFactory {
    DateTimeFormatterFactory() {
    }

    private static int stringToStyle(String option) {
        switch (AsciiUtil.toUpperString(option)) {
            case "FULL": {
                return 0;
            }
            case "LONG": {
                return 1;
            }
            case "MEDIUM": {
                return 2;
            }
            case "SHORT": {
                return 3;
            }
            case "": 
            case "DEFAULT": {
                return 2;
            }
        }
        throw new IllegalArgumentException("Invalid datetime style: " + option);
    }

    @Override
    public Formatter createFormatter(Locale locale, Map<String, Object> fixedOptions) {
        Object opt = fixedOptions.get("skeleton");
        if (opt != null) {
            String skeleton = Objects.toString(opt);
            DateFormat df = DateFormat.getInstanceForSkeleton(skeleton, locale);
            return new DateTimeFormatter(df);
        }
        opt = fixedOptions.get("pattern");
        if (opt != null) {
            String pattern = Objects.toString(opt);
            SimpleDateFormat sf = new SimpleDateFormat(pattern, locale);
            return new DateTimeFormatter(sf);
        }
        int dateStyle = -1;
        opt = fixedOptions.get("datestyle");
        if (opt != null) {
            dateStyle = DateTimeFormatterFactory.stringToStyle(Objects.toString(opt, ""));
        }
        int timeStyle = -1;
        opt = fixedOptions.get("timestyle");
        if (opt != null) {
            timeStyle = DateTimeFormatterFactory.stringToStyle(Objects.toString(opt, ""));
        }
        if (dateStyle == -1 && timeStyle == -1) {
            dateStyle = 3;
            timeStyle = 3;
        }
        DateFormat df = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
        return new DateTimeFormatter(df);
    }

    private static class DateTimeFormatter
    implements Formatter {
        private final DateFormat icuFormatter;

        private DateTimeFormatter(DateFormat df) {
            this.icuFormatter = df;
        }

        @Override
        public FormattedPlaceholder format(Object toFormat, Map<String, Object> variableOptions) {
            if (toFormat == null) {
                throw new IllegalArgumentException("The date to format can't be null");
            }
            String result = this.icuFormatter.format(toFormat);
            return new FormattedPlaceholder(toFormat, new PlainStringFormattedValue(result));
        }

        @Override
        public String formatToString(Object toFormat, Map<String, Object> variableOptions) {
            return this.format(toFormat, variableOptions).toString();
        }
    }
}

