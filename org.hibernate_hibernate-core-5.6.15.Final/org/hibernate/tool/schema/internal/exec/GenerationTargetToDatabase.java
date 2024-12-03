/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import org.hibernate.engine.jdbc.internal.DDLFormatterImpl;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;

public class GenerationTargetToDatabase
implements GenerationTarget {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(GenerationTargetToDatabase.class);
    private final DdlTransactionIsolator ddlTransactionIsolator;
    private final boolean releaseAfterUse;
    private Statement jdbcStatement;

    public GenerationTargetToDatabase(DdlTransactionIsolator ddlTransactionIsolator) {
        this(ddlTransactionIsolator, true);
    }

    public GenerationTargetToDatabase(DdlTransactionIsolator ddlTransactionIsolator, boolean releaseAfterUse) {
        this.ddlTransactionIsolator = ddlTransactionIsolator;
        this.releaseAfterUse = releaseAfterUse;
    }

    @Override
    public void prepare() {
    }

    @Override
    public void accept(String command) {
        this.ddlTransactionIsolator.getJdbcContext().getSqlStatementLogger().logStatement(command, DDLFormatterImpl.INSTANCE);
        try {
            Statement jdbcStatement = this.jdbcStatement();
            jdbcStatement.execute(command);
            try {
                SQLWarning warnings = jdbcStatement.getWarnings();
                if (warnings != null) {
                    this.ddlTransactionIsolator.getJdbcContext().getSqlExceptionHelper().logAndClearWarnings(jdbcStatement);
                }
            }
            catch (SQLException e) {
                log.unableToLogSqlWarnings(e);
            }
        }
        catch (SQLException e) {
            throw new CommandAcceptanceException("Error executing DDL \"" + command + "\" via JDBC Statement", e);
        }
    }

    private Statement jdbcStatement() {
        if (this.jdbcStatement == null) {
            try {
                this.jdbcStatement = this.ddlTransactionIsolator.getIsolatedConnection().createStatement();
            }
            catch (SQLException e) {
                throw this.ddlTransactionIsolator.getJdbcContext().getSqlExceptionHelper().convert(e, "Unable to create JDBC Statement for DDL execution");
            }
        }
        return this.jdbcStatement;
    }

    @Override
    public void release() {
        if (this.jdbcStatement != null) {
            try {
                this.jdbcStatement.close();
                this.jdbcStatement = null;
            }
            catch (SQLException e) {
                throw this.ddlTransactionIsolator.getJdbcContext().getSqlExceptionHelper().convert(e, "Unable to close JDBC Statement after DDL execution");
            }
        }
        if (this.releaseAfterUse) {
            this.ddlTransactionIsolator.release();
        }
    }
}

