/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity.htmlsafe.introspection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public interface MethodAnnotator {
    public Collection<Annotation> getAnnotationsForMethod(Method var1);
}

