/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.stdclasses.DoubleCachedClass;

public class BigDecimalCachedClass
extends DoubleCachedClass {
    public BigDecimalCachedClass(Class klazz, ClassInfo classInfo) {
        super(klazz, classInfo, true);
    }

    @Override
    public boolean isDirectlyAssignable(Object argument) {
        return argument instanceof BigDecimal;
    }

    @Override
    public Object coerceArgument(Object argument) {
        if (argument instanceof BigDecimal) {
            return argument;
        }
        if (argument instanceof Long) {
            return new BigDecimal((Long)argument);
        }
        if (argument instanceof BigInteger) {
            return new BigDecimal((BigInteger)argument);
        }
        if (argument instanceof Number) {
            return new BigDecimal(((Number)argument).doubleValue());
        }
        return argument;
    }
}

