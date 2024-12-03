/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.lang.Nullable;

public abstract class CompiledExpression {
    public abstract Object getValue(@Nullable Object var1, @Nullable EvaluationContext var2) throws EvaluationException;
}

