/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseCompatibilityKit;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.schema.DialectProvider;
import com.atlassian.pocketknife.api.querydsl.util.Connections;
import com.querydsl.sql.dml.SQLInsertClause;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCompatibilityKitImpl
implements DatabaseCompatibilityKit {
    private final DialectProvider dialectProvider;

    @Autowired
    public DatabaseCompatibilityKitImpl(DialectProvider dialectProvider) {
        this.dialectProvider = dialectProvider;
    }

    @Override
    public <T> T executeWithKey(DatabaseConnection connection, SQLInsertClause insertClause, Class<T> idClass) {
        if (this.isHSQLBefore20(connection)) {
            long howMany = insertClause.execute();
            if (howMany > 0L) {
                return DatabaseCompatibilityKitImpl.callHsqlIdentity(connection);
            }
            return null;
        }
        return insertClause.executeWithKey(idClass);
    }

    private boolean isHSQLBefore20(DatabaseConnection connection) {
        DialectProvider.DatabaseInfo databaseInfo = this.dialectProvider.getDialectConfig(connection.getJdbcConnection()).getDatabaseInfo();
        return databaseInfo.getSupportedDatabase() == DialectProvider.SupportedDatabase.HSQLDB && databaseInfo.getDatabaseMajorVersion() < 2;
    }

    private static <T> T callHsqlIdentity(DatabaseConnection connection) {
        T idValue2;
        ResultSet resultSet;
        PreparedStatement prepareStatement;
        block5: {
            Object idValue2;
            prepareStatement = null;
            resultSet = null;
            prepareStatement = connection.getJdbcConnection().prepareStatement("CALL IDENTITY()");
            resultSet = prepareStatement.executeQuery();
            if (!resultSet.next()) break block5;
            Object object = idValue2 = resultSet.getObject(1);
            Connections.close(resultSet);
            Connections.close(prepareStatement);
            return (T)object;
        }
        try {
            idValue2 = null;
        }
        catch (SQLException e) {
            try {
                throw new RuntimeException(e);
            }
            catch (Throwable throwable) {
                Connections.close(resultSet);
                Connections.close(prepareStatement);
                throw throwable;
            }
        }
        Connections.close(resultSet);
        Connections.close(prepareStatement);
        return idValue2;
    }
}

