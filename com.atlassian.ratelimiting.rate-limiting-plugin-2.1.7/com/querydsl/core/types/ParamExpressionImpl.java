/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core.types;

import com.querydsl.core.types.ExpressionBase;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Visitor;
import java.util.UUID;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ParamExpressionImpl<T>
extends ExpressionBase<T>
implements ParamExpression<T> {
    private static final long serialVersionUID = -6872502615009012503L;
    private final String name;
    private final boolean anon;

    public ParamExpressionImpl(Class<? extends T> type, String name) {
        super(type);
        this.name = name;
        this.anon = false;
    }

    public ParamExpressionImpl(Class<? extends T> type) {
        super(type);
        this.name = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        this.anon = true;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ParamExpression) {
            ParamExpression other = (ParamExpression)o;
            return other.getType().equals(this.getType()) && other.getName().equals(this.name) && other.isAnon() == this.anon;
        }
        return false;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final boolean isAnon() {
        return this.anon;
    }

    @Override
    public final String getNotSetMessage() {
        if (!this.anon) {
            return "The parameter " + this.name + " needs to be set";
        }
        return "A parameter of type " + this.getType().getName() + " was not set";
    }
}

