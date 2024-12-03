/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.InterTypeDeclaration;

public interface InterTypeFieldDeclaration
extends InterTypeDeclaration {
    public String getName();

    public AjType<?> getType();

    public Type getGenericType();
}

