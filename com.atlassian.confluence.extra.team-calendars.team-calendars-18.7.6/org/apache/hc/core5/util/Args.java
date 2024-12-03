/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.util.TextUtils;
import org.apache.hc.core5.util.TimeValue;

public class Args {
    public static void check(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void check(boolean expression, String message, Object ... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static void check(boolean expression, String message, Object arg) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, arg));
        }
    }

    @Deprecated
    public static long checkContentLength(EntityDetails entityDetails) {
        return Args.checkRange(entityDetails.getContentLength(), -1L, Integer.MAX_VALUE, "HTTP entity too large to be buffered in memory)");
    }

    public static int checkRange(int value, int lowInclusive, int highInclusive, String message) {
        if (value < lowInclusive || value > highInclusive) {
            throw Args.illegalArgumentException("%s: %d is out of range [%d, %d]", message, value, lowInclusive, highInclusive);
        }
        return value;
    }

    public static long checkRange(long value, long lowInclusive, long highInclusive, String message) {
        if (value < lowInclusive || value > highInclusive) {
            throw Args.illegalArgumentException("%s: %d is out of range [%d, %d]", message, value, lowInclusive, highInclusive);
        }
        return value;
    }

    public static <T extends CharSequence> T containsNoBlanks(T argument, String name) {
        Args.notNull(argument, name);
        if (Args.isEmpty(argument)) {
            throw Args.illegalArgumentExceptionNotEmpty(name);
        }
        if (TextUtils.containsBlanks(argument)) {
            throw new IllegalArgumentException(name + " must not contain blanks");
        }
        return argument;
    }

    private static IllegalArgumentException illegalArgumentException(String format, Object ... args) {
        return new IllegalArgumentException(String.format(format, args));
    }

    private static IllegalArgumentException illegalArgumentExceptionNotEmpty(String name) {
        return new IllegalArgumentException(name + " must not be empty");
    }

    private static NullPointerException NullPointerException(String name) {
        return new NullPointerException(name + " must not be null");
    }

    public static <T extends CharSequence> T notBlank(T argument, String name) {
        Args.notNull(argument, name);
        if (TextUtils.isBlank(argument)) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return argument;
    }

    public static <T extends CharSequence> T notEmpty(T argument, String name) {
        Args.notNull(argument, name);
        if (Args.isEmpty(argument)) {
            throw Args.illegalArgumentExceptionNotEmpty(name);
        }
        return argument;
    }

    public static <E, T extends Collection<E>> T notEmpty(T argument, String name) {
        Args.notNull(argument, name);
        if (Args.isEmpty(argument)) {
            throw Args.illegalArgumentExceptionNotEmpty(name);
        }
        return argument;
    }

    public static <T> T notEmpty(T argument, String name) {
        Args.notNull(argument, name);
        if (Args.isEmpty(argument)) {
            throw Args.illegalArgumentExceptionNotEmpty(name);
        }
        return argument;
    }

    public static int notNegative(int n, String name) {
        if (n < 0) {
            throw Args.illegalArgumentException("%s must not be negative: %d", name, n);
        }
        return n;
    }

    public static long notNegative(long n, String name) {
        if (n < 0L) {
            throw Args.illegalArgumentException("%s must not be negative: %d", name, n);
        }
        return n;
    }

    public static <T> T notNull(T argument, String name) {
        return Objects.requireNonNull(argument, name);
    }

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence)object).length() == 0;
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        }
        if (object instanceof Collection) {
            return ((Collection)object).isEmpty();
        }
        if (object instanceof Map) {
            return ((Map)object).isEmpty();
        }
        return false;
    }

    public static int positive(int n, String name) {
        if (n <= 0) {
            throw Args.illegalArgumentException("%s must not be negative or zero: %d", name, n);
        }
        return n;
    }

    public static long positive(long n, String name) {
        if (n <= 0L) {
            throw Args.illegalArgumentException("%s must not be negative or zero: %d", name, n);
        }
        return n;
    }

    public static <T extends TimeValue> T positive(T timeValue, String name) {
        Args.positive(timeValue.getDuration(), name);
        return timeValue;
    }

    private Args() {
    }
}

