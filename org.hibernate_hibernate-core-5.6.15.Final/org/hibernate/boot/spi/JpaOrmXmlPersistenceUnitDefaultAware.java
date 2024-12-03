/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

public interface JpaOrmXmlPersistenceUnitDefaultAware {
    public void apply(JpaOrmXmlPersistenceUnitDefaults var1);

    public static interface JpaOrmXmlPersistenceUnitDefaults {
        public String getDefaultSchemaName();

        public String getDefaultCatalogName();

        public boolean shouldImplicitlyQuoteIdentifiers();
    }
}

