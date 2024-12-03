/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.reflect.AnnotationFinder;

public interface IReflectionWorld {
    public AnnotationFinder getAnnotationFinder();

    public ResolvedType resolve(Class var1);
}

