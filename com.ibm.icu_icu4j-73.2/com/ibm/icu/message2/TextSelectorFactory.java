/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import com.ibm.icu.message2.Selector;
import com.ibm.icu.message2.SelectorFactory;
import java.util.Locale;
import java.util.Map;

class TextSelectorFactory
implements SelectorFactory {
    TextSelectorFactory() {
    }

    @Override
    public Selector createSelector(Locale locale, Map<String, Object> fixedOptions) {
        return new TextSelector();
    }

    private static class TextSelector
    implements Selector {
        private TextSelector() {
        }

        @Override
        public boolean matches(Object value, String key, Map<String, Object> variableOptions) {
            if ("*".equals(key)) {
                return true;
            }
            return key.equals(value);
        }
    }
}

