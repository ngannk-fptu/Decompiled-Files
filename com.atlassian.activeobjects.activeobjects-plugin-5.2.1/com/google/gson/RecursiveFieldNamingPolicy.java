/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingStrategy2;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class RecursiveFieldNamingPolicy
implements FieldNamingStrategy2 {
    RecursiveFieldNamingPolicy() {
    }

    @Override
    public final String translateName(FieldAttributes f) {
        return this.translateName(f.getName(), f.getDeclaredType(), f.getAnnotations());
    }

    protected abstract String translateName(String var1, Type var2, Collection<Annotation> var3);
}

