/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Visitor;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.concurrent.Immutable;

@Immutable
public class QTuple
extends FactoryExpressionBase<Tuple> {
    private static final long serialVersionUID = -2640616030595420465L;
    private final ImmutableList<Expression<?>> args;
    private final ImmutableMap<Expression<?>, Integer> bindings;

    private static ImmutableMap<Expression<?>, Integer> createBindings(List<Expression<?>> exprs) {
        HashMap map = Maps.newHashMap();
        for (int i = 0; i < exprs.size(); ++i) {
            Expression<?> e = exprs.get(i);
            if (e instanceof Operation && ((Operation)e).getOperator() == Ops.ALIAS) {
                map.put(((Operation)e).getArg(1), i);
            }
            map.put(e, i);
        }
        return ImmutableMap.copyOf((Map)map);
    }

    protected QTuple(Expression<?> ... args) {
        super(Tuple.class);
        this.args = ImmutableList.copyOf((Object[])args);
        this.bindings = QTuple.createBindings(this.args);
    }

    protected QTuple(ImmutableList<Expression<?>> args) {
        super(Tuple.class);
        this.args = args;
        this.bindings = QTuple.createBindings(this.args);
    }

    protected QTuple(Expression<?>[] ... args) {
        super(Tuple.class);
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Object[] objectArray : args) {
            builder.add(objectArray);
        }
        this.args = builder.build();
        this.bindings = QTuple.createBindings(this.args);
    }

    @Override
    public Tuple newInstance(Object ... a) {
        return new TupleImpl(a);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FactoryExpression) {
            FactoryExpression c = (FactoryExpression)obj;
            return this.args.equals(c.getArgs()) && this.getType().equals(c.getType());
        }
        return false;
    }

    @Override
    public List<Expression<?>> getArgs() {
        return this.args;
    }

    private final class TupleImpl
    implements Tuple,
    Serializable {
        private static final long serialVersionUID = 6635924689293325950L;
        private final Object[] a;

        private TupleImpl(Object[] a) {
            this.a = a;
        }

        @Override
        public <T> T get(int index, Class<T> type) {
            return (T)this.a[index];
        }

        @Override
        public <T> T get(Expression<T> expr) {
            Integer idx = (Integer)QTuple.this.bindings.get(expr);
            if (idx != null) {
                return (T)this.a[idx];
            }
            return null;
        }

        @Override
        public int size() {
            return this.a.length;
        }

        @Override
        public Object[] toArray() {
            return this.a;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Tuple) {
                return Arrays.equals(this.a, ((Tuple)obj).toArray());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.a);
        }

        public String toString() {
            return Arrays.toString(this.a);
        }
    }
}

