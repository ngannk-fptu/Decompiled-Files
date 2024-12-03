/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;
import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

final class NumberComparatorHelper {
    private NumberComparatorHelper() {
    }

    public static int compare(BigDecimal number, long value) {
        return number.compareTo(BigDecimal.valueOf(value));
    }

    public static int compare(BigInteger number, long value) {
        return number.compareTo(BigInteger.valueOf(value));
    }

    public static int compare(Long number, long value) {
        return number.compareTo(value);
    }

    public static int compare(Number number, long value, OptionalInt treatNanAs) {
        if (number instanceof Double) {
            return NumberComparatorHelper.compare((Double)number, value, treatNanAs);
        }
        if (number instanceof Float) {
            return NumberComparatorHelper.compare((Float)number, value, treatNanAs);
        }
        if (number instanceof BigDecimal) {
            return NumberComparatorHelper.compare((BigDecimal)number, value);
        }
        if (number instanceof BigInteger) {
            return NumberComparatorHelper.compare((BigInteger)number, value);
        }
        return Long.compare(number.longValue(), value);
    }

    public static int compare(Double number, long value, OptionalInt treatNanAs) {
        OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return Double.compare(number, value);
    }

    public static int compare(Float number, long value, OptionalInt treatNanAs) {
        OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return Float.compare(number.floatValue(), value);
    }
}

