/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.PointcutExpression;

public interface Advice {
    public AjType getDeclaringType();

    public AdviceKind getKind();

    public String getName();

    public AjType<?>[] getParameterTypes();

    public Type[] getGenericParameterTypes();

    public AjType<?>[] getExceptionTypes();

    public PointcutExpression getPointcutExpression();
}

