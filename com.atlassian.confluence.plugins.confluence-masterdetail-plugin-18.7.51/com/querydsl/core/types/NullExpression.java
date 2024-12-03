/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpressionImpl;
import com.querydsl.core.types.TemplateFactory;

public final class NullExpression<T>
extends TemplateExpressionImpl<T> {
    private static final Template NULL_TEMPLATE = TemplateFactory.DEFAULT.create("null");
    private static final long serialVersionUID = -5311968198973316411L;
    public static final NullExpression<Object> DEFAULT = new NullExpression<Object>(Object.class);

    private NullExpression(Class<? extends T> type) {
        super(type, NULL_TEMPLATE, ImmutableList.of());
    }
}

