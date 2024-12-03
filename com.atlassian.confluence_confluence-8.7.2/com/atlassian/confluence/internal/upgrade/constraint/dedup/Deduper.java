/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.internal.upgrade.constraint.dedup;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.internal.upgrade.constraint.dedup.DedupeStrategy;
import com.atlassian.confluence.internal.upgrade.constraint.dedup.DuplicateRowHolder;
import com.atlassian.confluence.internal.upgrade.constraint.dedup.DuplicateRowHolderFactory;
import com.atlassian.confluence.upgrade.UpgradeException;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Deduper {
    private static final Logger log = LoggerFactory.getLogger(Deduper.class);
    private final SessionFactory sessionFactory;
    private final HibernateConfig hibernateConfig;

    public Deduper(SessionFactory sessionFactory, HibernateConfig hibernateConfig) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
    }

    public long removeDuplicates(String table, String idColumn, List<String> uniqueColumns, DedupeStrategy dedupeStrategy) throws UpgradeException {
        List duplicates;
        DuplicateRowHolderFactory rowHolderFactory = new DuplicateRowHolderFactory(idColumn, uniqueColumns);
        JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
        String queryToFindDuplicate = this.makeQueryToFindDuplicates(table, idColumn, uniqueColumns);
        log.debug("Query to find duplicate in table [{}]: [{}]", (Object)table, (Object)queryToFindDuplicate);
        try {
            duplicates = jdbcTemplate.query(queryToFindDuplicate, (rs, rowNum) -> rowHolderFactory.make(rs));
        }
        catch (DataAccessException dae) {
            throw new UpgradeException("Error fetching duplicated records in table " + table, (Throwable)dae);
        }
        return this.removeDuplicates(jdbcTemplate, duplicates, dedupeStrategy);
    }

    public boolean multipleNullsNotAllowed() {
        return this.hibernateConfig.isOracle() || this.hibernateConfig.isSqlServer();
    }

    @VisibleForTesting
    long removeDuplicates(JdbcTemplate jdbcTemplate, List<DuplicateRowHolder> duplicates, DedupeStrategy dedupeStrategy) throws UpgradeException {
        if (duplicates == null || duplicates.size() == 0) {
            return 0L;
        }
        DuplicateRowHolder lastUniqueRowHolder = null;
        TreeSet<Object> lastUniqueIds = new TreeSet<Object>();
        AtomicLong dedupedCount = new AtomicLong(0L);
        for (DuplicateRowHolder rowHolder : duplicates) {
            if (rowHolder.duplicates(lastUniqueRowHolder)) {
                lastUniqueIds.add(rowHolder.getId());
                continue;
            }
            if (!lastUniqueIds.isEmpty()) {
                dedupeStrategy.perform(jdbcTemplate, lastUniqueIds);
                dedupedCount.addAndGet(lastUniqueIds.size() - 1);
            }
            lastUniqueRowHolder = rowHolder;
            lastUniqueIds.clear();
            lastUniqueIds.add(rowHolder.getId());
        }
        if (!lastUniqueIds.isEmpty()) {
            dedupeStrategy.perform(jdbcTemplate, lastUniqueIds);
            dedupedCount.addAndGet(lastUniqueIds.size() - 1);
        }
        return dedupedCount.get();
    }

    private String makeQueryToFindDuplicates(String table, String idColumn, List<String> uniqueColumns) {
        StringBuilder duplicationSqlQuery = new StringBuilder();
        duplicationSqlQuery.append("SELECT lhs.").append(idColumn);
        for (String column : uniqueColumns) {
            duplicationSqlQuery.append(", lhs.").append(column);
        }
        duplicationSqlQuery.append(" FROM ").append(table).append(" lhs, ").append(table).append(" rhs");
        duplicationSqlQuery.append(" WHERE lhs.").append(idColumn).append(" <> rhs.").append(idColumn);
        for (String column : uniqueColumns) {
            duplicationSqlQuery.append(" AND (");
            duplicationSqlQuery.append("  lhs.").append(column).append(" = rhs.").append(column);
            if (this.multipleNullsNotAllowed()) {
                duplicationSqlQuery.append("  OR (lhs.").append(column).append(" IS NULL AND rhs.").append(column).append(" IS NULL)");
            }
            duplicationSqlQuery.append(")");
        }
        duplicationSqlQuery.append(" ORDER BY");
        for (String column : uniqueColumns) {
            duplicationSqlQuery.append(" lhs.").append(column).append(" ASC,");
        }
        duplicationSqlQuery.append(" lhs.").append(idColumn).append(" ASC");
        return duplicationSqlQuery.toString();
    }
}

