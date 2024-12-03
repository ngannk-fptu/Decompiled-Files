/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnectionConverter;
import com.atlassian.pocketknife.api.querydsl.schema.DialectProvider;
import com.atlassian.pocketknife.internal.querydsl.DatabaseConnectionImpl;
import com.atlassian.pocketknife.internal.querydsl.SpecificBehaviourConnection;
import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.sql.SQLException;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public class DatabaseConnectionConverterImpl
implements DatabaseConnectionConverter {
    private final DialectProvider dialectProvider;

    @Autowired
    public DatabaseConnectionConverterImpl(DialectProvider dialectProvider) {
        this.dialectProvider = dialectProvider;
    }

    @Override
    public DatabaseConnection convert(Connection jdbcConnection) {
        return this.convertImpl(jdbcConnection, false);
    }

    @Override
    public DatabaseConnection convertExternallyManaged(Connection jdbcConnection) {
        return this.convertImpl(jdbcConnection, true);
    }

    private DatabaseConnection convertImpl(Connection jdbcConnection, boolean managedConnection) {
        this.assertNotClosed(jdbcConnection);
        DialectProvider.Config dialectConfig = this.getDialectConfig(jdbcConnection);
        SpecificBehaviourConnection specificBehaviourConnection = new SpecificBehaviourConnection(jdbcConnection);
        return new DatabaseConnectionImpl(dialectConfig, specificBehaviourConnection, managedConnection);
    }

    private DialectProvider.Config getDialectConfig(Connection jdbcConnection) {
        return this.dialectProvider.getDialectConfig(this.assertNotClosed(jdbcConnection));
    }

    private Connection assertNotClosed(Connection jdbcConnection) {
        try {
            Preconditions.checkState((!jdbcConnection.isClosed() ? 1 : 0) != 0);
        }
        catch (SQLException e) {
            throw new IllegalStateException("PKQDSL is unable to assert that the JDBC connection is not closed", e);
        }
        return jdbcConnection;
    }
}

