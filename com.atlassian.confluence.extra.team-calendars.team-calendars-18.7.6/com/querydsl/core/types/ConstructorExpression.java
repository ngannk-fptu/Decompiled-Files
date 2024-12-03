/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionException;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.util.ConstructorUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ConstructorExpression<T>
extends FactoryExpressionBase<T> {
    private static final long serialVersionUID = -602747921848073175L;
    private final ImmutableList<Expression<?>> args;
    private final Class<?>[] parameterTypes;
    private final transient Constructor<?> constructor;
    private final transient Iterable<Function<Object[], Object[]>> transformers;

    private static Class<?>[] getParameterTypes(Expression<?> ... args) {
        Class[] paramTypes = new Class[args.length];
        for (int i = 0; i < paramTypes.length; ++i) {
            paramTypes[i] = args[i].getType();
        }
        return paramTypes;
    }

    protected ConstructorExpression(Class<? extends T> type, Expression<?> ... args) {
        this(type, ConstructorExpression.getParameterTypes(args), ImmutableList.copyOf((Object[])args));
    }

    protected ConstructorExpression(Class<? extends T> type, Class<?>[] paramTypes, Expression<?> ... args) {
        this(type, paramTypes, ImmutableList.copyOf((Object[])args));
    }

    protected ConstructorExpression(Class<? extends T> type, Class<?>[] paramTypes, ImmutableList<Expression<?>> args) {
        super(type);
        try {
            this.parameterTypes = (Class[])ConstructorUtils.getConstructorParameters(type, paramTypes).clone();
            this.args = args;
            this.constructor = ConstructorUtils.getConstructor(this.getType(), this.parameterTypes);
            this.transformers = ConstructorUtils.getTransformers(this.constructor);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Expression<T> as(Path<T> alias) {
        return ExpressionUtils.operation(this.getType(), (Operator)Ops.ALIAS, this, alias);
    }

    public Expression<T> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
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
        if (obj instanceof ConstructorExpression) {
            ConstructorExpression c = (ConstructorExpression)obj;
            return Arrays.equals(this.parameterTypes, c.parameterTypes) && this.args.equals(c.args) && this.getType().equals(c.getType());
        }
        return false;
    }

    @Override
    public final List<Expression<?>> getArgs() {
        return this.args;
    }

    @Override
    public T newInstance(Object ... args) {
        try {
            for (Function<Object[], Object[]> transformer : this.transformers) {
                args = (Object[])transformer.apply((Object)args);
            }
            return (T)this.constructor.newInstance(args);
        }
        catch (SecurityException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        catch (InstantiationException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        catch (IllegalAccessException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        catch (InvocationTargetException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.readObject();
        try {
            Field constructor = ConstructorExpression.class.getDeclaredField("constructor");
            constructor.setAccessible(true);
            constructor.set(this, ConstructorUtils.getConstructor(this.getType(), this.parameterTypes));
            Field transformers = ConstructorExpression.class.getDeclaredField("transformers");
            transformers.setAccessible(true);
            transformers.set(this, ConstructorUtils.getTransformers(this.constructor));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

