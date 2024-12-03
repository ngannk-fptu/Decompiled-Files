/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.predicate;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class ImplicitNumericExpressionTypeDeterminer {
    private ImplicitNumericExpressionTypeDeterminer() {
    }

    public static Class<? extends Number> determineResultType(Class<? extends Number> ... types) {
        Class<Number> result = Number.class;
        for (Class<? extends Number> type : types) {
            if (Double.class.equals(type)) {
                result = Double.class;
                continue;
            }
            if (Float.class.equals(type)) {
                result = Float.class;
                continue;
            }
            if (BigDecimal.class.equals(type)) {
                result = BigDecimal.class;
                continue;
            }
            if (BigInteger.class.equals(type)) {
                result = BigInteger.class;
                continue;
            }
            if (Long.class.equals(type)) {
                result = Long.class;
                continue;
            }
            if (!ImplicitNumericExpressionTypeDeterminer.isIntegralType(type)) continue;
            result = Integer.class;
        }
        return result;
    }

    private static boolean isIntegralType(Class<? extends Number> type) {
        return Integer.class.equals(type) || Short.class.equals(type);
    }
}

