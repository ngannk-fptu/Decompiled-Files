/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.classmate.TypeResolver
 */
package org.hibernate.validator.internal.util;

import com.fasterxml.classmate.TypeResolver;

public class TypeResolutionHelper {
    private final TypeResolver typeResolver = new TypeResolver();

    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }
}

