/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.internal;

import org.hibernate.dialect.Database;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

public final class StandardDialectResolver
implements DialectResolver {
    @Override
    public Dialect resolveDialect(DialectResolutionInfo info) {
        for (Database database : Database.values()) {
            Dialect dialect = database.resolveDialect(info);
            if (dialect == null) continue;
            return dialect;
        }
        return null;
    }
}

