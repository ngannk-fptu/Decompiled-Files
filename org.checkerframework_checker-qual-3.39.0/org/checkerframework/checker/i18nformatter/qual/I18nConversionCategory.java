/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.checker.i18nformatter.qual;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum I18nConversionCategory {
    UNUSED(null, null),
    GENERAL(null, null),
    DATE(new Class[]{Date.class, Number.class}, new String[]{"date", "time"}),
    NUMBER(new Class[]{Number.class}, new String[]{"number", "choice"});

    public final Class<?> @Nullable [] types;
    public final String @Nullable [] strings;
    private static final I18nConversionCategory[] namedCategories;
    private static final I18nConversionCategory[] conversionCategoriesForIntersect;

    private I18nConversionCategory(Class<?>[] types, String[] strings) {
        this.types = types;
        this.strings = strings;
    }

    public static I18nConversionCategory stringToI18nConversionCategory(String string) {
        string = string.toLowerCase();
        for (I18nConversionCategory v : namedCategories) {
            for (String s2 : v.strings) {
                if (!s2.equals(string)) continue;
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid format type " + string);
    }

    private static <E> Set<E> arrayToSet(E[] a) {
        return new HashSet<E>(Arrays.asList(a));
    }

    public static boolean isSubsetOf(I18nConversionCategory a, I18nConversionCategory b) {
        return I18nConversionCategory.intersect(a, b) == a;
    }

    public static I18nConversionCategory intersect(I18nConversionCategory a, I18nConversionCategory b) {
        if (a == UNUSED) {
            return b;
        }
        if (b == UNUSED) {
            return a;
        }
        if (a == GENERAL) {
            return b;
        }
        if (b == GENERAL) {
            return a;
        }
        Set<Class<Class<?>>> as = I18nConversionCategory.arrayToSet(a.types);
        Set<Class<?>> bs = I18nConversionCategory.arrayToSet(b.types);
        as.retainAll(bs);
        for (I18nConversionCategory v : conversionCategoriesForIntersect) {
            Set<Class<?>> vs = I18nConversionCategory.arrayToSet(v.types);
            if (!vs.equals(as)) continue;
            return v;
        }
        throw new RuntimeException();
    }

    public static I18nConversionCategory union(I18nConversionCategory a, I18nConversionCategory b) {
        if (a == UNUSED || b == UNUSED) {
            return UNUSED;
        }
        if (a == GENERAL || b == GENERAL) {
            return GENERAL;
        }
        if (a == DATE || b == DATE) {
            return DATE;
        }
        return NUMBER;
    }

    public boolean isAssignableFrom(Class<?> argType) {
        if (this.types == null) {
            return true;
        }
        if (argType == Void.TYPE) {
            return true;
        }
        for (Class<?> c : this.types) {
            if (!c.isAssignableFrom(argType)) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.name());
        if (this.types == null) {
            sb.append(" conversion category (all types)");
        } else {
            StringJoiner sj = new StringJoiner(", ", " conversion category (one of: ", ")");
            for (Class<?> cls : this.types) {
                sj.add(cls.getCanonicalName());
            }
            sb.append(sj);
        }
        return sb.toString();
    }

    static {
        namedCategories = new I18nConversionCategory[]{DATE, NUMBER};
        conversionCategoriesForIntersect = new I18nConversionCategory[]{DATE, NUMBER};
    }
}

