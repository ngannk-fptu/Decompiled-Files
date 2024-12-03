/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

import java.sql.Connection;

public interface SchemaNameResolver {
    public String resolveSchemaName(Connection var1);
}

