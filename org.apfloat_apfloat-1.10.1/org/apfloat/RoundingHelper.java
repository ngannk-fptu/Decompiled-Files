/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.math.RoundingMode;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.Aprational;

class RoundingHelper {
    private RoundingHelper() {
    }

    public static Apfloat round(Apfloat x, long precision, RoundingMode roundingMode) throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException {
        boolean overflow;
        if (precision <= 0L) {
            throw new IllegalArgumentException("Invalid precision: " + precision);
        }
        if (x.signum() == 0 || precision == Long.MAX_VALUE) {
            return x;
        }
        long scale = x.scale();
        boolean bl = overflow = scale - precision >= scale;
        if (overflow) {
            x = x.scale(-scale);
            x = x.scale(precision);
        } else {
            x = x.scale(precision - scale);
        }
        switch (roundingMode) {
            case UP: {
                x = x.roundAway();
                break;
            }
            case DOWN: {
                x = x.truncate();
                break;
            }
            case CEILING: {
                x = x.ceil();
                break;
            }
            case FLOOR: {
                x = x.floor();
                break;
            }
            case HALF_UP: 
            case HALF_DOWN: 
            case HALF_EVEN: {
                Apint whole = x.truncate();
                Apfloat fraction = x.frac().abs();
                int comparison = fraction.compareToHalf();
                if (comparison < 0 || comparison == 0 && roundingMode.equals((Object)RoundingMode.HALF_DOWN)) {
                    x = x.truncate();
                    break;
                }
                if (comparison > 0 || comparison == 0 && roundingMode.equals((Object)RoundingMode.HALF_UP)) {
                    x = x.roundAway();
                    break;
                }
                x = RoundingHelper.isEven(whole) ? x.truncate() : x.roundAway();
                break;
            }
            case UNNECESSARY: {
                if (x.size() <= x.scale()) break;
                throw new ArithmeticException("Rounding necessary");
            }
            default: {
                throw new IllegalArgumentException("Unknown rounding mode: " + (Object)((Object)roundingMode));
            }
        }
        if (overflow) {
            x = ApfloatMath.scale(x, -precision);
            x = ApfloatMath.scale(x, scale);
        } else {
            x = ApfloatMath.scale(x, scale - precision);
        }
        return x.precision(precision);
    }

    public static int compareToHalf(Apfloat x) {
        int comparison;
        if (x.radix() % 2 == 0) {
            comparison = x.compareTo(new Apfloat("0." + Character.forDigit(x.radix() / 2, x.radix()), Long.MAX_VALUE, x.radix()));
        } else {
            Apint one = new Apint(1L, x.radix());
            Apint two = new Apint(2L, x.radix());
            comparison = x.precision(Long.MAX_VALUE).multiply(two).compareTo(one);
        }
        return comparison;
    }

    public static int compareToHalf(Aprational x) {
        Aprational half = new Aprational(new Apint(1L, x.radix()), new Apint(2L, x.radix()));
        int comparison = x.compareTo(half);
        return comparison;
    }

    private static boolean isEven(Apint x) {
        Apint two = new Apint(2L, x.radix());
        return x.mod(two).signum() == 0;
    }
}

