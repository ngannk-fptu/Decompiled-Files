/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import com.ibm.icu.message2.FormattedPlaceholder;
import com.ibm.icu.message2.Formatter;
import com.ibm.icu.message2.FormatterFactory;
import com.ibm.icu.message2.PlainStringFormattedValue;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

class IdentityFormatterFactory
implements FormatterFactory {
    IdentityFormatterFactory() {
    }

    @Override
    public Formatter createFormatter(Locale locale, Map<String, Object> fixedOptions) {
        return new IdentityFormatterImpl();
    }

    private static class IdentityFormatterImpl
    implements Formatter {
        private IdentityFormatterImpl() {
        }

        @Override
        public FormattedPlaceholder format(Object toFormat, Map<String, Object> variableOptions) {
            return new FormattedPlaceholder(toFormat, new PlainStringFormattedValue(Objects.toString(toFormat)));
        }

        @Override
        public String formatToString(Object toFormat, Map<String, Object> variableOptions) {
            return this.format(toFormat, variableOptions).toString();
        }
    }
}

