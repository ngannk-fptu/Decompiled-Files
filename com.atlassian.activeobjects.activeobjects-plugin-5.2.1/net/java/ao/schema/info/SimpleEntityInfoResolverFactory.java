/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.info;

import net.java.ao.schema.NameConverters;
import net.java.ao.schema.info.EntityInfoResolver;
import net.java.ao.schema.info.EntityInfoResolverFactory;
import net.java.ao.schema.info.SimpleEntityInfoResolver;
import net.java.ao.types.TypeManager;

public class SimpleEntityInfoResolverFactory
implements EntityInfoResolverFactory {
    @Override
    public EntityInfoResolver create(NameConverters nameConverters, TypeManager typeManager) {
        return new SimpleEntityInfoResolver(nameConverters, typeManager);
    }
}

