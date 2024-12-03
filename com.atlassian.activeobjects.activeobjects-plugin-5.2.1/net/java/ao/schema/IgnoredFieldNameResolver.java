/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.reflect.Method;
import net.java.ao.schema.AbstractFieldNameResolver;
import net.java.ao.schema.Ignore;

public final class IgnoredFieldNameResolver
extends AbstractFieldNameResolver {
    public IgnoredFieldNameResolver() {
        super(false);
    }

    @Override
    public boolean accept(Method method) {
        return method.isAnnotationPresent(Ignore.class);
    }
}

