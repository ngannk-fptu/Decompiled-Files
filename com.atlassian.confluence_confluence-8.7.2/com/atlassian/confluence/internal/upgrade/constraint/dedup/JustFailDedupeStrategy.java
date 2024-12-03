/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.internal.upgrade.constraint.dedup;

import com.atlassian.confluence.internal.upgrade.constraint.dedup.DedupeStrategy;
import com.atlassian.confluence.upgrade.UpgradeException;
import java.util.SortedSet;
import org.springframework.jdbc.core.JdbcTemplate;

public class JustFailDedupeStrategy
implements DedupeStrategy {
    @Override
    public void perform(JdbcTemplate jdbcTemplate, SortedSet<Object> ids) throws UpgradeException {
        throw new UpgradeException("There are duplicates. Cannot add the unique constraint.");
    }
}

