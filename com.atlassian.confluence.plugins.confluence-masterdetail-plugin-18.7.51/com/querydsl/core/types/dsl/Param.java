/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.ParamExpressionImpl;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.SimpleExpression;

public class Param<T>
extends SimpleExpression<T>
implements ParamExpression<T> {
    private static final long serialVersionUID = -6872502615009012503L;
    private final ParamExpression<T> paramMixin;

    public Param(Class<? extends T> type, String name) {
        super(new ParamExpressionImpl<T>(type, name));
        this.paramMixin = (ParamExpression)this.mixin;
    }

    public Param(Class<? extends T> type) {
        super(new ParamExpressionImpl<T>(type));
        this.paramMixin = (ParamExpression)this.mixin;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public String getName() {
        return this.paramMixin.getName();
    }

    @Override
    public boolean isAnon() {
        return this.paramMixin.isAnon();
    }

    @Override
    public String getNotSetMessage() {
        return this.paramMixin.getNotSetMessage();
    }
}

