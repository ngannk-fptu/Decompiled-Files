/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types.dsl;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.TemplateExpressionImpl;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.StringExpression;
import java.util.List;

public class StringTemplate
extends StringExpression
implements TemplateExpression<String> {
    private static final long serialVersionUID = 3181686132439356614L;
    private final TemplateExpressionImpl<String> templateMixin;

    protected StringTemplate(TemplateExpressionImpl<String> mixin) {
        super((Expression<String>)mixin);
        this.templateMixin = mixin;
    }

    protected StringTemplate(Template template, ImmutableList<?> args) {
        super((Expression<String>)ExpressionUtils.template(String.class, template, args));
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

