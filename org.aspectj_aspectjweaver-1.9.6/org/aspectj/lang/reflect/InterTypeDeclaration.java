/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import org.aspectj.lang.reflect.AjType;

public interface InterTypeDeclaration {
    public AjType<?> getDeclaringType();

    public AjType<?> getTargetType() throws ClassNotFoundException;

    public int getModifiers();
}

