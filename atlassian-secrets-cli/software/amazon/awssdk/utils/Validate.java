/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.StringUtils;

@SdkProtectedApi
public final class Validate {
    private static final String DEFAULT_IS_NULL_EX_MESSAGE = "The validated object is null";

    private Validate() {
    }

    public static void isTrue(boolean expression, String message, Object ... values) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isFalse(boolean expression, String message, Object ... values) {
        Validate.isTrue(!expression, message, values);
    }

    public static <T> T notNull(T object, String message, Object ... values) {
        if (object == null) {
            throw new NullPointerException(String.format(message, values));
        }
        return object;
    }

    public static <T> void isNull(T object, String message, Object ... values) {
        if (object != null) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static <T> T paramNotNull(T object, String paramName) {
        if (object == null) {
            throw new NullPointerException(String.format("%s must not be null.", paramName));
        }
        return object;
    }

    public static <T extends CharSequence> T paramNotBlank(T chars, String paramName) {
        if (chars == null) {
            throw new NullPointerException(String.format("%s must not be null.", paramName));
        }
        if (StringUtils.isBlank(chars)) {
            throw new IllegalArgumentException(String.format("%s must not be blank or empty.", paramName));
        }
        return chars;
    }

    public static <T> T validState(T object, Predicate<T> test, String message, Object ... values) {
        if (!test.test(object)) {
            throw new IllegalStateException(String.format(message, values));
        }
        return object;
    }

    public static <T> T[] notEmpty(T[] array, String message, Object ... values) {
        if (array == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (array.length == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return array;
    }

    public static <T extends Collection<?>> T notEmpty(T collection, String message, Object ... values) {
        if (collection == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return collection;
    }

    public static <T extends Map<?, ?>> T notEmpty(T map, String message, Object ... values) {
        if (map == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return map;
    }

    public static <T extends CharSequence> T notEmpty(T chars, String message, Object ... values) {
        if (chars == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (chars.length() == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return chars;
    }

    public static <T extends CharSequence> T notBlank(T chars, String message, Object ... values) {
        if (chars == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (StringUtils.isBlank(chars)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return chars;
    }

    public static <T> T[] noNullElements(T[] array, String message, Object ... values) {
        Validate.notNull(array, message, new Object[0]);
        for (T anArray : array) {
            if (anArray != null) continue;
            throw new IllegalArgumentException(String.format(message, values));
        }
        return array;
    }

    public static <T extends Iterable<?>> T noNullElements(T iterable, String message, Object ... values) {
        Validate.notNull(iterable, DEFAULT_IS_NULL_EX_MESSAGE, new Object[0]);
        int i = 0;
        Iterator<?> it = iterable.iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                throw new IllegalArgumentException(String.format(message, values));
            }
            ++i;
        }
        return iterable;
    }

    public static void validState(boolean expression, String message, Object ... values) {
        if (!expression) {
            throw new IllegalStateException(String.format(message, values));
        }
    }

    public static <T extends Comparable<U>, U> T inclusiveBetween(U start, U end, T value, String message, Object ... values) {
        if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return (T)value;
    }

    public static long inclusiveBetween(long start, long end, long value, String message) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static double inclusiveBetween(double start, double end, double value, String message) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static <T extends Comparable<U>, U> T exclusiveBetween(U start, U end, T value, String message, Object ... values) {
        if (value.compareTo(start) <= 0 || value.compareTo(end) >= 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return (T)value;
    }

    public static long exclusiveBetween(long start, long end, long value, String message) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static double exclusiveBetween(double start, double end, double value, String message) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static <T, U> U isInstanceOf(Class<U> type, T obj, String message, Object ... values) {
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return type.cast(obj);
    }

    public static <T> Class<? extends T> isAssignableFrom(Class<T> superType, Class<?> type, String message, Object ... values) {
        if (!superType.isAssignableFrom(type)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return type;
    }

    public static int isPositive(int num, String fieldName) {
        if (num <= 0) {
            throw new IllegalArgumentException(String.format("%s must be positive", fieldName));
        }
        return num;
    }

    public static long isPositive(long num, String fieldName) {
        if (num <= 0L) {
            throw new IllegalArgumentException(String.format("%s must be positive", fieldName));
        }
        return num;
    }

    public static double isPositive(double num, String fieldName) {
        if (num <= 0.0) {
            throw new IllegalArgumentException(String.format("%s must be positive", fieldName));
        }
        return num;
    }

    public static int isNotNegative(int num, String fieldName) {
        if (num < 0) {
            throw new IllegalArgumentException(String.format("%s must not be negative", fieldName));
        }
        return num;
    }

    public static Long isNotNegativeOrNull(Long num, String fieldName) {
        if (num == null) {
            return null;
        }
        if (num < 0L) {
            throw new IllegalArgumentException(String.format("%s must not be negative", fieldName));
        }
        return num;
    }

    public static long isNotNegative(long num, String fieldName) {
        if (num < 0L) {
            throw new IllegalArgumentException(String.format("%s must not be negative", fieldName));
        }
        return num;
    }

    public static Duration isPositive(Duration duration, String fieldName) {
        if (duration == null) {
            throw new IllegalArgumentException(String.format("%s cannot be null", fieldName));
        }
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException(String.format("%s must be positive", fieldName));
        }
        return duration;
    }

    public static Duration isPositiveOrNull(Duration duration, String fieldName) {
        if (duration == null) {
            return null;
        }
        return Validate.isPositive(duration, fieldName);
    }

    public static Integer isPositiveOrNull(Integer num, String fieldName) {
        if (num == null) {
            return null;
        }
        return Validate.isPositive(num, fieldName);
    }

    public static Double isPositiveOrNull(Double num, String fieldName) {
        if (num == null) {
            return null;
        }
        return Validate.isPositive(num, fieldName);
    }

    public static Long isPositiveOrNull(Long num, String fieldName) {
        if (num == null) {
            return null;
        }
        return Validate.isPositive(num, fieldName);
    }

    public static Duration isNotNegative(Duration duration, String fieldName) {
        if (duration == null) {
            throw new IllegalArgumentException(String.format("%s cannot be null", fieldName));
        }
        if (duration.isNegative()) {
            throw new IllegalArgumentException(String.format("%s must not be negative", fieldName));
        }
        return duration;
    }

    public static <T> T getOrDefault(T param, Supplier<T> defaultValue) {
        Validate.paramNotNull(defaultValue, "defaultValue");
        return param != null ? param : defaultValue.get();
    }

    public static void mutuallyExclusive(String message, Object ... objs) {
        boolean oneProvided = false;
        for (Object o : objs) {
            if (o == null) continue;
            if (oneProvided) {
                throw new IllegalArgumentException(message);
            }
            oneProvided = true;
        }
    }
}

