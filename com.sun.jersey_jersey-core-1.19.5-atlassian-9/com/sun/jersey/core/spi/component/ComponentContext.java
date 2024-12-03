/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

public interface ComponentContext {
    public AccessibleObject getAccesibleObject();

    public Annotation[] getAnnotations();
}

