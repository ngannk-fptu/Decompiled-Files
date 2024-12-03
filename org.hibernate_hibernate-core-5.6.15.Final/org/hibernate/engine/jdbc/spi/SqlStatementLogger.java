/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.jdbc.spi;

import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.build.AllowSysOut;
import org.jboss.logging.Logger;

public class SqlStatementLogger {
    private static final Logger LOG = CoreLogging.logger("org.hibernate.SQL");
    private static final Logger LOG_SLOW = CoreLogging.logger("org.hibernate.SQL_SLOW");
    private boolean logToStdout;
    private boolean format;
    private final boolean highlight;
    private final long logSlowQuery;

    public SqlStatementLogger() {
        this(false, false, false);
    }

    public SqlStatementLogger(boolean logToStdout, boolean format) {
        this(logToStdout, format, false);
    }

    public SqlStatementLogger(boolean logToStdout, boolean format, boolean highlight) {
        this(logToStdout, format, highlight, 0L);
    }

    public SqlStatementLogger(boolean logToStdout, boolean format, boolean highlight, long logSlowQuery) {
        this.logToStdout = logToStdout;
        this.format = format;
        this.highlight = highlight;
        this.logSlowQuery = logSlowQuery;
    }

    public boolean isLogToStdout() {
        return this.logToStdout;
    }

    @Deprecated
    public void setLogToStdout(boolean logToStdout) {
        this.logToStdout = logToStdout;
    }

    public boolean isFormat() {
        return this.format;
    }

    @Deprecated
    public void setFormat(boolean format) {
        this.format = format;
    }

    public long getLogSlowQuery() {
        return this.logSlowQuery;
    }

    public void logStatement(String statement) {
        this.logStatement(statement, FormatStyle.BASIC.getFormatter());
    }

    @AllowSysOut
    public void logStatement(String statement, Formatter formatter) {
        if (this.logToStdout || LOG.isDebugEnabled()) {
            if (this.format) {
                statement = formatter.format(statement);
            }
            if (this.highlight) {
                statement = FormatStyle.HIGHLIGHT.getFormatter().format(statement);
            }
        }
        LOG.debug((Object)statement);
        if (this.logToStdout) {
            String prefix = this.highlight ? "\u001b[35m[Hibernate]\u001b[0m " : "Hibernate: ";
            System.out.println(prefix + statement);
        }
    }

    public void logSlowQuery(Statement statement, long startTimeNanos) {
        if (this.logSlowQuery < 1L) {
            return;
        }
        this.logSlowQuery(statement.toString(), startTimeNanos);
    }

    @AllowSysOut
    public void logSlowQuery(String sql, long startTimeNanos) {
        if (this.logSlowQuery < 1L) {
            return;
        }
        if (startTimeNanos <= 0L) {
            throw new IllegalArgumentException("startTimeNanos [" + startTimeNanos + "] should be greater than 0!");
        }
        long queryExecutionMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTimeNanos);
        if (queryExecutionMillis > this.logSlowQuery) {
            String logData = "SlowQuery: " + queryExecutionMillis + " milliseconds. SQL: '" + sql + "'";
            LOG_SLOW.info((Object)logData);
            if (this.logToStdout) {
                System.out.println(logData);
            }
        }
    }
}

