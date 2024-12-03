/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.message.Message
 *  javax.annotation.Nonnull
 *  net.java.ao.Mutator
 *  net.java.ao.Query
 *  net.java.ao.schema.Table
 */
package com.atlassian.audit.plugin.upgrade.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.ao.dao.entity.AoAuditEntity;
import com.atlassian.audit.plugin.upgrade.AuditUpgradeTask;
import com.atlassian.sal.api.message.Message;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import net.java.ao.Mutator;
import net.java.ao.Query;
import net.java.ao.schema.Table;

public class UpgradeTask3MigrateJiraCategories
extends AuditUpgradeTask {
    @VisibleForTesting
    public static final String BOARDS_CATEGORY_WITH_TYPO = "boards ";
    @VisibleForTesting
    public static final String TARGET_BOARDS_CATEGORY = "boards";
    private static final int BUILD_NUMBER = 3;
    private static final int DEFAULT_PAGE_SIZE = 10000;
    private final ActiveObjects ao;

    public UpgradeTask3MigrateJiraCategories(ActiveObjects ao) {
        this.ao = ao;
    }

    public int getBuildNumber() {
        return 3;
    }

    public String getShortDescription() {
        return "Migrate Jira 'boards ' category to the proper one (whitespace typo fix).";
    }

    public Collection<Message> doUpgrade() throws Exception {
        int total = this.getTotalPages();
        for (int page = 0; page <= total; ++page) {
            AoUpgradeAuditEntity[] aoAuditEntities = (AoUpgradeAuditEntity[])this.ao.find(AoUpgradeAuditEntity.class, this.getAuditsQuery().limit(10000));
            Arrays.stream(aoAuditEntities).forEach(auditEntity -> this.migrateBoardsCategory((AoUpgradeAuditEntity)auditEntity));
        }
        return Collections.emptyList();
    }

    private Query getAuditsQuery() {
        return Query.select().where(String.format("%s LIKE ?", "CATEGORY"), new Object[]{BOARDS_CATEGORY_WITH_TYPO});
    }

    private void migrateBoardsCategory(@Nonnull AoUpgradeAuditEntity auditEntity) {
        this.ao.executeInTransaction(() -> {
            auditEntity.setCategory(TARGET_BOARDS_CATEGORY);
            auditEntity.save();
            return auditEntity;
        });
    }

    private int getTotalPages() {
        return (int)Math.ceil((double)this.getTotalAudits() / 10000.0);
    }

    private int getTotalAudits() {
        return this.ao.count(AoUpgradeAuditEntity.class, this.getAuditsQuery());
    }

    @Table(value="AUDIT_ENTITY")
    @VisibleForTesting
    static interface AoUpgradeAuditEntity
    extends AoAuditEntity {
        @Mutator(value="CATEGORY")
        public void setCategory(String var1);
    }
}

