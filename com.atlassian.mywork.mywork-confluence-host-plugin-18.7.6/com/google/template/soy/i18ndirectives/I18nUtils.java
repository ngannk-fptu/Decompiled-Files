/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.i18ndirectives;

import com.ibm.icu.util.ULocale;
import java.util.Locale;

class I18nUtils {
    private I18nUtils() {
    }

    public static Locale parseLocale(String localeString) {
        if (localeString == null) {
            return Locale.US;
        }
        String[] groups = localeString.split("[-_]");
        switch (groups.length) {
            case 1: {
                return new Locale(groups[0]);
            }
            case 2: {
                return new Locale(groups[0], groups[1].toUpperCase());
            }
            case 3: {
                return new Locale(groups[0], groups[1].toUpperCase(), groups[2]);
            }
        }
        throw new IllegalArgumentException("Malformed localeString: " + localeString);
    }

    public static ULocale parseULocale(String localeString) {
        return ULocale.forLocale(I18nUtils.parseLocale(localeString));
    }
}

