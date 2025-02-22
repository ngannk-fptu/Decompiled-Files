/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.OptionalInt;
import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

final class NumberSignHelper {
    private static final short SHORT_ZERO = 0;
    private static final byte BYTE_ZERO = 0;

    private NumberSignHelper() {
    }

    static int signum(Long number) {
        return Long.signum(number);
    }

    static int signum(Integer number) {
        return Integer.signum(number);
    }

    static int signum(Short number) {
        return number.compareTo((short)0);
    }

    static int signum(Byte number) {
        return number.compareTo((byte)0);
    }

    static int signum(BigInteger number) {
        return number.signum();
    }

    static int signum(BigDecimal number) {
        return number.signum();
    }

    static int signum(Number value) {
        return Double.compare(value.doubleValue(), 0.0);
    }

    static int signum(Float number, OptionalInt treatNanAs) {
        OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return number.compareTo(Float.valueOf(0.0f));
    }

    static int signum(Double number, OptionalInt treatNanAs) {
        OptionalInt infinity = InfinityNumberComparatorHelper.infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return number.compareTo(0.0);
    }
}

