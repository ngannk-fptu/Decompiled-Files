/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types.dsl;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.TemplateExpressionImpl;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.TimeExpression;
import java.util.List;

public class TimeTemplate<T extends Comparable<?>>
extends TimeExpression<T>
implements TemplateExpression<T> {
    private static final long serialVersionUID = -7684306954555037051L;
    private final TemplateExpressionImpl<T> templateMixin;

    protected TimeTemplate(TemplateExpressionImpl<T> mixin) {
        super(mixin);
        this.templateMixin = mixin;
    }

    protected TimeTemplate(Class<? extends T> type, Template template, ImmutableList<?> args) {
        super(ExpressionUtils.template(type, template, args));
        this.templateMixin = (TemplateExpressionImpl)this.mixin;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this.templateMixin, context);
    }

    @Override
    public Object getArg(int index) {
        return this.templateMixin.getArg(index);
    }

    @Override
    public List<?> getArgs() {
        return this.templateMixin.getArgs();
    }

    @Override
    public Template getTemplate() {
        return this.templateMixin.getTemplate();
    }
}

