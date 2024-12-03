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
abstract class CompositionFieldNamingPolicy
extends RecursiveFieldNamingPolicy {
    private final RecursiveFieldNamingPolicy[] fieldPolicies;

    public CompositionFieldNamingPolicy(RecursiveFieldNamingPolicy ... fieldNamingPolicies) {
        if (fieldNamingPolicies == null) {
            throw new NullPointerException("naming policies can not be null.");
        }
        this.fieldPolicies = fieldNamingPolicies;
    }

    @Override
    protected String translateName(String target, Type fieldType, Collection<Annotation> annotations) {
        for (RecursiveFieldNamingPolicy policy : this.fieldPolicies) {
            target = policy.translateName(target, fieldType, annotations);
        }
        return target;
    }
}

