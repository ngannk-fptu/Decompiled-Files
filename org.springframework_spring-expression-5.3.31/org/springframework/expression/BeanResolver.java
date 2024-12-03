/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;

public interface BeanResolver {
    public Object resolve(EvaluationContext var1, String var2) throws AccessException;
}

