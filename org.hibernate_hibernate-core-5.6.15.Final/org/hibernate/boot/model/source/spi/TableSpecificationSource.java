/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

public interface TableSpecificationSource {
    public String getExplicitSchemaName();

    public String getExplicitCatalogName();

    public String getComment();
}

