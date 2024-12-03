/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.cache.spi.access.AccessType;

public interface MappingDefaults {
    public static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
    public static final String DEFAULT_TENANT_IDENTIFIER_COLUMN_NAME = "tenant_id";
    public static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
    public static final String DEFAULT_CASCADE_NAME = "none";
    public static final String DEFAULT_PROPERTY_ACCESS_NAME = "property";

    public String getImplicitSchemaName();

    public String getImplicitCatalogName();

    public boolean shouldImplicitlyQuoteIdentifiers();

    public String getImplicitIdColumnName();

    public String getImplicitTenantIdColumnName();

    public String getImplicitDiscriminatorColumnName();

    public String getImplicitPackageName();

    public boolean isAutoImportEnabled();

    public String getImplicitCascadeStyleName();

    public String getImplicitPropertyAccessorName();

    public boolean areEntitiesImplicitlyLazy();

    public boolean areCollectionsImplicitlyLazy();

    public AccessType getImplicitCacheAccessType();
}

