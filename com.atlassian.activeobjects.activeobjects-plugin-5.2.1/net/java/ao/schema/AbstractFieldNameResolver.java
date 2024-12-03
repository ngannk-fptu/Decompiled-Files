/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.reflect.Method;
import net.java.ao.schema.FieldNameResolver;

abstract class AbstractFieldNameResolver
implements FieldNameResolver {
    private final boolean transform;

    protected AbstractFieldNameResolver(boolean transform) {
        this.transform = transform;
    }

    @Override
    public boolean accept(Method method) {
        return false;
    }

    @Override
    public String resolve(Method method) {
        return null;
    }

    @Override
    public final boolean transform() {
        return this.transform;
    }
}

