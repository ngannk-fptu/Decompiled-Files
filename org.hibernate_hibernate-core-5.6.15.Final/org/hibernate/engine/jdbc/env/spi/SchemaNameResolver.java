/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.env.spi;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.dialect.Dialect;

public interface SchemaNameResolver {
    public String resolveSchemaName(Connection var1, Dialect var2) throws SQLException;
}

