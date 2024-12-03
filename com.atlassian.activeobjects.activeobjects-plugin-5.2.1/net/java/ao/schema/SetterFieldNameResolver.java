/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.reflect.Method;
import net.java.ao.schema.AbstractFieldNameResolver;

public final class SetterFieldNameResolver
extends AbstractFieldNameResolver {
    public SetterFieldNameResolver() {
        super(true);
    }

    @Override
    public boolean accept(Method method) {
        return method.getName().startsWith("set");
    }

    @Override
    public String resolve(Method method) {
        return method.getName().substring(3);
    }
}

