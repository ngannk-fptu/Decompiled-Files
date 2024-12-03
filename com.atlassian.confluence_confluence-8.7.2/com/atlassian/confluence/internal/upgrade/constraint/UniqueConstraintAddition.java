/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.upgrade.constraint;

import com.atlassian.confluence.internal.upgrade.constraint.ConstraintChecker;
import com.atlassian.confluence.internal.upgrade.constraint.ConstraintCreator;
import com.atlassian.confluence.internal.upgrade.constraint.dedup.DedupeStrategy;
import com.atlassian.confluence.internal.upgrade.constraint.dedup.Deduper;
import com.atlassian.confluence.upgrade.UpgradeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniqueConstraintAddition {
    private static final Logger log = LoggerFactory.getLogger(UniqueConstraintAddition.class);
    private final String name;
    private final String table;
    private final List<String> uniqueColumns;
    private final String idColumn;
    private final DedupeStrategy strategy;

    public UniqueConstraintAddition(String name, String table, List<String> uniqueColumns, String idColumn, DedupeStrategy strategy) {
        this.name = Objects.requireNonNull(name);
        this.table = Objects.requireNonNull(table);
        this.uniqueColumns = new ArrayList<String>((Collection)Objects.requireNonNull(uniqueColumns));
        this.idColumn = Objects.requireNonNull(idColumn);
        this.strategy = Objects.requireNonNull(strategy);
    }

    public boolean addIfMissing(ConstraintChecker constraintChecker, Deduper deduper, ConstraintCreator constraintCreator) throws UpgradeException {
        log.info("Fixing constraint [{}] on table [{}]", (Object)this.name, (Object)this.table);
        if (constraintChecker.exists(this.table, this.name, this.uniqueColumns)) {
            log.info("Constraint [{}] already exists", (Object)this.name);
            return false;
        }
        log.info("Constraint [{}] not found on table [{}]. Adding it soon.", (Object)this.name, (Object)this.table);
        long dedupedCount = deduper.removeDuplicates(this.table, this.idColumn, this.uniqueColumns, this.strategy);
        log.info("Removed {} duplicated records from table [{}]", (Object)dedupedCount, (Object)this.table);
        constraintCreator.create(this.table, this.name, this.uniqueColumns);
        log.info("Added constraint [{}] to table [{}]", (Object)this.name, (Object)this.table);
        return true;
    }

    public String toString() {
        return String.format("Unique constraint [%s] on table [%s]", this.name, this.table);
    }
}

