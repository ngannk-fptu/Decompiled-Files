/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.SimpleNode;

public final class AstNegative
extends SimpleNode {
    public AstNegative(int id) {
        super(id);
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return Number.class;
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj = this.children[0].getValue(ctx);
        if (obj == null) {
            return 0L;
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal)obj).negate();
        }
        if (obj instanceof BigInteger) {
            return ((BigInteger)obj).negate();
        }
        if (obj instanceof String) {
            if (AstNegative.isStringFloat((String)obj)) {
                return -Double.parseDouble((String)obj);
            }
            return -Long.parseLong((String)obj);
        }
        if (obj instanceof Long) {
            return -((Long)obj).longValue();
        }
        if (obj instanceof Double) {
            return -((Double)obj).doubleValue();
        }
        if (obj instanceof Integer) {
            return -((Integer)obj).intValue();
        }
        if (obj instanceof Float) {
            return Float.valueOf(-((Float)obj).floatValue());
        }
        if (obj instanceof Short) {
            return -((Short)obj).shortValue();
        }
        if (obj instanceof Byte) {
            return -((Byte)obj).byteValue();
        }
        Long num = (Long)AstNegative.coerceToNumber(ctx, obj, Long.class);
        return -num.longValue();
    }
}

