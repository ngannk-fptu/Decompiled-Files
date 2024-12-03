/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.common.annotation;

import io.micrometer.common.annotation.ValueResolver;

public class NoOpValueResolver
implements ValueResolver {
    @Override
    public String resolve(Object parameter) {
        return null;
    }
}

