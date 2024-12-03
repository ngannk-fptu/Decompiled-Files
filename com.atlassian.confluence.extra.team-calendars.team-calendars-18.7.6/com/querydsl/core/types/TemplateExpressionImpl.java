/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.ExpressionBase;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public class TemplateExpressionImpl<T>
extends ExpressionBase<T>
implements TemplateExpression<T> {
    private static final long serialVersionUID = 6951623726800809083L;
    private final ImmutableList<?> args;
    private final Template template;

    protected TemplateExpressionImpl(Class<? extends T> type, Template template, Object ... args) {
        this(type, template, ImmutableList.copyOf((Object[])args));
    }

    protected TemplateExpressionImpl(Class<? extends T> type, Template template, ImmutableList<?> args) {
        super(type);
        this.args = args;
        this.template = template;
    }

    @Override
    public final Object getArg(int index) {
        return this.getArgs().get(index);
    }

    @Override
    public final List<?> getArgs() {
        return this.args;
    }

    @Override
    public final Template getTemplate() {
        return this.template;
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TemplateExpression) {
            TemplateExpression c = (TemplateExpression)o;
            return c.getTemplate().equals(this.template) && c.getType().equals(this.getType()) && c.getArgs().equals(this.args);
        }
        return false;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }
}

