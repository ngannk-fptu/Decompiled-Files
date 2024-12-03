/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.reflect.Method;
import net.java.ao.schema.AbstractFieldNameResolver;

public final class NullFieldNameResolver
extends AbstractFieldNameResolver {
    public NullFieldNameResolver() {
        super(false);
    }

    @Override
    public boolean accept(Method method) {
        return true;
    }
}

