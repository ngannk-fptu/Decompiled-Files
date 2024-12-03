/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.core.persistence;

import com.atlassian.config.db.HibernateConfig;
import com.google.common.base.Predicate;
import org.checkerframework.checker.nullness.qual.Nullable;

public class HsqldbPredicate
implements Predicate {
    private final HibernateConfig hibernateConfig;

    public HsqldbPredicate(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    public boolean apply(@Nullable Object input) {
        return this.hibernateConfig.isHSQL();
    }
}

