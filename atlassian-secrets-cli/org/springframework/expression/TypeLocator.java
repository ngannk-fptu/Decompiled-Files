/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression;

import org.springframework.expression.EvaluationException;

@FunctionalInterface
public interface TypeLocator {
    public Class<?> findType(String var1) throws EvaluationException;
}

