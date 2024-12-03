/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.lang.reflect.Method;

public interface FieldNameResolver {
    public boolean accept(Method var1);

    public String resolve(Method var1);

    public boolean transform();
}

