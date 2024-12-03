/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.MustBeClosed
 *  net.jcip.annotations.ThreadSafe
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.boot.model.naming.Identifier
 *  org.hibernate.dialect.Dialect
 *  org.hibernate.tool.schema.extract.spi.DatabaseInformation
 */
package com.atlassian.confluence.core.persistence.schema.api;

import com.google.errorprone.annotations.MustBeClosed;
import java.sql.SQLException;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;

@ThreadSafe
public interface SchemaInformationService {
    @MustBeClosed
    public @NonNull CloseableDatabaseInformation getDatabaseInformation() throws SQLException;

    public Dialect getDialect();

    public Identifier getCurrentCatalog();

    public Identifier getCurrentSchema();

    public static interface CloseableDatabaseInformation
    extends DatabaseInformation,
    AutoCloseable {
        @Override
        public void close();
    }
}

