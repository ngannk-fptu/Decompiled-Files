/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.PointcutExpression;

public interface Pointcut {
    public String getName();

    public int getModifiers();

    public AjType<?>[] getParameterTypes();

    public String[] getParameterNames();

    public AjType getDeclaringType();

    public PointcutExpression getPointcutExpression();
}

