/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.SunUnsafeReflectionProvider;

public class Sun14ReflectionProvider
extends SunUnsafeReflectionProvider {
    public Sun14ReflectionProvider() {
    }

    public Sun14ReflectionProvider(FieldDictionary dic) {
        super(dic);
    }

    private Object readResolve() {
        this.init();
        return this;
    }
}

