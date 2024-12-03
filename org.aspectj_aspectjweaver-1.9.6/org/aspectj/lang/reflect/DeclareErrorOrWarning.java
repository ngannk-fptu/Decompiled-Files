/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.PointcutExpression;

public interface DeclareErrorOrWarning {
    public AjType getDeclaringType();

    public PointcutExpression getPointcutExpression();

    public String getMessage();

    public boolean isError();
}

