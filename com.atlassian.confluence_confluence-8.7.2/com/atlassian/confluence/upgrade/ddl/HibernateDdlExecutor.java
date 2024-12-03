/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.CreateUniqueConstraintWithMultipleNullsCommand;
import com.atlassian.confluence.upgrade.ddl.DdlCommand;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DropIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DropTableCommand;
import com.atlassian.confluence.upgrade.ddl.RenameTableCommand;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class HibernateDdlExecutor
implements DdlExecutor {
    private static final Logger log = LoggerFactory.getLogger(HibernateDdlExecutor.class);
    private final HibernateDatabaseCapabilities databaseCapabilities;
    private final SessionFactory sessionFactory;

    public HibernateDdlExecutor(HibernateDatabaseCapabilities databaseCapabilities, SessionFactory sessionFactory) {
        this.databaseCapabilities = databaseCapabilities;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public CreateIndexCommand createCreateIndexCommand(String indexName, String tableName, String ... columnNames) {
        return new CreateIndexCommand(indexName, tableName, columnNames);
    }

    @Override
    public CreateIndexCommand createCreateIndexCommand(String indexName, String tableName, boolean isUnique, String ... columnNames) {
        return new CreateIndexCommand(indexName, tableName, isUnique, columnNames);
    }

    @Override
    public CreateUniqueConstraintWithMultipleNullsCommand createUniqueConstraintWithMultipleNullsCommand(String constraintName, String tableName, String columnName) {
        return new CreateUniqueConstraintWithMultipleNullsCommand(this.databaseCapabilities, constraintName, tableName, columnName);
    }

    @Override
    public DropIndexCommand createDropIndexCommand(String indexName, String tableName) {
        return new DropIndexCommand(this.databaseCapabilities, indexName, tableName);
    }

    @Override
    public DropTableCommand createDropTableCommand(String tableName) {
        return new DropTableCommand(tableName);
    }

    @Override
    public RenameTableCommand createRenameTableCommand(String oldTableName, String newTableName) {
        return new RenameTableCommand(this.databaseCapabilities, oldTableName, newTableName);
    }

    @Override
    public void executeDdl(List<? extends DdlCommand> commands) {
        List<String> statements = this.getDdlStatements(commands);
        this.executeDdlStatements(statements);
    }

    @Override
    public void executeDdlStatements(List<String> statements) {
        Session session = this.sessionFactory.getCurrentSession();
        JdbcTemplate template = DataAccessUtils.getJdbcTemplate(session);
        for (String sql : statements) {
            log.info("Executing DDL: {}", (Object)sql);
            template.execute(sql);
        }
    }

    @Override
    public List<String> getDdlStatements(List<? extends DdlCommand> commands) {
        ArrayList<String> statements = new ArrayList<String>();
        for (DdlCommand ddlCommand : commands) {
            statements.add(ddlCommand.getStatement());
        }
        return statements;
    }
}

