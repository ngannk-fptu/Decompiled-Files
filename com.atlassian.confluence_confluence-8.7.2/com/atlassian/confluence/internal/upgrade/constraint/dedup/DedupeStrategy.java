/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.internal.upgrade.constraint.dedup;

import com.atlassian.confluence.upgrade.UpgradeException;
import java.util.SortedSet;
import org.springframework.jdbc.core.JdbcTemplate;

public interface DedupeStrategy {
    public void perform(JdbcTemplate var1, SortedSet<Object> var2) throws UpgradeException;
}

