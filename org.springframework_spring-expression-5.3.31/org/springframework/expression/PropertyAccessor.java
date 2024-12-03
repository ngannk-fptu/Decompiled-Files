/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.expression;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;

public interface PropertyAccessor {
    @Nullable
    public Class<?>[] getSpecificTargetClasses();

    public boolean canRead(EvaluationContext var1, @Nullable Object var2, String var3) throws AccessException;

    public TypedValue read(EvaluationContext var1, @Nullable Object var2, String var3) throws AccessException;

    public boolean canWrite(EvaluationContext var1, @Nullable Object var2, String var3) throws AccessException;

    public void write(EvaluationContext var1, @Nullable Object var2, String var3, @Nullable Object var4) throws AccessException;
}

