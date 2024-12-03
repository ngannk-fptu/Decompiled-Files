/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import com.ibm.icu.message2.FormattedPlaceholder;
import com.ibm.icu.message2.OptUtils;
import com.ibm.icu.message2.Selector;
import com.ibm.icu.message2.SelectorFactory;
import com.ibm.icu.number.FormattedNumber;
import com.ibm.icu.text.FormattedValue;
import com.ibm.icu.text.PluralRules;
import java.util.Locale;
import java.util.Map;

class PluralSelectorFactory
implements SelectorFactory {
    private final PluralRules.PluralType pluralType;

    PluralSelectorFactory(String type) {
        switch (type) {
            case "ordinal": {
                this.pluralType = PluralRules.PluralType.ORDINAL;
                break;
            }
            default: {
                this.pluralType = PluralRules.PluralType.CARDINAL;
            }
        }
    }

    @Override
    public Selector createSelector(Locale locale, Map<String, Object> fixedOptions) {
        PluralRules rules = PluralRules.forLocale(locale, this.pluralType);
        return new PluralSelectorImpl(rules, fixedOptions);
    }

    private static class PluralSelectorImpl
    implements Selector {
        private final PluralRules rules;
        private Map<String, Object> fixedOptions;

        private PluralSelectorImpl(PluralRules rules, Map<String, Object> fixedOptions) {
            this.rules = rules;
            this.fixedOptions = fixedOptions;
        }

        @Override
        public boolean matches(Object value, String key, Map<String, Object> variableOptions) {
            if (value == null) {
                return false;
            }
            if ("*".equals(key)) {
                return true;
            }
            Integer offset = OptUtils.getInteger(variableOptions, "offset");
            if (offset == null && this.fixedOptions != null) {
                offset = OptUtils.getInteger(this.fixedOptions, "offset");
            }
            if (offset == null) {
                offset = 0;
            }
            double valToCheck = Double.MIN_VALUE;
            FormattedValue formattedValToCheck = null;
            if (value instanceof FormattedPlaceholder) {
                FormattedPlaceholder fph = (FormattedPlaceholder)value;
                value = fph.getInput();
                formattedValToCheck = fph.getFormattedValue();
            }
            if (value instanceof Double) {
                valToCheck = (Double)value;
            } else if (value instanceof Integer) {
                valToCheck = ((Integer)value).intValue();
            } else {
                return false;
            }
            if (!this.fixedOptions.containsKey("skeleton") && !variableOptions.containsKey("skeleton")) {
                try {
                    if (Double.parseDouble(key) == valToCheck) {
                        return true;
                    }
                }
                catch (NumberFormatException fph) {
                    // empty catch block
                }
            }
            String match = formattedValToCheck instanceof FormattedNumber ? this.rules.select((FormattedNumber)formattedValToCheck) : this.rules.select(valToCheck - (double)offset.intValue());
            return match.equals(key);
        }
    }
}

