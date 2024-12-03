/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.constraintvalidators.bv.number;

import java.util.OptionalInt;

public final class InfinityNumberComparatorHelper {
    public static final OptionalInt LESS_THAN = OptionalInt.of(-1);
    public static final OptionalInt FINITE_VALUE = OptionalInt.empty();
    public static final OptionalInt GREATER_THAN = OptionalInt.of(1);

    private InfinityNumberComparatorHelper() {
    }

    public static OptionalInt infinityCheck(Double number, OptionalInt treatNanAs) {
        OptionalInt result = FINITE_VALUE;
        if (number == Double.NEGATIVE_INFINITY) {
            result = LESS_THAN;
        } else if (number.isNaN()) {
            result = treatNanAs;
        } else if (number == Double.POSITIVE_INFINITY) {
            result = GREATER_THAN;
        }
        return result;
    }

    public static OptionalInt infinityCheck(Float number, OptionalInt treatNanAs) {
        OptionalInt result = FINITE_VALUE;
        if (number.floatValue() == Float.NEGATIVE_INFINITY) {
            result = LESS_THAN;
        } else if (number.isNaN()) {
            result = treatNanAs;
        } else if (number.floatValue() == Float.POSITIVE_INFINITY) {
            result = GREATER_THAN;
        }
        return result;
    }
}

