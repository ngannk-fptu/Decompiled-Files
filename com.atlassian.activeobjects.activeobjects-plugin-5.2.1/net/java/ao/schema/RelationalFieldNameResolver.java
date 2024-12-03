/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.reflect.Method;
import net.java.ao.Common;
import net.java.ao.schema.AbstractFieldNameResolver;

public final class RelationalFieldNameResolver
extends AbstractFieldNameResolver {
    public RelationalFieldNameResolver() {
        super(false);
    }

    @Override
    public boolean accept(Method method) {
        return Common.isAnnotatedAsRelational(method);
    }
}

