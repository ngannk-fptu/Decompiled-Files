/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.reflect.Method;
import net.java.ao.schema.AbstractFieldNameResolver;

public final class GetterFieldNameResolver
extends AbstractFieldNameResolver {
    public GetterFieldNameResolver() {
        super(true);
    }

    @Override
    public boolean accept(Method method) {
        return method.getName().startsWith("get");
    }

    @Override
    public String resolve(Method method) {
        return method.getName().substring(3);
    }
}

