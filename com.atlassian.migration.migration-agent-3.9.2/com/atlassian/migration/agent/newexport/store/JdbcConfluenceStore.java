/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.BadSqlGrammarException
 *  org.springframework.jdbc.UncategorizedSQLException
 *  org.springframework.jdbc.core.JdbcOperations
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 */
package com.atlassian.migration.agent.newexport.store;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.processor.RowProcessor;
import com.atlassian.migration.agent.rest.QueryFailedException;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.util.StopConditionCheckingUtil;
import com.atlassian.migration.agent.store.jpa.impl.ConfluenceWrapperDataSource;
import com.google.common.collect.ImmutableMap;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcConfluenceStore {
    private static final Logger log = LoggerFactory.getLogger(JdbcConfluenceStore.class);
    private static final int FETCHSIZE = 500;
    private static final int MYSQL_FETCHSIZE = Integer.MIN_VALUE;
    public static final String QUERY_FAILED_LOG = "Query failed at: {}";
    public static final String QUERY_FAILED_AT = "Query failed at: ";
    private final NamedParameterJdbcTemplate jdbcTemplateAutoCommitOff;
    private final NamedParameterJdbcTemplate jdbcTemplateAutoCommitOn;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final DataSource dataSourceAutoCommitOff;

    public JdbcConfluenceStore(ConfluenceWrapperDataSource dataSource, MigrationAgentConfiguration config) {
        this.dataSourceAutoCommitOff = new ConfluenceWrapperDataSource(dataSource.getConnectionProvider()){

            @Override
            public Connection getConnection() throws SQLException {
                Connection connection = super.getConnection();
                connection.setAutoCommit(false);
                return connection;
            }
        };
        ConfluenceWrapperDataSource dataSourceAutoCommitOn = new ConfluenceWrapperDataSource(dataSource.getConnectionProvider()){

            @Override
            public Connection getConnection() throws SQLException {
                Connection connection = super.getConnection();
                connection.setAutoCommit(true);
                return connection;
            }
        };
        this.migrationAgentConfiguration = config;
        int fetchSize = this.migrationAgentConfiguration.getDBType() == DbType.MYSQL ? Integer.MIN_VALUE : 500;
        JdbcTemplate jdbcTemplateACOff = new JdbcTemplate(this.dataSourceAutoCommitOff);
        jdbcTemplateACOff.setFetchSize(fetchSize);
        this.jdbcTemplateAutoCommitOff = new NamedParameterJdbcTemplate((JdbcOperations)jdbcTemplateACOff);
        JdbcTemplate jdbcTemplateACOn = new JdbcTemplate((DataSource)((Object)dataSourceAutoCommitOn));
        this.jdbcTemplateAutoCommitOn = new NamedParameterJdbcTemplate((JdbcOperations)jdbcTemplateACOn);
    }

    public DbType getDbType() {
        return this.migrationAgentConfiguration.getDBType();
    }

    @VisibleForTesting
    public JdbcConfluenceStore(NamedParameterJdbcTemplate jdbcTemplateAutoCommitOff, NamedParameterJdbcTemplate jdbcTemplateAutoCommitOn, MigrationAgentConfiguration migrationAgentConfiguration, DataSource dataSourceAutoCommitOff) {
        this.jdbcTemplateAutoCommitOn = jdbcTemplateAutoCommitOn;
        this.jdbcTemplateAutoCommitOff = jdbcTemplateAutoCommitOff;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.dataSourceAutoCommitOff = dataSourceAutoCommitOff;
    }

    public void queryAndProcess(Query query, Map<String, ?> paramMap, RowProcessor processor) {
        StopConditionCheckingUtil.throwIfStopConditionWasReached();
        try {
            this.jdbcTemplateAutoCommitOff.query(query.sql, paramMap, rs -> {
                processor.initialise(rs, query);
                while (rs.next()) {
                    StopConditionCheckingUtil.throwIfStopConditionWasReached();
                    processor.process(rs);
                }
                return null;
            });
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception | OutOfMemoryError ex) {
            throw new QueryFailedException(QUERY_FAILED_AT + query.sql, ex);
        }
    }

    public int mutate(Query query, Map<String, ?> params) {
        try {
            return this.jdbcTemplateAutoCommitOn.update(query.sql, params);
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception ex) {
            log.error(QUERY_FAILED_LOG, (Object)query.sql, (Object)ex);
            throw new QueryFailedException(QUERY_FAILED_AT + query.sql, ex);
        }
    }

    public int mutate(Query query) {
        try {
            return this.jdbcTemplateAutoCommitOn.update(query.sql, Collections.emptyMap());
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception | NoClassDefFoundError ex) {
            if (ex instanceof NoClassDefFoundError) {
                log.warn("No class defined error occured. It is likely there is a class missmatch happening with spring");
            }
            log.error(QUERY_FAILED_LOG, (Object)query.sql, (Object)ex);
            throw new QueryFailedException(QUERY_FAILED_AT + query.sql, ex);
        }
    }

    public Optional<Integer> fetchInteger(Query query, Map<String, ?> params) {
        try {
            return Optional.of(this.jdbcTemplateAutoCommitOff.queryForObject(query.sql, params, Integer.class));
        }
        catch (UncategorizedSQLException ex) {
            if (ex.getSQLException().getErrorCode() == 300) {
                throw new SecurityException(ex);
            }
            throw new QueryFailedException(QUERY_FAILED_AT + query);
        }
        catch (BadSqlGrammarException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new QueryFailedException(QUERY_FAILED_AT + query, ex);
        }
    }

    public Optional<Integer> fetchInteger(Query query) {
        return this.fetchInteger(query, Collections.emptyMap());
    }

    public Optional<String> fetchString(Query query) {
        try {
            return Optional.of(this.jdbcTemplateAutoCommitOff.queryForObject(query.sql, new HashMap(), String.class));
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception ex) {
            throw new QueryFailedException(QUERY_FAILED_AT + query, ex);
        }
    }

    public List<Long> findContentIds(String query, Map paramMap) {
        try {
            return (List)this.jdbcTemplateAutoCommitOff.query(query, paramMap, rs -> {
                ArrayList<Long> result = new ArrayList<Long>();
                while (rs.next()) {
                    result.add(rs.getLong(1));
                }
                return result;
            });
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception ex) {
            log.error(QUERY_FAILED_LOG, (Object)query, (Object)ex);
            throw new QueryFailedException(QUERY_FAILED_AT + query);
        }
    }

    public long getSpaceId(String spaceKey) {
        try {
            return (Long)this.jdbcTemplateAutoCommitOff.query("select SPACEID from SPACES where SPACEKEY = :spaceKey\n ", (Map)ImmutableMap.of((Object)"spaceKey", (Object)spaceKey), rs -> {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Space [" + spaceKey + "] not found!");
                }
                return rs.getLong(1);
            });
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception ex) {
            log.error("Query failed at getSpaceId, spaceKey is : {}", (Object)spaceKey, (Object)ex);
            throw new QueryFailedException("Query failed at getSpaceId, spaceKey is : " + spaceKey);
        }
    }

    public boolean hasSpace(String spaceKey) {
        try {
            return this.getSpaceId(spaceKey) > 0L;
        }
        catch (Exception e) {
            return false;
        }
    }

    /*
     * Exception decompiling
     */
    public boolean checkIfTableExists(String tableName) throws Exception {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }
}

