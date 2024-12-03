/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.checker.formatter.qual;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

public enum ConversionCategory {
    GENERAL("bBhHsS", null),
    CHAR("cC", Character.class, Byte.class, Short.class, Integer.class),
    INT("doxX", Byte.class, Short.class, Integer.class, Long.class, BigInteger.class),
    FLOAT("eEfgGaA", Float.class, Double.class, BigDecimal.class),
    TIME("tT", Long.class, Calendar.class, Date.class),
    CHAR_AND_INT(null, Byte.class, Short.class, Integer.class),
    INT_AND_TIME(null, Long.class),
    NULL(null, new Class[0]),
    UNUSED(null, null);

    public final Class<?> @Nullable [] types;
    public final @Nullable String chars;
    private static final ConversionCategory[] conversionCategoriesWithChar;
    private static final ConversionCategory[] conversionCategoriesForIntersect;
    private static final ConversionCategory[] conversionCategoriesForUnion;

    private ConversionCategory(String chars, Class<?> ... types) {
        this.chars = chars;
        if (types == null) {
            this.types = types;
        } else {
            ArrayList typesWithPrimitives = new ArrayList(types.length);
            for (Class<?> type : types) {
                typesWithPrimitives.add(type);
                Class<? extends Object> unwrapped = ConversionCategory.unwrapPrimitive(type);
                if (unwrapped == null) continue;
                typesWithPrimitives.add(unwrapped);
            }
            this.types = typesWithPrimitives.toArray(new Class[typesWithPrimitives.size()]);
        }
    }

    private static @Nullable Class<? extends Object> unwrapPrimitive(Class<?> c) {
        if (c == Byte.class) {
            return Byte.TYPE;
        }
        if (c == Character.class) {
            return Character.TYPE;
        }
        if (c == Short.class) {
            return Short.TYPE;
        }
        if (c == Integer.class) {
            return Integer.TYPE;
        }
        if (c == Long.class) {
            return Long.TYPE;
        }
        if (c == Float.class) {
            return Float.TYPE;
        }
        if (c == Double.class) {
            return Double.TYPE;
        }
        if (c == Boolean.class) {
            return Boolean.TYPE;
        }
        return null;
    }

    public static ConversionCategory fromConversionChar(char c) {
        for (ConversionCategory v : conversionCategoriesWithChar) {
            if (!v.chars.contains(String.valueOf(c))) continue;
            return v;
        }
        throw new IllegalArgumentException("Bad conversion character " + c);
    }

    private static <E> Set<E> arrayToSet(E[] a) {
        return new HashSet<E>(Arrays.asList(a));
    }

    public static boolean isSubsetOf(ConversionCategory a, ConversionCategory b) {
        return ConversionCategory.intersect(a, b) == a;
    }

    public static ConversionCategory intersect(ConversionCategory a, ConversionCategory b) {
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
        Set<Class<Class<?>>> as = ConversionCategory.arrayToSet(a.types);
        Set<Class<?>> bs = ConversionCategory.arrayToSet(b.types);
        as.retainAll(bs);
        for (ConversionCategory v : conversionCategoriesForIntersect) {
            Set<Class<?>> vs = ConversionCategory.arrayToSet(v.types);
            if (!vs.equals(as)) continue;
            return v;
        }
        throw new RuntimeException();
    }

    public static ConversionCategory union(ConversionCategory a, ConversionCategory b) {
        if (a == UNUSED || b == UNUSED) {
            return UNUSED;
        }
        if (a == GENERAL || b == GENERAL) {
            return GENERAL;
        }
        if (a == CHAR_AND_INT && b == INT_AND_TIME || a == INT_AND_TIME && b == CHAR_AND_INT) {
            return INT;
        }
        Set<Class<?>> as = ConversionCategory.arrayToSet(a.types);
        Set<Class<?>> bs = ConversionCategory.arrayToSet(b.types);
        as.addAll(bs);
        for (ConversionCategory v : conversionCategoriesForUnion) {
            Set<Class<?>> vs = ConversionCategory.arrayToSet(v.types);
            if (!vs.equals(as)) continue;
            return v;
        }
        return GENERAL;
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

    @Pure
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name());
        sb.append(" conversion category");
        if (this.types == null || this.types.length == 0) {
            return sb.toString();
        }
        StringJoiner sj = new StringJoiner(", ", "(one of: ", ")");
        for (Class<?> cls : this.types) {
            sj.add(cls.getSimpleName());
        }
        sb.append(" ");
        sb.append(sj);
        return sb.toString();
    }

    static {
        conversionCategoriesWithChar = new ConversionCategory[]{GENERAL, CHAR, INT, FLOAT, TIME};
        conversionCategoriesForIntersect = new ConversionCategory[]{CHAR, INT, FLOAT, TIME, CHAR_AND_INT, INT_AND_TIME, NULL};
        conversionCategoriesForUnion = new ConversionCategory[]{NULL, CHAR_AND_INT, INT_AND_TIME, CHAR, INT, FLOAT, TIME};
    }
}

