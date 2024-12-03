/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types.dsl;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.PredicateTemplate;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;

public class BooleanTemplate
extends BooleanExpression
implements TemplateExpression<Boolean> {
    private static final long serialVersionUID = 5749369427497731719L;
    private final PredicateTemplate templateMixin;

    protected BooleanTemplate(PredicateTemplate mixin) {
        super(mixin);
        this.templateMixin = mixin;
    }

    protected BooleanTemplate(Template template, ImmutableList<?> args) {
        super(ExpressionUtils.predicateTemplate(template, args));
        this.templateMixin = (PredicateTemplate)this.mixin;
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

