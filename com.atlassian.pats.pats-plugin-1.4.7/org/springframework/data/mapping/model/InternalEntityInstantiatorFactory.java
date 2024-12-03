/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.model.ClassGeneratingEntityInstantiator;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.mapping.model.KotlinClassGeneratingEntityInstantiator;
import org.springframework.data.mapping.model.ReflectionEntityInstantiator;

@Deprecated
public class InternalEntityInstantiatorFactory {
    public static EntityInstantiator getClassGeneratingEntityInstantiator() {
        return new ClassGeneratingEntityInstantiator();
    }

    public static EntityInstantiator getKotlinClassGeneratingEntityInstantiator() {
        return new KotlinClassGeneratingEntityInstantiator();
    }

    public static EntityInstantiator getReflectionEntityInstantiator() {
        return ReflectionEntityInstantiator.INSTANCE;
    }
}

