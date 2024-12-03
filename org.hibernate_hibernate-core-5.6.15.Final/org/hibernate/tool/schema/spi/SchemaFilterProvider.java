/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.Incubating;
import org.hibernate.tool.schema.spi.SchemaFilter;

@Incubating
public interface SchemaFilterProvider {
    public SchemaFilter getCreateFilter();

    public SchemaFilter getDropFilter();

    public SchemaFilter getMigrateFilter();

    public SchemaFilter getValidateFilter();
}

