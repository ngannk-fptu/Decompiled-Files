/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.RecursiveFieldNamingPolicy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class UpperCaseNamingPolicy
extends RecursiveFieldNamingPolicy {
    UpperCaseNamingPolicy() {
    }

    @Override
    protected String translateName(String target, Type fieldType, Collection<Annotation> annotations) {
        return target.toUpperCase();
    }
}

