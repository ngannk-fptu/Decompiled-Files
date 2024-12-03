/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.TemplateFactory;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.util.List;

public class RelationalFunctionCall<T>
extends SimpleExpression<T>
implements TemplateExpression<T> {
    private static final long serialVersionUID = 256739044928186923L;
    private final TemplateExpression<T> templateMixin;

    private static Template createTemplate(String function, int argCount) {
        StringBuilder builder = new StringBuilder();
        builder.append(function);
        builder.append("(");
        for (int i = 0; i < argCount; ++i) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append("{" + i + "}");
        }
        builder.append(")");
        return TemplateFactory.DEFAULT.create(builder.toString());
    }

    protected RelationalFunctionCall(Class<? extends T> type, String function, Object ... args) {
        super(ExpressionUtils.template(type, RelationalFunctionCall.createTemplate(function, args.length), args));
        this.templateMixin = (TemplateExpression)this.mixin;
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
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

