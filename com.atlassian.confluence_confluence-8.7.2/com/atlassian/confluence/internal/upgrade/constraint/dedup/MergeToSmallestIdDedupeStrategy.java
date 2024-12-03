/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.internal.upgrade.constraint.dedup;

import com.atlassian.confluence.internal.upgrade.constraint.dedup.KeepSmallestIdDedupeStrategy;
import com.atlassian.confluence.upgrade.UpgradeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class MergeToSmallestIdDedupeStrategy
extends KeepSmallestIdDedupeStrategy {
    private final Collection<ReferencedTable> referencedTables;

    public MergeToSmallestIdDedupeStrategy(String table, String idColumn, Collection<ReferencedTable> referencedTables) {
        super(table, idColumn);
        this.referencedTables = new ArrayList<ReferencedTable>(referencedTables);
    }

    @Override
    public void perform(JdbcTemplate jdbcTemplate, SortedSet<Object> ids) throws UpgradeException {
        Object minId = ids.first();
        try {
            ids.stream().filter(id -> !id.equals(minId)).forEach(id -> this.merge(jdbcTemplate, id, minId));
        }
        catch (DataAccessException dae) {
            throw new UpgradeException("Error merging duplicates", (Throwable)dae);
        }
        super.perform(jdbcTemplate, ids);
    }

    private void merge(JdbcTemplate jdbcTemplate, Object sourceId, Object targetId) throws DataAccessException {
        for (ReferencedTable referencedTable : this.referencedTables) {
            String mergeSqlQuery = String.format("UPDATE %s SET %s = ? WHERE %s = ?", referencedTable.name, referencedTable.referencingColumn, referencedTable.referencingColumn);
            jdbcTemplate.update(mergeSqlQuery, new Object[]{targetId, sourceId});
        }
    }

    public static class ReferencedTable {
        String name;
        String referencingColumn;

        public ReferencedTable(String name, String referencingColumn) {
            this.name = name;
            this.referencingColumn = referencingColumn;
        }
    }
}

