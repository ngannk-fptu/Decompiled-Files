/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression;

import org.springframework.expression.EvaluationException;
import org.springframework.lang.Nullable;

public interface TypeComparator {
    public boolean canCompare(@Nullable Object var1, @Nullable Object var2);

    public int compare(@Nullable Object var1, @Nullable Object var2) throws EvaluationException;
}

