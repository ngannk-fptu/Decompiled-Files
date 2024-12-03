/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.expression;

import org.springframework.expression.EvaluationException;
import org.springframework.lang.Nullable;

public interface TypeComparator {
    public boolean canCompare(@Nullable Object var1, @Nullable Object var2);

    public int compare(@Nullable Object var1, @Nullable Object var2) throws EvaluationException;
}

