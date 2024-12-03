/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.internal.upgrade.constraint.dedup;

import com.atlassian.confluence.internal.upgrade.constraint.dedup.DedupeStrategy;
import com.atlassian.confluence.upgrade.UpgradeException;
import java.util.SortedSet;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class KeepOneDedupeStrategy
implements DedupeStrategy {
    private final String deleteQuery;

    KeepOneDedupeStrategy(String table, String idColumn) {
        this.deleteQuery = String.format("DELETE FROM %s WHERE %s = ?", table, idColumn);
    }

    @Override
    public void perform(JdbcTemplate jdbcTemplate, SortedSet<Object> ids) throws UpgradeException {
        Object idToKeep = this.getIdToKeep(ids);
        try {
            ids.stream().filter(id -> !id.equals(idToKeep)).forEach(id -> jdbcTemplate.update(this.deleteQuery, new Object[]{id}));
        }
        catch (DataAccessException dae) {
            throw new UpgradeException("Error deleting duplicates", (Throwable)dae);
        }
    }

    protected abstract Object getIdToKeep(SortedSet<Object> var1);
}

