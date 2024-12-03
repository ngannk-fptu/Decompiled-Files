/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.internal.upgrade.constraint;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.impl.HibernateCheckConstraint;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public class ConstraintChecker {
    private static final Logger log = LoggerFactory.getLogger(ConstraintChecker.class);
    private final HibernateConfig hibernateConfig;
    private final SessionFactory sessionFactory;

    public ConstraintChecker(HibernateConfig hibernateConfig, SessionFactory sessionFactory) {
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
    }

    public boolean exists(String table, String name, List<String> uniqueColumns) throws UpgradeException {
        Map<String, List<String>> constraintsForTable;
        if (this.hibernateConfig.isSqlServer() && uniqueColumns.size() == 1) {
            String checkSqlQuery = String.format("SELECT COUNT(*) FROM sys.tables t, sys.indexes i WHERE t.object_id = i.object_id AND t.name = '%s' AND i.name='%s'", table, table + "_" + name);
            log.debug("Query to check for constraint [{}]:{}", (Object)name, (Object)checkSqlQuery);
            try {
                Long count = (Long)DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession()).queryForObject(checkSqlQuery, Long.class);
                return count != null && count > 0L;
            }
            catch (DataAccessException dae) {
                throw new UpgradeException("Error checking unique constraint " + name, (Throwable)dae);
            }
        }
        try {
            constraintsForTable = HibernateCheckConstraint.getConstraintsForTable(this.hibernateConfig, this.sessionFactory, table);
        }
        catch (Exception e) {
            throw new UpgradeException("Error checking unique constraint " + name, (Throwable)e);
        }
        return constraintsForTable.keySet().stream().anyMatch(constraint -> {
            if (constraint.equalsIgnoreCase(name)) {
                log.debug("Found an existing constraint with same name (case insensitive) {}", constraint);
                return true;
            }
            List uniqueColumnsInConstraint = (List)constraintsForTable.get(constraint);
            return uniqueColumns.size() == uniqueColumnsInConstraint.size() && uniqueColumns.stream().map(String::toUpperCase).collect(Collectors.toList()).containsAll(uniqueColumnsInConstraint.stream().map(String::toUpperCase).collect(Collectors.toList()));
        });
    }
}

