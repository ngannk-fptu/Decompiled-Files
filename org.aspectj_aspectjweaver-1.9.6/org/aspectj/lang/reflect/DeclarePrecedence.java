/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.TypePattern;

public interface DeclarePrecedence {
    public AjType getDeclaringType();

    public TypePattern[] getPrecedenceOrder();
}

