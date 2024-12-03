/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.info.EntityInfoResolverFactory;

public interface EntityManagerConfiguration {
    @Deprecated
    public boolean useWeakCache();

    public NameConverters getNameConverters();

    public SchemaConfiguration getSchemaConfiguration();

    public EntityInfoResolverFactory getEntityInfoResolverFactory();
}

