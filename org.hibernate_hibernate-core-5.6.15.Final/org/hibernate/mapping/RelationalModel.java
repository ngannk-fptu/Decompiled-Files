/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.HibernateException;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.model.relational.internal.SqlStringGenerationContextImpl;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.Mapping;

@Deprecated
public interface RelationalModel {
    @Deprecated
    default public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) throws HibernateException {
        return this.sqlCreateString(p, SqlStringGenerationContextImpl.forBackwardsCompatibility(dialect, defaultCatalog, defaultSchema), defaultCatalog, defaultSchema);
    }

    public String sqlCreateString(Mapping var1, SqlStringGenerationContext var2, String var3, String var4) throws HibernateException;

    @Deprecated
    default public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) throws HibernateException {
        return this.sqlDropString(SqlStringGenerationContextImpl.forBackwardsCompatibility(dialect, defaultCatalog, defaultSchema), defaultCatalog, defaultSchema);
    }

    public String sqlDropString(SqlStringGenerationContext var1, String var2, String var3);
}

