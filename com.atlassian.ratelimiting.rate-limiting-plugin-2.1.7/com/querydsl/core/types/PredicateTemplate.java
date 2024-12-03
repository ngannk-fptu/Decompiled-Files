/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpressionImpl;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PredicateTemplate
extends TemplateExpressionImpl<Boolean>
implements Predicate {
    private static final long serialVersionUID = -5371430939203772072L;
    @Nullable
    private volatile transient Predicate not;

    protected PredicateTemplate(Template template, ImmutableList<?> args) {
        super(Boolean.class, template, args);
    }

    @Override
    public Predicate not() {
        if (this.not == null) {
            this.not = ExpressionUtils.predicate((Operator)Ops.NOT, this);
        }
        return this.not;
    }
}

