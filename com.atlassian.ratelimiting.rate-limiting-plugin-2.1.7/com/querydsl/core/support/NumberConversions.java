/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.querydsl.core.support;

import com.google.common.collect.Maps;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.util.MathUtils;
import java.util.List;
import java.util.Map;

public class NumberConversions<T>
extends FactoryExpressionBase<T> {
    private static final long serialVersionUID = -7834053123363933721L;
    private final FactoryExpression<T> expr;
    private final Map<Class<?>, Enum<?>[]> values = Maps.newHashMap();

    public NumberConversions(FactoryExpression<T> expr) {
        super(expr.getType());
        this.expr = expr;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public List<Expression<?>> getArgs() {
        return this.expr.getArgs();
    }

    private <E extends Enum<E>> Enum<E>[] getValues(Class<E> enumClass) {
        Enum<?>[] values = this.values.get(enumClass);
        if (values == null) {
            values = (Enum[])enumClass.getEnumConstants();
            this.values.put(enumClass, values);
        }
        return values;
    }

    @Override
    public T newInstance(Object ... args) {
        for (int i = 0; i < args.length; ++i) {
            Class<?> type = this.expr.getArgs().get(i).getType();
            if (Enum.class.isAssignableFrom(type) && !type.isInstance(args[i])) {
                if (args[i] instanceof String) {
                    args[i] = Enum.valueOf(type, (String)args[i]);
                    continue;
                }
                if (!(args[i] instanceof Number)) continue;
                args[i] = this.getValues(type)[((Number)args[i]).intValue()];
                continue;
            }
            if (!(args[i] instanceof Number) || type.isInstance(args[i])) continue;
            if (type.equals(Boolean.class)) {
                args[i] = ((Number)args[i]).intValue() > 0;
                continue;
            }
            if (!Number.class.isAssignableFrom(type)) continue;
            args[i] = MathUtils.cast((Number)args[i], type);
        }
        return this.expr.newInstance(args);
    }
}

