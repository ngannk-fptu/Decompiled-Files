/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 */
package org.springframework.expression;

import java.util.List;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.ConstructorExecutor;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface ConstructorResolver {
    @Nullable
    public ConstructorExecutor resolve(EvaluationContext var1, String var2, List<TypeDescriptor> var3) throws AccessException;
}

