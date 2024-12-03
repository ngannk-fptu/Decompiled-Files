/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Multimap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.embedded.impl;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;

public final class IdentifierUtils {
    private static Locale IDENTIFIER_COMPARE_LOCALE;
    public static final Function<String, String> TO_LOWER_CASE;

    private IdentifierUtils() {
    }

    public static void prepareIdentifierCompareLocale() {
        String preferredLang = System.getProperty("crowd.identifier.language");
        IDENTIFIER_COMPARE_LOCALE = StringUtils.isNotBlank((CharSequence)preferredLang) ? new Locale(preferredLang) : Locale.ENGLISH;
    }

    public static String toLowerCase(String identifier) {
        return identifier == null ? null : identifier.toLowerCase(IDENTIFIER_COMPARE_LOCALE);
    }

    public static Set<String> toLowerCase(Collection<? extends String> identifiers) {
        HashSet<String> lowerCased = new HashSet<String>(identifiers.size());
        for (String string : identifiers) {
            lowerCased.add(IdentifierUtils.toLowerCase(string));
        }
        return lowerCased;
    }

    public static Predicate<String> containsIdentifierPredicate(Collection<String> identifiers) {
        if (identifiers.isEmpty()) {
            return s -> false;
        }
        Set<String> lowerCaseIds = IdentifierUtils.toLowerCase(identifiers);
        return name -> lowerCaseIds.contains(IdentifierUtils.toLowerCase(name));
    }

    public static int compareToInLowerCase(String identifier1, String identifier2) {
        return IdentifierUtils.toLowerCase(identifier1).compareTo(IdentifierUtils.toLowerCase(identifier2));
    }

    public static boolean equalsInLowerCase(String identifier1, String identifier2) {
        if (identifier1 == null) {
            return identifier2 == null;
        }
        return identifier2 != null && IdentifierUtils.compareToInLowerCase(identifier1, identifier2) == 0;
    }

    public static boolean hasLeadingOrTrailingWhitespace(String s) {
        return !s.equals(s.trim());
    }

    public static BiPredicate<String, String> containsIdentifierBiPredicate(Multimap<String, String> multimap) {
        HashMap predicateMap = new HashMap();
        multimap.asMap().forEach((k, v) -> predicateMap.put(IdentifierUtils.toLowerCase(k), IdentifierUtils.containsIdentifierPredicate(v)));
        return (key, value) -> predicateMap.getOrDefault(IdentifierUtils.toLowerCase(key), name -> false).test((String)value);
    }

    static {
        IdentifierUtils.prepareIdentifierCompareLocale();
        TO_LOWER_CASE = IdentifierUtils::toLowerCase;
    }
}

