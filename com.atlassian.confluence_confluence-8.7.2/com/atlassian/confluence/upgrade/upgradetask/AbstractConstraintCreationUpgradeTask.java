/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.UpgradeUtils;
import com.atlassian.confluence.upgrade.upgradetask.EmbeddedCrowdSchemaUpgradeTask;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractConstraintCreationUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    public static final Logger log = LoggerFactory.getLogger(AbstractConstraintCreationUpgradeTask.class);
    protected final SessionFactory sessionFactory;

    public AbstractConstraintCreationUpgradeTask(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected abstract List<String> getSqlStatementsFromPropertiesFile();

    protected static List<String> getSqlStatementsFromPropertiesFile(String propertiesFile) {
        try {
            InputStream propertiesStream = EmbeddedCrowdSchemaUpgradeTask.class.getClassLoader().getResourceAsStream(propertiesFile);
            if (propertiesStream == null) {
                throw new FileNotFoundException(propertiesFile);
            }
            Properties props = new Properties();
            props.load(propertiesStream);
            Collection<Object> statements = props.values();
            return new ArrayList<Object>(statements);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to load properties file from classpath: " + propertiesFile + " " + e, e);
        }
    }

    private void doUpgrade(JdbcTemplate template) throws Exception {
        for (String statement : this.getSqlStatementsFromPropertiesFile()) {
            try {
                template.execute(statement);
            }
            catch (DataAccessException e) {
                log.warn("Could not create unique constraint: " + statement + ", " + e.getMessage());
            }
        }
    }

    protected abstract void doBeforeUpgrade(Session var1, JdbcTemplate var2);

    public final void doUpgrade() throws Exception {
        Session session = this.sessionFactory.getCurrentSession();
        JdbcTemplate template = DataAccessUtils.getJdbcTemplate(session);
        this.doBeforeUpgrade(session, template);
        this.doUpgrade(template);
    }

    public static boolean uniqueAllowsMultipleNullValues() {
        return !UpgradeUtils.isOracle() && !UpgradeUtils.isSqlServer();
    }
}

