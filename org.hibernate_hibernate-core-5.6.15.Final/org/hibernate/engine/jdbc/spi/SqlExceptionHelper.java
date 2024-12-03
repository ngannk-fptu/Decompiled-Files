/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.engine.jdbc.spi;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import org.hibernate.JDBCException;
import org.hibernate.exception.internal.SQLStateConverter;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.jboss.logging.Logger;

public class SqlExceptionHelper {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)SqlExceptionHelper.class.getName());
    private static final String DEFAULT_EXCEPTION_MSG = "SQL Exception";
    private static final String DEFAULT_WARNING_MSG = "SQL Warning";
    private final boolean logWarnings;
    private static final SQLExceptionConverter DEFAULT_CONVERTER = new SQLStateConverter(new ViolatedConstraintNameExtracter(){

        @Override
        public String extractConstraintName(SQLException e) {
            return null;
        }
    });
    private SQLExceptionConverter sqlExceptionConverter;
    public static final StandardWarningHandler STANDARD_WARNING_HANDLER = new StandardWarningHandler("SQL Warning");

    public SqlExceptionHelper(boolean logWarnings) {
        this(DEFAULT_CONVERTER, logWarnings);
    }

    public SqlExceptionHelper(SQLExceptionConverter sqlExceptionConverter, boolean logWarnings) {
        this.sqlExceptionConverter = sqlExceptionConverter;
        this.logWarnings = logWarnings;
    }

    public SQLExceptionConverter getSqlExceptionConverter() {
        return this.sqlExceptionConverter;
    }

    public void setSqlExceptionConverter(SQLExceptionConverter sqlExceptionConverter) {
        this.sqlExceptionConverter = sqlExceptionConverter == null ? DEFAULT_CONVERTER : sqlExceptionConverter;
    }

    public JDBCException convert(SQLException sqlException, String message) {
        return this.convert(sqlException, message, "n/a");
    }

    public JDBCException convert(SQLException sqlException, String message, String sql) {
        this.logExceptions(sqlException, message + " [" + sql + "]");
        return this.sqlExceptionConverter.convert(sqlException, message, sql);
    }

    public void logExceptions(SQLException sqlException, String message) {
        if (LOG.isEnabled(Logger.Level.ERROR)) {
            if (LOG.isDebugEnabled()) {
                message = StringHelper.isNotEmpty(message) ? message : DEFAULT_EXCEPTION_MSG;
                LOG.debug(message, sqlException);
            }
            boolean warnEnabled = LOG.isEnabled(Logger.Level.WARN);
            ArrayList<String> previousWarnMessages = new ArrayList<String>();
            ArrayList<String> previousErrorMessages = new ArrayList<String>();
            while (sqlException != null) {
                String warnMessage;
                if (warnEnabled && !previousWarnMessages.contains(warnMessage = "SQL Error: " + sqlException.getErrorCode() + ", SQLState: " + sqlException.getSQLState())) {
                    LOG.warn(warnMessage);
                    previousWarnMessages.add(warnMessage);
                }
                if (!previousErrorMessages.contains(sqlException.getMessage())) {
                    LOG.error(sqlException.getMessage());
                    previousErrorMessages.add(sqlException.getMessage());
                }
                sqlException = sqlException.getNextException();
            }
        }
    }

    public void walkWarnings(SQLWarning warning, WarningHandler handler) {
        if (warning == null || !handler.doProcess()) {
            return;
        }
        handler.prepare(warning);
        while (warning != null) {
            handler.handleWarning(warning);
            warning = warning.getNextWarning();
        }
    }

    public void logAndClearWarnings(Connection connection) {
        this.handleAndClearWarnings(connection, (WarningHandler)STANDARD_WARNING_HANDLER);
    }

    public void logAndClearWarnings(Statement statement) {
        this.handleAndClearWarnings(statement, (WarningHandler)STANDARD_WARNING_HANDLER);
    }

    public void handleAndClearWarnings(Connection connection, WarningHandler handler) {
        try {
            if (this.logWarnings) {
                this.walkWarnings(connection.getWarnings(), handler);
            }
        }
        catch (SQLException sqle) {
            LOG.debug("could not log warnings", sqle);
        }
        try {
            connection.clearWarnings();
        }
        catch (SQLException sqle) {
            LOG.debug("could not clear warnings", sqle);
        }
    }

    public void handleAndClearWarnings(Statement statement, WarningHandler handler) {
        if (this.logWarnings) {
            try {
                this.walkWarnings(statement.getWarnings(), handler);
            }
            catch (SQLException sqlException) {
                LOG.debug("could not log warnings", sqlException);
            }
        }
        try {
            statement.clearWarnings();
        }
        catch (SQLException sqle) {
            LOG.debug("could not clear warnings", sqle);
        }
    }

    public static class StandardWarningHandler
    extends WarningHandlerLoggingSupport {
        private final String introMessage;

        public StandardWarningHandler(String introMessage) {
            this.introMessage = introMessage;
        }

        @Override
        public boolean doProcess() {
            return LOG.isEnabled(Logger.Level.WARN);
        }

        @Override
        public void prepare(SQLWarning warning) {
            LOG.debug(this.introMessage, warning);
        }

        @Override
        protected void logWarning(String description, String message) {
            LOG.warn(description);
            LOG.warn(message);
        }
    }

    public static abstract class WarningHandlerLoggingSupport
    implements WarningHandler {
        @Override
        public final void handleWarning(SQLWarning warning) {
            this.logWarning("SQL Warning Code: " + warning.getErrorCode() + ", SQLState: " + warning.getSQLState(), warning.getMessage());
        }

        protected abstract void logWarning(String var1, String var2);
    }

    public static interface WarningHandler {
        public boolean doProcess();

        public void prepare(SQLWarning var1);

        public void handleWarning(SQLWarning var1);
    }
}

