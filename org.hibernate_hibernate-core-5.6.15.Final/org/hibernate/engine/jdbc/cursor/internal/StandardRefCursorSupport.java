/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.cursor.internal;

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.cursor.spi.RefCursorSupport;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.spi.InjectService;
import org.jboss.logging.Logger;

public class StandardRefCursorSupport
implements RefCursorSupport {
    private static final Logger log = Logger.getLogger(StandardRefCursorSupport.class);
    private JdbcServices jdbcServices;

    @InjectService
    public void injectJdbcServices(JdbcServices jdbcServices) {
        this.jdbcServices = jdbcServices;
    }

    @Override
    public void registerRefCursorParameter(CallableStatement statement, int position) {
        if (this.jdbcServices.getExtractedMetaDataSupport().supportsRefCursors()) {
            try {
                statement.registerOutParameter(position, this.refCursorTypeCode());
            }
            catch (SQLException e) {
                throw this.jdbcServices.getSqlExceptionHelper().convert(e, "Error registering REF_CURSOR parameter [" + position + "]");
            }
        }
        try {
            this.jdbcServices.getDialect().registerResultSetOutParameter(statement, position);
        }
        catch (SQLException e) {
            throw this.jdbcServices.getSqlExceptionHelper().convert(e, "Error asking dialect to register ref cursor parameter [" + position + "]");
        }
    }

    @Override
    public void registerRefCursorParameter(CallableStatement statement, String name) {
        if (this.jdbcServices.getExtractedMetaDataSupport().supportsRefCursors()) {
            try {
                statement.registerOutParameter(name, this.refCursorTypeCode());
            }
            catch (SQLException e) {
                throw this.jdbcServices.getSqlExceptionHelper().convert(e, "Error registering REF_CURSOR parameter [" + name + "]");
            }
        }
        try {
            this.jdbcServices.getDialect().registerResultSetOutParameter(statement, name);
        }
        catch (SQLException e) {
            throw this.jdbcServices.getSqlExceptionHelper().convert(e, "Error asking dialect to register ref cursor parameter [" + name + "]");
        }
    }

    @Override
    public ResultSet getResultSet(CallableStatement statement, int position) {
        if (this.jdbcServices.getExtractedMetaDataSupport().supportsRefCursors()) {
            try {
                return statement.getObject(position, ResultSet.class);
            }
            catch (Exception e) {
                throw new HibernateException("Unexpected error extracting REF_CURSOR parameter [" + position + "]", e);
            }
        }
        try {
            return this.jdbcServices.getDialect().getResultSet(statement, position);
        }
        catch (SQLException e) {
            throw this.jdbcServices.getSqlExceptionHelper().convert(e, "Error asking dialect to extract ResultSet from CallableStatement parameter [" + position + "]");
        }
    }

    @Override
    public ResultSet getResultSet(CallableStatement statement, String name) {
        if (this.jdbcServices.getExtractedMetaDataSupport().supportsRefCursors()) {
            try {
                return statement.getObject(name, ResultSet.class);
            }
            catch (Exception e) {
                throw new HibernateException("Unexpected error extracting REF_CURSOR parameter [" + name + "]", e);
            }
        }
        try {
            return this.jdbcServices.getDialect().getResultSet(statement, name);
        }
        catch (SQLException e) {
            throw this.jdbcServices.getSqlExceptionHelper().convert(e, "Error asking dialect to extract ResultSet from CallableStatement parameter [" + name + "]");
        }
    }

    public static boolean supportsRefCursors(DatabaseMetaData meta) {
        try {
            boolean mightSupportIt = meta.supportsRefCursors();
            if (mightSupportIt && "Oracle JDBC driver".equals(meta.getDriverName()) && meta.getDriverMajorVersion() < 19) {
                return false;
            }
            return mightSupportIt;
        }
        catch (Exception throwable) {
            log.debug((Object)("Unexpected error trying to gauge level of JDBC REF_CURSOR support : " + throwable.getMessage()));
            return false;
        }
    }

    private int refCursorTypeCode() {
        return 2012;
    }
}

