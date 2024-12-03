/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.dialect.spi;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.dialect.spi.BasicSQLExceptionConverter;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

public class DatabaseMetaDataDialectResolutionInfoAdapter
implements DialectResolutionInfo {
    private final DatabaseMetaData databaseMetaData;

    public DatabaseMetaDataDialectResolutionInfoAdapter(DatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }

    @Override
    public String getDatabaseName() {
        try {
            return this.databaseMetaData.getDatabaseProductName();
        }
        catch (SQLException e) {
            throw BasicSQLExceptionConverter.INSTANCE.convert(e);
        }
    }

    @Override
    public int getDatabaseMajorVersion() {
        try {
            return DatabaseMetaDataDialectResolutionInfoAdapter.interpretVersion(this.databaseMetaData.getDatabaseMajorVersion());
        }
        catch (SQLException e) {
            throw BasicSQLExceptionConverter.INSTANCE.convert(e);
        }
    }

    private static int interpretVersion(int result) {
        return result < 0 ? -9999 : result;
    }

    @Override
    public int getDatabaseMinorVersion() {
        try {
            return DatabaseMetaDataDialectResolutionInfoAdapter.interpretVersion(this.databaseMetaData.getDatabaseMinorVersion());
        }
        catch (SQLException e) {
            throw BasicSQLExceptionConverter.INSTANCE.convert(e);
        }
    }

    @Override
    public String getDriverName() {
        try {
            return this.databaseMetaData.getDriverName();
        }
        catch (SQLException e) {
            throw BasicSQLExceptionConverter.INSTANCE.convert(e);
        }
    }

    @Override
    public int getDriverMajorVersion() {
        return DatabaseMetaDataDialectResolutionInfoAdapter.interpretVersion(this.databaseMetaData.getDriverMajorVersion());
    }

    @Override
    public int getDriverMinorVersion() {
        return DatabaseMetaDataDialectResolutionInfoAdapter.interpretVersion(this.databaseMetaData.getDriverMinorVersion());
    }
}

