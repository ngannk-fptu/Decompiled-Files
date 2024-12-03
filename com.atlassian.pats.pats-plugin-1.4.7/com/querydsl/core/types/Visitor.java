/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;

public interface Visitor<R, C> {
    public R visit(Constant<?> var1, C var2);

    public R visit(FactoryExpression<?> var1, C var2);

    public R visit(Operation<?> var1, C var2);

    public R visit(ParamExpression<?> var1, C var2);

    public R visit(Path<?> var1, C var2);

    public R visit(SubQueryExpression<?> var1, C var2);

    public R visit(TemplateExpression<?> var1, C var2);
}

