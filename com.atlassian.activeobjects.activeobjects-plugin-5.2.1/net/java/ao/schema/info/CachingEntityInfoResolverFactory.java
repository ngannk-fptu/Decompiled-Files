/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.info;

import net.java.ao.schema.NameConverters;
import net.java.ao.schema.info.CachingEntityInfoResolver;
import net.java.ao.schema.info.EntityInfoResolver;
import net.java.ao.schema.info.SimpleEntityInfoResolverFactory;
import net.java.ao.types.TypeManager;

public class CachingEntityInfoResolverFactory
extends SimpleEntityInfoResolverFactory {
    @Override
    public EntityInfoResolver create(NameConverters nameConverters, TypeManager typeManager) {
        return new CachingEntityInfoResolver(super.create(nameConverters, typeManager));
    }
}

