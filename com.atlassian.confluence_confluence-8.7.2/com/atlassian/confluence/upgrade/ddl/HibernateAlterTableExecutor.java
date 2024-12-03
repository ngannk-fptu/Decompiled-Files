/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.BadSqlGrammarException
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.upgrade.ddl.AddUniqueConstraintCommand;
import com.atlassian.confluence.upgrade.ddl.AlterColumnNullabilityCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableExecutor;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DropUniqueConstraintByColumnsCommand;
import com.atlassian.confluence.upgrade.ddl.DropUniqueConstraintCommand;
import com.atlassian.confluence.upgrade.ddl.NullChoice;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

public class HibernateAlterTableExecutor
implements AlterTableExecutor {
    private static final Logger log = LoggerFactory.getLogger(HibernateAlterTableExecutor.class);
    private static final String ORACLE_COLUMN_ALREADY_NOT_NULL = "ORA-01442";
    private static final String ORACLE_COLUMN_ALREADY_NULL = "ORA-01451";
    private static final String ORACLE_KEY_ALREADY_EXISTS = "ORA-02261";
    public static final String POSTGRES_RELATION_ALREADY_EXISTS_SQLSTATE = "42P07";
    public static final String MYSQL_RELATION_ALREADY_EXISTS_SQLSTATE = "42000";
    public static final int MYSQL_RELATION_ALREADY_EXISTS_ERRORCODE = 1061;
    public static final int SQLSERVER_INDEX_ALREADY_EXISTS_ERRORCODE = 1913;
    public static final int SQLSERVER_RELATION_ALREADY_EXISTS_ERRORCODE = 2714;
    public static final String HSQLDB_RELATION_ALREADY_EXISTS_SQLSTATE = "42504";
    public static final int HSQLDB_RELATION_ALREADY_EXISTS_ERRORCODE = -5504;
    private final HibernateDatabaseCapabilities databaseCapabilities;
    private final DdlExecutor ddlExecutor;

    public HibernateAlterTableExecutor(HibernateDatabaseCapabilities databaseCapabilities, DdlExecutor ddlExecutor) {
        this.databaseCapabilities = databaseCapabilities;
        this.ddlExecutor = ddlExecutor;
    }

    @Override
    public AlterColumnNullabilityCommand createAlterColumnNullChoiceCommand(String columnName, String oldDataType, NullChoice nullChoice) {
        return new AlterColumnNullabilityCommand(this.databaseCapabilities, columnName, oldDataType, nullChoice);
    }

    @Override
    public AddUniqueConstraintCommand createAddUniqueConstraintCommand(String constraintName, String ... columnNames) {
        Preconditions.checkArgument((columnNames.length != 0 ? 1 : 0) != 0);
        return new AddUniqueConstraintCommand(constraintName, Arrays.asList(columnNames));
    }

    @Override
    public DropUniqueConstraintCommand createDropUniqueConstraintCommand(String constraintName) {
        return new DropUniqueConstraintCommand(this.databaseCapabilities, constraintName);
    }

    @Override
    public DropUniqueConstraintCommand createDropUniqueConstraintIfExistsCommand(String constraintName) {
        return new DropUniqueConstraintCommand(this.databaseCapabilities, constraintName, true);
    }

    @Override
    public DropUniqueConstraintByColumnsCommand createDropUniqueConstraintByColumnsCommand(String ... columnNames) {
        if (!this.databaseCapabilities.isOracle()) {
            throw new IllegalArgumentException("Drop unique constraint by columns is only supported by Oracle");
        }
        return new DropUniqueConstraintByColumnsCommand(columnNames);
    }

    @Override
    public void alterTable(String tableName, List<? extends AlterTableCommand> commands) {
        try {
            log.info("Executing grouped alter table command on {}", (Object)tableName);
            this.alterTableGrouped(tableName, commands);
        }
        catch (DataAccessException e) {
            if (this.isIgnorableException(e)) {
                log.info("Executing ungrouped alter table commands on {}", (Object)tableName);
                this.alterTableUngrouped(tableName, commands);
                return;
            }
            throw e;
        }
    }

    private void alterTableGrouped(String tableName, List<? extends AlterTableCommand> commands) {
        List<String> statements = this.getAlterTableStatements(tableName, commands, false);
        this.ddlExecutor.executeDdlStatements(statements);
    }

    private void alterTableUngrouped(String tableName, List<? extends AlterTableCommand> commands) {
        List<String> statements = this.getAlterTableStatements(tableName, commands, true);
        for (String sql : statements) {
            try {
                this.ddlExecutor.executeDdlStatements(Lists.newArrayList((Object[])new String[]{sql}));
            }
            catch (DataAccessException e) {
                if (this.isIgnorableException(e)) {
                    log.info("Database is reporting that the column already has the property that we want. SQL: " + sql);
                    continue;
                }
                log.error("Failed to run alter table SQL: {}", (Object)sql);
                throw e;
            }
        }
    }

    private boolean isIgnorableException(DataAccessException e) {
        String message = e.getMessage();
        String sqlState = null;
        int errorCode = 0;
        if (e instanceof BadSqlGrammarException) {
            BadSqlGrammarException badSqlGrammarException = (BadSqlGrammarException)e;
            SQLException sqlException = badSqlGrammarException.getSQLException();
            sqlState = sqlException.getSQLState();
            errorCode = sqlException.getErrorCode();
        }
        if (this.databaseCapabilities.isMySql()) {
            return MYSQL_RELATION_ALREADY_EXISTS_SQLSTATE.equals(sqlState) && 1061 == errorCode;
        }
        if (this.databaseCapabilities.isOracle()) {
            return message.contains(ORACLE_COLUMN_ALREADY_NOT_NULL) || message.contains(ORACLE_COLUMN_ALREADY_NULL) || message.contains(ORACLE_KEY_ALREADY_EXISTS);
        }
        if (this.databaseCapabilities.isPostgreSql()) {
            return POSTGRES_RELATION_ALREADY_EXISTS_SQLSTATE.equals(sqlState);
        }
        if (this.databaseCapabilities.isSqlServer()) {
            return 1913 == errorCode || 2714 == errorCode;
        }
        if (this.databaseCapabilities.isHSQL()) {
            return HSQLDB_RELATION_ALREADY_EXISTS_SQLSTATE.equals(sqlState) && -5504 == errorCode;
        }
        return false;
    }

    static boolean isGroupable(List<? extends AlterTableCommand> commands) {
        AlterTableCommand lastCommand = null;
        for (AlterTableCommand alterTableCommand : commands) {
            if (lastCommand != null && alterTableCommand.getCommandName().equals(lastCommand.getCommandName())) {
                return true;
            }
            lastCommand = alterTableCommand;
        }
        return false;
    }

    @Override
    public List<String> getAlterTableStatements(String tableName, List<? extends AlterTableCommand> commands) {
        return this.getAlterTableStatements(tableName, commands, true);
    }

    List<String> getAlterTableStatements(String tableName, List<? extends AlterTableCommand> commands, boolean forceOneStatementPerAction) {
        String alterTablePrefix = "alter table " + tableName + " ";
        if (!forceOneStatementPerAction) {
            if (this.databaseCapabilities.isOracle()) {
                return this.getGroupedOracleAlterTableStatements(alterTablePrefix, commands);
            }
            if (this.databaseCapabilities.isPostgreSql() || this.databaseCapabilities.isMySql()) {
                return this.getGroupedAlterTableStatements(alterTablePrefix, commands);
            }
        }
        return this.getSingleAlterTableStatements(alterTablePrefix, commands);
    }

    private List<String> getSingleAlterTableStatements(String alterTablePrefix, List<? extends AlterTableCommand> commands) {
        ArrayList<String> statements = new ArrayList<String>();
        StringBuilder sb = new StringBuilder(16 + alterTablePrefix.length());
        for (AlterTableCommand alterTableCommand : commands) {
            sb.append(alterTablePrefix);
            sb.append(HibernateAlterTableExecutor.toSqlSnippet(alterTableCommand));
            statements.add(sb.toString());
            sb.setLength(0);
        }
        return statements;
    }

    private List<String> getGroupedAlterTableStatements(String alterTablePrefix, List<? extends AlterTableCommand> commands) {
        ArrayList<String> statements = new ArrayList<String>();
        StringBuilder sb = new StringBuilder(16 + alterTablePrefix.length());
        sb.append(alterTablePrefix);
        String delimiter = ", ";
        Iterator<? extends AlterTableCommand> i = commands.iterator();
        while (i.hasNext()) {
            AlterTableCommand command = i.next();
            sb.append(HibernateAlterTableExecutor.toSqlSnippet(command));
            if (!i.hasNext()) continue;
            sb.append(", ");
        }
        statements.add(sb.toString());
        return statements;
    }

    private List<String> getGroupedOracleAlterTableStatements(String alterTablePrefix, List<? extends AlterTableCommand> commands) {
        ArrayList<String> statements = new ArrayList<String>();
        StringBuilder sb = new StringBuilder(16 + alterTablePrefix.length());
        String lastActionName = null;
        ArrayList<AlterTableCommand> sameActionCommands = new ArrayList<AlterTableCommand>();
        for (AlterTableCommand alterTableCommand : commands) {
            String actionName = alterTableCommand.getCommandName();
            if (!actionName.equals(lastActionName)) {
                if (!sameActionCommands.isEmpty()) {
                    sb.append(alterTablePrefix);
                    HibernateAlterTableExecutor.createOracleAlterTableBody(sb, sameActionCommands);
                    statements.add(sb.toString());
                    sameActionCommands.clear();
                    sb.setLength(0);
                }
                sameActionCommands.add(alterTableCommand);
            } else {
                sameActionCommands.add(alterTableCommand);
            }
            lastActionName = actionName;
        }
        if (!sameActionCommands.isEmpty()) {
            sb.append(alterTablePrefix);
            HibernateAlterTableExecutor.createOracleAlterTableBody(sb, sameActionCommands);
            statements.add(sb.toString());
        }
        return statements;
    }

    private static StringBuilder createOracleAlterTableBody(StringBuilder sb, List<? extends AlterTableCommand> sameCommands) {
        if (sameCommands.isEmpty()) {
            throw new IllegalArgumentException("List of commands passed to create oracle alter table body must not be empty");
        }
        AlterTableCommand first = sameCommands.get(0);
        if (sameCommands.size() == 1) {
            sb.append(HibernateAlterTableExecutor.toSqlSnippet(first));
        } else {
            sb.append(first.getCommandName()).append(" ( ");
            Iterator<? extends AlterTableCommand> itr = sameCommands.iterator();
            while (itr.hasNext()) {
                AlterTableCommand command = itr.next();
                sb.append(command.getCommandParameters());
                if (!itr.hasNext()) continue;
                sb.append(", ");
            }
            sb.append(" )");
        }
        return sb;
    }

    private static String toSqlSnippet(AlterTableCommand command) {
        return command.getCommandName() + " " + command.getCommandParameters();
    }
}

