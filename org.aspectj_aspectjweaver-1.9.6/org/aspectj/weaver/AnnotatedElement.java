/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;

public interface AnnotatedElement {
    public boolean hasAnnotation(UnresolvedType var1);

    public ResolvedType[] getAnnotationTypes();

    public AnnotationAJ getAnnotationOfType(UnresolvedType var1);
}

