/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.PointcutExpression;

public interface DeclareSoft {
    public AjType getDeclaringType();

    public AjType getSoftenedExceptionType() throws ClassNotFoundException;

    public PointcutExpression getPointcutExpression();
}

