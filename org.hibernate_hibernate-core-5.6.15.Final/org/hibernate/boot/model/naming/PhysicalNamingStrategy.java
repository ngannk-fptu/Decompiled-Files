/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public interface PhysicalNamingStrategy {
    public Identifier toPhysicalCatalogName(Identifier var1, JdbcEnvironment var2);

    public Identifier toPhysicalSchemaName(Identifier var1, JdbcEnvironment var2);

    public Identifier toPhysicalTableName(Identifier var1, JdbcEnvironment var2);

    public Identifier toPhysicalSequenceName(Identifier var1, JdbcEnvironment var2);

    public Identifier toPhysicalColumnName(Identifier var1, JdbcEnvironment var2);
}

