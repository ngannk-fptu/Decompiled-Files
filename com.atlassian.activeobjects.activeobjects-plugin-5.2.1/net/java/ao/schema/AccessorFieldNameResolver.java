/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.reflect.Method;
import net.java.ao.Accessor;
import net.java.ao.schema.AbstractFieldNameResolver;

public final class AccessorFieldNameResolver
extends AbstractFieldNameResolver {
    public AccessorFieldNameResolver() {
        super(false);
    }

    @Override
    public boolean accept(Method method) {
        return method.isAnnotationPresent(Accessor.class);
    }

    @Override
    public String resolve(Method method) {
        return method.getAnnotation(Accessor.class).value();
    }
}

