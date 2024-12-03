/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

public final class Numbers {
    private Numbers() {
    }

    public static boolean equal(Number lhs, Number rhs) {
        Class<?> lhsClass = lhs.getClass();
        Class<?> rhsClass = rhs.getClass();
        assert (lhsClass != rhsClass);
        if (Numbers.isDoubleRepresentable(lhsClass)) {
            if (Numbers.isDoubleRepresentable(rhsClass)) {
                return Numbers.equalDoubles(lhs.doubleValue(), rhs.doubleValue());
            }
            if (Numbers.isLongRepresentable(rhsClass)) {
                return Numbers.equalLongAndDouble(rhs.longValue(), lhs.doubleValue());
            }
        } else if (Numbers.isLongRepresentable(lhsClass)) {
            if (Numbers.isDoubleRepresentable(rhsClass)) {
                return Numbers.equalLongAndDouble(lhs.longValue(), rhs.doubleValue());
            }
            if (Numbers.isLongRepresentable(rhsClass)) {
                return lhs.longValue() == rhs.longValue();
            }
        }
        return lhs.equals(rhs);
    }

    public static int compare(Comparable lhs, Comparable rhs) {
        Class<?> lhsClass = lhs.getClass();
        Class<?> rhsClass = rhs.getClass();
        assert (lhsClass != rhsClass);
        assert (lhs instanceof Number);
        assert (rhs instanceof Number);
        Number lhsNumber = (Number)((Object)lhs);
        Number rhsNumber = (Number)((Object)rhs);
        if (Numbers.isDoubleRepresentable(lhsClass)) {
            if (Numbers.isDoubleRepresentable(rhsClass)) {
                return Double.compare(lhsNumber.doubleValue(), rhsNumber.doubleValue());
            }
            if (Numbers.isLongRepresentable(rhsClass)) {
                return -Integer.signum(Numbers.compareLongWithDouble(rhsNumber.longValue(), lhsNumber.doubleValue()));
            }
        } else if (Numbers.isLongRepresentable(lhsClass)) {
            if (Numbers.isDoubleRepresentable(rhsClass)) {
                return Numbers.compareLongWithDouble(lhsNumber.longValue(), rhsNumber.doubleValue());
            }
            if (Numbers.isLongRepresentable(rhsClass)) {
                return Numbers.compareLongs(lhsNumber.longValue(), rhsNumber.longValue());
            }
        }
        return lhs.compareTo(rhs);
    }

    public static Comparable canonicalizeForHashLookup(Comparable value) {
        Class<?> clazz = value.getClass();
        assert (value instanceof Number);
        Number number = (Number)((Object)value);
        if (Numbers.isDoubleRepresentable(clazz)) {
            long longValue;
            double doubleValue = number.doubleValue();
            if (Numbers.equalDoubles(doubleValue, longValue = number.longValue())) {
                return Long.valueOf(longValue);
            }
            if (clazz == Float.class) {
                return Double.valueOf(doubleValue);
            }
        } else if (Numbers.isLongRepresentableExceptLong(clazz)) {
            return Long.valueOf(number.longValue());
        }
        return value;
    }

    public static boolean equalDoubles(double lhs, double rhs) {
        return Double.doubleToLongBits(lhs) == Double.doubleToLongBits(rhs);
    }

    public static boolean equalFloats(float lhs, float rhs) {
        return Float.floatToIntBits(lhs) == Float.floatToIntBits(rhs);
    }

    public static double asDoubleExactly(Number number) {
        Class<?> clazz = number.getClass();
        if (Numbers.isDoubleRepresentable(clazz) || Numbers.isLongRepresentableExceptLong(clazz)) {
            return number.doubleValue();
        }
        if (clazz == Long.class) {
            double doubleValue = number.doubleValue();
            if (number.longValue() == (long)doubleValue) {
                return doubleValue;
            }
        }
        throw new IllegalArgumentException("Can't represent " + number + " as double exactly");
    }

    public static long asLongExactly(Number number) {
        Class<?> clazz = number.getClass();
        if (Numbers.isLongRepresentable(clazz)) {
            return number.longValue();
        }
        if (Numbers.isDoubleRepresentable(clazz)) {
            long longValue = number.longValue();
            if (Numbers.equalDoubles(number.doubleValue(), longValue)) {
                return longValue;
            }
        }
        throw new IllegalArgumentException("Can't represent " + number + " as long exactly");
    }

    public static int asIntExactly(Number number) {
        Class<?> clazz = number.getClass();
        if (Numbers.isLongRepresentableExceptLong(clazz)) {
            return number.intValue();
        }
        if (clazz == Long.class) {
            int intValue = number.intValue();
            if (number.longValue() == (long)intValue) {
                return intValue;
            }
        } else if (Numbers.isDoubleRepresentable(clazz)) {
            int intValue = number.intValue();
            if (Numbers.equalDoubles(number.doubleValue(), intValue)) {
                return intValue;
            }
        }
        throw new IllegalArgumentException("Can't represent " + number + " as int exactly");
    }

    public static boolean isDoubleRepresentable(Class clazz) {
        return clazz == Double.class || clazz == Float.class;
    }

    public static boolean isLongRepresentable(Class clazz) {
        return clazz == Long.class || clazz == Integer.class || clazz == Short.class || clazz == Byte.class;
    }

    public static boolean equalLongAndDouble(long l, double d) {
        if (d > -9.007199254740992E15 && d < 9.007199254740992E15) {
            return Numbers.equalDoubles(l, d);
        }
        if (d <= -9.223372036854776E18) {
            return false;
        }
        if (d >= 9.223372036854776E18) {
            return false;
        }
        if (Double.isNaN(d)) {
            return false;
        }
        return l == (long)d;
    }

    public static int compareLongs(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    private static boolean isLongRepresentableExceptLong(Class clazz) {
        return clazz == Integer.class || clazz == Short.class || clazz == Byte.class;
    }

    private static int compareLongWithDouble(long l, double d) {
        if (d > -9.007199254740992E15 && d < 9.007199254740992E15) {
            return Double.compare(l, d);
        }
        if (d <= -9.223372036854776E18) {
            return 1;
        }
        if (d >= 9.223372036854776E18) {
            return -1;
        }
        if (Double.isNaN(d)) {
            return -1;
        }
        return Numbers.compareLongs(l, (long)d);
    }
}

