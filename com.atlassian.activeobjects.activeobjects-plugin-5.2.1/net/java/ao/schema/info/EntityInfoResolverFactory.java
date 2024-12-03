/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.info;

import net.java.ao.schema.NameConverters;
import net.java.ao.schema.info.EntityInfoResolver;
import net.java.ao.types.TypeManager;

public interface EntityInfoResolverFactory {
    public EntityInfoResolver create(NameConverters var1, TypeManager var2);
}

