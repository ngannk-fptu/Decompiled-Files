/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.lang.Nullable;

public interface OperatorOverloader {
    public boolean overridesOperation(Operation var1, @Nullable Object var2, @Nullable Object var3) throws EvaluationException;

    public Object operate(Operation var1, @Nullable Object var2, @Nullable Object var3) throws EvaluationException;
}

