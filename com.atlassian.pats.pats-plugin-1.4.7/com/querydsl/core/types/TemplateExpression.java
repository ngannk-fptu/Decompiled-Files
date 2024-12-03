/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Template;
import java.util.List;

public interface TemplateExpression<T>
extends Expression<T> {
    public Object getArg(int var1);

    public List<?> getArgs();

    public Template getTemplate();
}

