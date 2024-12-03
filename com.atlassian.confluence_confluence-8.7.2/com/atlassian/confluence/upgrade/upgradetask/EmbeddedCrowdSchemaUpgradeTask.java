/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeError
 *  org.apache.commons.lang3.ObjectUtils
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.core.RowCallbackHandler
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.upgradetask.AbstractConstraintCreationUpgradeTask;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class EmbeddedCrowdSchemaUpgradeTask
extends AbstractConstraintCreationUpgradeTask {
    private static final String CONSTRAINTS_FILE_UNIQUE_NULLS = "com/atlassian/crowd/embedded/hibernate2/additional_unique_constraints_unique_nulls.properties";
    private static final String CONSTRAINTS_FILE_NON_UNIQUE_NULLS = "com/atlassian/crowd/embedded/hibernate2/additional_unique_constraints_non_unique_nulls.properties";

    public EmbeddedCrowdSchemaUpgradeTask(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public String getBuildNumber() {
        return "2161";
    }

    public String getShortDescription() {
        return "Adds required multi-column unique constraints to the Crowd database tables";
    }

    public static List<String> getUniqueConstraintSqlStatements() {
        return EmbeddedCrowdSchemaUpgradeTask.getSqlStatementsFromPropertiesFile(EmbeddedCrowdSchemaUpgradeTask.getConstraintsFile());
    }

    private static String getConstraintsFile() {
        return EmbeddedCrowdSchemaUpgradeTask.uniqueAllowsMultipleNullValues() ? CONSTRAINTS_FILE_NON_UNIQUE_NULLS : CONSTRAINTS_FILE_UNIQUE_NULLS;
    }

    @Override
    protected List<String> getSqlStatementsFromPropertiesFile() {
        return EmbeddedCrowdSchemaUpgradeTask.getUniqueConstraintSqlStatements();
    }

    public Collection<UpgradeError> getErrors() {
        return Collections.emptyList();
    }

    @Override
    protected void doBeforeUpgrade(Session session, JdbcTemplate template) {
        this.removeDuplicateUserMemberships(template);
        this.removeDuplicateGroupMemberships(template);
    }

    private void removeDuplicateUserMemberships(JdbcTemplate template) {
        log.debug("Looking for duplicate user memberships...");
        String duplicateMembershipSql = "SELECT mem1.id, mem1.parent_id, mem1.child_group_id, mem1.child_user_id FROM cwd_membership mem1, cwd_membership mem2 WHERE mem1.id <> mem2.id AND mem1.parent_id = mem2.parent_id AND mem1.child_user_id = mem2.child_user_id ORDER BY mem1.parent_id ASC, mem1.child_user_id ASC, mem1.id ASC";
        MembershipRowHolder lastUniqueMembershipRow = new MembershipRowHolder();
        MembershipRowHolder currentMembershipRow = new MembershipRowHolder();
        AtomicLong numRemovedDuplicateMemberships = new AtomicLong(0L);
        RowCallbackHandler handler = rs -> {
            currentMembershipRow.assignFrom(rs);
            if (lastUniqueMembershipRow.duplicates(currentMembershipRow)) {
                log.debug("Duplicate user membership id: {} is same as id: ", (Object)currentMembershipRow.id, (Object)lastUniqueMembershipRow.id);
                log.debug("Removing duplicate user membership id: {}", (Object)currentMembershipRow.id);
                template.update("DELETE FROM cwd_membership WHERE id = ?", new Object[]{currentMembershipRow.id}, new int[]{-5});
                numRemovedDuplicateMemberships.incrementAndGet();
            } else {
                lastUniqueMembershipRow.assignFrom(currentMembershipRow);
            }
        };
        template.query("SELECT mem1.id, mem1.parent_id, mem1.child_group_id, mem1.child_user_id FROM cwd_membership mem1, cwd_membership mem2 WHERE mem1.id <> mem2.id AND mem1.parent_id = mem2.parent_id AND mem1.child_user_id = mem2.child_user_id ORDER BY mem1.parent_id ASC, mem1.child_user_id ASC, mem1.id ASC", handler);
        log.debug("Found {} duplicate user memberships.", (Object)numRemovedDuplicateMemberships);
        if (numRemovedDuplicateMemberships.get() > 0L) {
            log.info("Removed {} duplicate user memberships.", (Object)numRemovedDuplicateMemberships);
        }
    }

    private void removeDuplicateGroupMemberships(JdbcTemplate template) {
        log.debug("Looking for duplicate group memberships...");
        String duplicateMembershipSql = "SELECT mem1.id, mem1.parent_id, mem1.child_group_id, mem1.child_user_id FROM cwd_membership mem1, cwd_membership mem2 WHERE mem1.id <> mem2.id AND mem1.parent_id = mem2.parent_id AND mem1.child_group_id = mem2.child_group_id ORDER BY mem1.parent_id ASC, mem1.child_group_id ASC, mem1.id ASC";
        MembershipRowHolder lastUniqueMembershipRow = new MembershipRowHolder();
        MembershipRowHolder currentMembershipRow = new MembershipRowHolder();
        AtomicLong numRemovedDuplicateMemberships = new AtomicLong(0L);
        RowCallbackHandler handler = rs -> {
            currentMembershipRow.assignFrom(rs);
            if (lastUniqueMembershipRow.duplicates(currentMembershipRow)) {
                log.debug("Duplicate group membership id: {} is same as id: ", (Object)currentMembershipRow.id, (Object)lastUniqueMembershipRow.id);
                log.debug("Removing duplicate group membership id: {}", (Object)currentMembershipRow.id);
                template.update("DELETE FROM cwd_membership WHERE id = ?", new Object[]{currentMembershipRow.id}, new int[]{-5});
                numRemovedDuplicateMemberships.incrementAndGet();
            } else {
                lastUniqueMembershipRow.assignFrom(currentMembershipRow);
            }
        };
        template.query("SELECT mem1.id, mem1.parent_id, mem1.child_group_id, mem1.child_user_id FROM cwd_membership mem1, cwd_membership mem2 WHERE mem1.id <> mem2.id AND mem1.parent_id = mem2.parent_id AND mem1.child_group_id = mem2.child_group_id ORDER BY mem1.parent_id ASC, mem1.child_group_id ASC, mem1.id ASC", handler);
        log.debug("Found {} duplicate group memberships.", (Object)numRemovedDuplicateMemberships);
        if (numRemovedDuplicateMemberships.get() > 0L) {
            log.info("Removed {} duplicate group memberships.", (Object)numRemovedDuplicateMemberships);
        }
    }

    public boolean runOnSpaceImport() {
        return true;
    }

    public boolean breaksBackwardCompatibility() {
        return true;
    }

    private static class MembershipRowHolder {
        private long id;
        private long parentId;
        private Long childGroupId;
        private Long childUserId;

        private MembershipRowHolder() {
        }

        public void assignFrom(ResultSet rs) throws SQLException {
            this.id = rs.getLong("id");
            this.parentId = rs.getLong("parent_id");
            this.childGroupId = (Long)rs.getObject("child_group_id");
            this.childUserId = (Long)rs.getObject("child_user_id");
        }

        public void assignFrom(MembershipRowHolder that) {
            this.id = that.id;
            this.parentId = that.parentId;
            this.childGroupId = that.childGroupId;
            this.childUserId = that.childUserId;
        }

        public boolean duplicates(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof MembershipRowHolder)) {
                return false;
            }
            MembershipRowHolder that = (MembershipRowHolder)other;
            return this.parentId == that.parentId && ObjectUtils.equals((Object)this.childGroupId, (Object)that.childGroupId) && ObjectUtils.equals((Object)this.childUserId, (Object)that.childUserId);
        }
    }
}

