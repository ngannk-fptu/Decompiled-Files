/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.sal.usercompatibility;

import java.util.Locale;
import org.apache.commons.lang.StringUtils;

public final class IdentifierUtils {
    private static Locale IDENTIFIER_COMPARE_LOCALE;

    private IdentifierUtils() {
    }

    protected static void prepareIdentifierCompareLocale() {
        String preferredLang = System.getProperty("crowd.identifier.language");
        IDENTIFIER_COMPARE_LOCALE = StringUtils.isNotBlank((String)preferredLang) ? new Locale(preferredLang) : Locale.ENGLISH;
    }

    public static String toLowerCase(String identifier) {
        return identifier.toLowerCase(IDENTIFIER_COMPARE_LOCALE);
    }

    public static int compareToInLowerCase(String identifier1, String identifier2) {
        return IdentifierUtils.toLowerCase(identifier1).compareTo(IdentifierUtils.toLowerCase(identifier2));
    }

    public static boolean equalsInLowerCase(String identifier1, String identifier2) {
        return IdentifierUtils.compareToInLowerCase(identifier1, identifier2) == 0;
    }

    static {
        IdentifierUtils.prepareIdentifierCompareLocale();
    }
}

