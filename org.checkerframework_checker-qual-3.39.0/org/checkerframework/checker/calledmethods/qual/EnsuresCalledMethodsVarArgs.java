/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.checker.calledmethods.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface EnsuresCalledMethodsVarArgs {
    public String[] value();
}

