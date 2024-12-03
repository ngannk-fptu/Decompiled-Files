/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.reflect.Method;
import net.java.ao.Mutator;
import net.java.ao.schema.AbstractFieldNameResolver;

public final class MutatorFieldNameResolver
extends AbstractFieldNameResolver {
    public MutatorFieldNameResolver() {
        super(false);
    }

    @Override
    public boolean accept(Method method) {
        return method.isAnnotationPresent(Mutator.class);
    }

    @Override
    public String resolve(Method method) {
        return method.getAnnotation(Mutator.class).value();
    }
}

