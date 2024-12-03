/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal;

import org.hibernate.boot.spi.MappingDefaults;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.internal.util.StringHelper;

public class OverriddenMappingDefaults
implements MappingDefaults {
    private final String implicitSchemaName;
    private final String implicitCatalogName;
    private final boolean implicitlyQuoteIdentifiers;
    private final String implicitIdColumnName;
    private final String implicitTenantIdColumnName;
    private final String implicitDiscriminatorColumnName;
    private final String implicitPackageName;
    private final boolean autoImportEnabled;
    private final String implicitCascadeStyleName;
    private final String implicitPropertyAccessorName;
    private boolean entitiesImplicitlyLazy;
    private boolean pluralAttributesImplicitlyLazy;
    private final AccessType implicitCacheAccessType;

    public OverriddenMappingDefaults(String implicitSchemaName, String implicitCatalogName, boolean implicitlyQuoteIdentifiers, String implicitIdColumnName, String implicitTenantIdColumnName, String implicitDiscriminatorColumnName, String implicitPackageName, boolean autoImportEnabled, String implicitCascadeStyleName, String implicitPropertyAccessorName, boolean entitiesImplicitlyLazy, boolean pluralAttributesImplicitlyLazy, AccessType implicitCacheAccessType) {
        this.implicitSchemaName = implicitSchemaName;
        this.implicitCatalogName = implicitCatalogName;
        this.implicitlyQuoteIdentifiers = implicitlyQuoteIdentifiers;
        this.implicitIdColumnName = implicitIdColumnName;
        this.implicitTenantIdColumnName = implicitTenantIdColumnName;
        this.implicitDiscriminatorColumnName = implicitDiscriminatorColumnName;
        this.implicitPackageName = implicitPackageName;
        this.autoImportEnabled = autoImportEnabled;
        this.implicitCascadeStyleName = implicitCascadeStyleName;
        this.implicitPropertyAccessorName = implicitPropertyAccessorName;
        this.entitiesImplicitlyLazy = entitiesImplicitlyLazy;
        this.pluralAttributesImplicitlyLazy = pluralAttributesImplicitlyLazy;
        this.implicitCacheAccessType = implicitCacheAccessType;
    }

    @Override
    public String getImplicitSchemaName() {
        return this.implicitSchemaName;
    }

    @Override
    public String getImplicitCatalogName() {
        return this.implicitCatalogName;
    }

    @Override
    public boolean shouldImplicitlyQuoteIdentifiers() {
        return this.implicitlyQuoteIdentifiers;
    }

    @Override
    public String getImplicitIdColumnName() {
        return this.implicitIdColumnName;
    }

    @Override
    public String getImplicitTenantIdColumnName() {
        return this.implicitTenantIdColumnName;
    }

    @Override
    public String getImplicitDiscriminatorColumnName() {
        return this.implicitDiscriminatorColumnName;
    }

    @Override
    public String getImplicitPackageName() {
        return this.implicitPackageName;
    }

    @Override
    public boolean isAutoImportEnabled() {
        return this.autoImportEnabled;
    }

    @Override
    public String getImplicitCascadeStyleName() {
        return this.implicitCascadeStyleName;
    }

    @Override
    public String getImplicitPropertyAccessorName() {
        return this.implicitPropertyAccessorName;
    }

    @Override
    public boolean areEntitiesImplicitlyLazy() {
        return this.entitiesImplicitlyLazy;
    }

    @Override
    public boolean areCollectionsImplicitlyLazy() {
        return this.pluralAttributesImplicitlyLazy;
    }

    @Override
    public AccessType getImplicitCacheAccessType() {
        return this.implicitCacheAccessType;
    }

    public static class Builder {
        private String implicitSchemaName;
        private String implicitCatalogName;
        private boolean implicitlyQuoteIdentifiers;
        private String implicitIdColumnName;
        private String implicitTenantIdColumnName;
        private String implicitDiscriminatorColumnName;
        private String implicitPackageName;
        private boolean autoImportEnabled;
        private String implicitCascadeStyleName;
        private String implicitPropertyAccessorName;
        private boolean entitiesImplicitlyLazy;
        private boolean pluralAttributesImplicitlyLazy;
        private AccessType implicitCacheAccessType;

        public Builder(MappingDefaults parentDefaults) {
            this.implicitSchemaName = parentDefaults.getImplicitSchemaName();
            this.implicitCatalogName = parentDefaults.getImplicitCatalogName();
            this.implicitlyQuoteIdentifiers = parentDefaults.shouldImplicitlyQuoteIdentifiers();
            this.implicitIdColumnName = parentDefaults.getImplicitIdColumnName();
            this.implicitTenantIdColumnName = parentDefaults.getImplicitTenantIdColumnName();
            this.implicitDiscriminatorColumnName = parentDefaults.getImplicitDiscriminatorColumnName();
            this.implicitPackageName = parentDefaults.getImplicitPackageName();
            this.autoImportEnabled = parentDefaults.isAutoImportEnabled();
            this.implicitCascadeStyleName = parentDefaults.getImplicitCascadeStyleName();
            this.implicitPropertyAccessorName = parentDefaults.getImplicitPropertyAccessorName();
            this.entitiesImplicitlyLazy = parentDefaults.areEntitiesImplicitlyLazy();
            this.pluralAttributesImplicitlyLazy = parentDefaults.areCollectionsImplicitlyLazy();
            this.implicitCacheAccessType = parentDefaults.getImplicitCacheAccessType();
        }

        public Builder setImplicitSchemaName(String implicitSchemaName) {
            if (StringHelper.isNotEmpty(implicitSchemaName)) {
                this.implicitSchemaName = implicitSchemaName;
            }
            return this;
        }

        public Builder setImplicitCatalogName(String implicitCatalogName) {
            if (StringHelper.isNotEmpty(implicitCatalogName)) {
                this.implicitCatalogName = implicitCatalogName;
            }
            return this;
        }

        public Builder setImplicitlyQuoteIdentifiers(boolean implicitlyQuoteIdentifiers) {
            this.implicitlyQuoteIdentifiers = implicitlyQuoteIdentifiers;
            return this;
        }

        public Builder setImplicitIdColumnName(String implicitIdColumnName) {
            if (StringHelper.isNotEmpty(implicitIdColumnName)) {
                this.implicitIdColumnName = implicitIdColumnName;
            }
            return this;
        }

        public Builder setImplicitTenantIdColumnName(String implicitTenantIdColumnName) {
            if (StringHelper.isNotEmpty(implicitTenantIdColumnName)) {
                this.implicitTenantIdColumnName = implicitTenantIdColumnName;
            }
            return this;
        }

        public Builder setImplicitDiscriminatorColumnName(String implicitDiscriminatorColumnName) {
            if (StringHelper.isNotEmpty(implicitDiscriminatorColumnName)) {
                this.implicitDiscriminatorColumnName = implicitDiscriminatorColumnName;
            }
            return this;
        }

        public Builder setImplicitPackageName(String implicitPackageName) {
            if (StringHelper.isNotEmpty(implicitPackageName)) {
                this.implicitPackageName = implicitPackageName;
            }
            return this;
        }

        public Builder setAutoImportEnabled(boolean autoImportEnabled) {
            this.autoImportEnabled = autoImportEnabled;
            return this;
        }

        public Builder setImplicitCascadeStyleName(String implicitCascadeStyleName) {
            if (StringHelper.isNotEmpty(implicitCascadeStyleName)) {
                this.implicitCascadeStyleName = implicitCascadeStyleName;
            }
            return this;
        }

        public Builder setImplicitPropertyAccessorName(String implicitPropertyAccessorName) {
            if (StringHelper.isNotEmpty(implicitPropertyAccessorName)) {
                this.implicitPropertyAccessorName = implicitPropertyAccessorName;
            }
            return this;
        }

        public Builder setEntitiesImplicitlyLazy(boolean entitiesImplicitlyLazy) {
            this.entitiesImplicitlyLazy = entitiesImplicitlyLazy;
            return this;
        }

        public Builder setPluralAttributesImplicitlyLazy(boolean pluralAttributesImplicitlyLazy) {
            this.pluralAttributesImplicitlyLazy = pluralAttributesImplicitlyLazy;
            return this;
        }

        public Builder setImplicitCacheAccessType(AccessType implicitCacheAccessType) {
            if (implicitCacheAccessType != null) {
                this.implicitCacheAccessType = implicitCacheAccessType;
            }
            return this;
        }

        public OverriddenMappingDefaults build() {
            return new OverriddenMappingDefaults(this.implicitSchemaName, this.implicitCatalogName, this.implicitlyQuoteIdentifiers, this.implicitIdColumnName, this.implicitTenantIdColumnName, this.implicitDiscriminatorColumnName, this.implicitPackageName, this.autoImportEnabled, this.implicitCascadeStyleName, this.implicitPropertyAccessorName, this.entitiesImplicitlyLazy, this.pluralAttributesImplicitlyLazy, this.implicitCacheAccessType);
        }
    }
}

