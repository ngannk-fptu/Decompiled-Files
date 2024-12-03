/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.InterTypeDeclaration;

public interface InterTypeMethodDeclaration
extends InterTypeDeclaration {
    public String getName();

    public AjType<?> getReturnType();

    public Type getGenericReturnType();

    public AjType<?>[] getParameterTypes();

    public Type[] getGenericParameterTypes();

    public TypeVariable<Method>[] getTypeParameters();

    public AjType<?>[] getExceptionTypes();
}

