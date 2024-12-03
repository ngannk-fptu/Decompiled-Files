/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.core.RowCallbackHandler
 *  org.springframework.jdbc.core.RowCountCallbackHandler
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.persistence.ContentPermissionDao;
import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.persistence.dao.ContentPermissionSetDao;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowCountCallbackHandler;

class ContentPermissionConstraintsCleaner {
    private static final Logger log = LoggerFactory.getLogger(ContentPermissionConstraintsCleaner.class);
    private ContentPermissionSetDao contentPermissionSetDao;
    private ContentPermissionDao contentPermissionDao;
    private ContentEntityManager contentEntityManager;
    private SessionFactory sessionFactory;

    ContentPermissionConstraintsCleaner() {
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setContentPermissionSetDao(ContentPermissionSetDao contentPermissionSetDao) {
        this.contentPermissionSetDao = contentPermissionSetDao;
    }

    public void setContentPermissionDao(ContentPermissionDao contentPermissionDao) {
        this.contentPermissionDao = contentPermissionDao;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void cleanUp() {
        Session session = this.sessionFactory.getCurrentSession();
        JdbcTemplate template = DataAccessUtils.getJdbcTemplate(session);
        this.removePermissionEntriesWithNoGroupAndUser(template);
        this.removePermissionEntriesWithNoPermissionSet(template);
        this.splitPermissionEntriesWithBothGroupAndUser(session, template);
        this.removeDuplicateContentPermissionSetEntries(template);
        this.fixInconsistentPermissionEntries(session, template);
        this.removeDuplicateUserContentPermissionEntries(template);
        this.removeDuplicateGroupContentPermissionEntries(template);
        this.removeEmptyContentPermissionSets(template);
        this.removePermissionSetEntriesWithNoOwningContent(template);
    }

    private void removePermissionEntriesWithNoGroupAndUser(JdbcTemplate template) {
        log.debug("Looking for empty permission entries (null user and group)...");
        String emptyCpSql = "DELETE FROM CONTENT_PERM WHERE USERNAME IS NULL AND GROUPNAME IS NULL";
        int numEmptyPermissions = template.update("DELETE FROM CONTENT_PERM WHERE USERNAME IS NULL AND GROUPNAME IS NULL");
        log.debug("Found {} empty permission entries.", (Object)numEmptyPermissions);
        if (numEmptyPermissions > 0) {
            log.warn("Removed {} empty permission entries (had no user and group set). These should not exist.You may be using a plugin that is corrupting permissions.", (Object)numEmptyPermissions);
        }
    }

    private void removePermissionEntriesWithNoPermissionSet(JdbcTemplate template) {
        log.debug("Looking for permission entries with no permission set (null cps_id)...");
        String deleteCpWithNullCpsSql = "DELETE FROM CONTENT_PERM WHERE CPS_ID IS NULL";
        int numEmptyPermissions = template.update("DELETE FROM CONTENT_PERM WHERE CPS_ID IS NULL");
        log.debug("Found {} permission entries with no permission set.", (Object)numEmptyPermissions);
    }

    private void removePermissionSetEntriesWithNoOwningContent(JdbcTemplate template) {
        log.debug("Looking for permission set entries with no owning content (null content_id)...");
        String deleteCpLinkToCpsWithNullContentSql = "DELETE FROM CONTENT_PERM WHERE CONTENT_PERM.CPS_ID IN (SELECT ID FROM CONTENT_PERM_SET WHERE CONTENT_ID IS NULL)";
        int numPermissionLinkToEmptyContentCps = template.update("DELETE FROM CONTENT_PERM WHERE CONTENT_PERM.CPS_ID IN (SELECT ID FROM CONTENT_PERM_SET WHERE CONTENT_ID IS NULL)");
        log.debug("Found {} permission entries link to permission set with no owning content.", (Object)numPermissionLinkToEmptyContentCps);
        String deleteCpsWithNullContentSql = "DELETE FROM CONTENT_PERM_SET WHERE CONTENT_ID IS NULL";
        int numEmptyPermissions = template.update("DELETE FROM CONTENT_PERM_SET WHERE CONTENT_ID IS NULL");
        log.debug("Found {} permission set entries with no owning content.", (Object)numEmptyPermissions);
    }

    private void splitPermissionEntriesWithBothGroupAndUser(Session session, final JdbcTemplate template) {
        log.debug("Looking for bad permission entries (with both user and group set)...");
        String invalidCpSql = "SELECT cp.ID, cp.CP_TYPE, cp.USERNAME, cp.GROUPNAME, cp.CPS_ID, cp.CREATOR, cp.CREATIONDATE, cp.LASTMODIFIER, cp.LASTMODDATE FROM CONTENT_PERM cp WHERE cp.USERNAME IS NOT NULL AND cp.GROUPNAME IS NOT NULL";
        RowCountCallbackHandler handler = new RowCountCallbackHandler(){

            public void processRow(ResultSet rs, int rowNum) throws SQLException {
                ContentPermissionRowHolder cp = new ContentPermissionRowHolder();
                cp.assignFrom(rs);
                log.debug("Splitting content permission entry with group {} and user {} for content permission set {}", new Object[]{cp.groupName, cp.username, cp.cpsId});
                template.update("UPDATE CONTENT_PERM SET GROUPNAME = NULL WHERE ID = ?", new Object[]{cp.id});
                ContentPermission groupCp = ContentPermission.createGroupPermission(cp.cpType, cp.groupName);
                groupCp.setOwningSet(ContentPermissionConstraintsCleaner.this.contentPermissionSetDao.getById(cp.cpsId));
                groupCp.setCreatorName(cp.creator);
                groupCp.setLastModifierName(cp.lastmodifier);
                groupCp.setCreationDate(cp.creationdate);
                groupCp.setLastModificationDate(cp.lastmoddate);
                ContentPermissionConstraintsCleaner.this.contentPermissionDao.save(groupCp);
            }
        };
        template.query("SELECT cp.ID, cp.CP_TYPE, cp.USERNAME, cp.GROUPNAME, cp.CPS_ID, cp.CREATOR, cp.CREATIONDATE, cp.LASTMODIFIER, cp.LASTMODDATE FROM CONTENT_PERM cp WHERE cp.USERNAME IS NOT NULL AND cp.GROUPNAME IS NOT NULL", (RowCallbackHandler)handler);
        int numBadPermissionEntries = handler.getRowCount();
        session.flush();
        log.debug("Found {} bad permission entries (had both user and group set).", (Object)numBadPermissionEntries);
        if (numBadPermissionEntries > 0) {
            log.warn("Split {} bad permission entries (had both user and group set). These should not exist.You may be using a plugin that is corrupting permissions.", (Object)numBadPermissionEntries);
        }
    }

    private void removeDuplicateContentPermissionSetEntries(JdbcTemplate template) {
        log.debug("Looking for duplicate permission set entries...");
        String duplicateCpsSql = "SELECT cps1.ID, cps1.CONTENT_ID, cps1.CONT_PERM_TYPE FROM CONTENT_PERM_SET cps1, CONTENT_PERM_SET cps2 WHERE cps1.ID <> cps2.ID AND cps1.CONTENT_ID = cps2.CONTENT_ID AND cps1.CONT_PERM_TYPE = cps2.CONT_PERM_TYPE ORDER BY cps1.CONTENT_ID, cps1.CONT_PERM_TYPE, cps1.CREATIONDATE ASC";
        ContentPermissionSetRowHolder lastUniqueCpsRow = new ContentPermissionSetRowHolder();
        ContentPermissionSetRowHolder currentCpsRow = new ContentPermissionSetRowHolder();
        AtomicLong numRemovedDuplicatePermSets = new AtomicLong(0L);
        RowCallbackHandler handler = rs -> {
            currentCpsRow.assignFrom(rs);
            if (lastUniqueCpsRow.duplicates(currentCpsRow)) {
                log.debug("Duplicate content permission set ID: {} is same as ID: {}", (Object)currentCpsRow.id, (Object)lastUniqueCpsRow.id);
                log.debug("Updating content permissions to use permission set ID: {} instead", (Object)lastUniqueCpsRow.id);
                template.update("UPDATE CONTENT_PERM SET CPS_ID = ? WHERE CPS_ID = ?", new Object[]{lastUniqueCpsRow.id, currentCpsRow.id}, new int[]{-5, -5});
                log.debug("Removing duplicate content permission set ID: {}", (Object)currentCpsRow.id);
                template.update("DELETE FROM CONTENT_PERM_SET WHERE ID = ?", new Object[]{currentCpsRow.id}, new int[]{-5});
                numRemovedDuplicatePermSets.incrementAndGet();
            } else {
                lastUniqueCpsRow.assignFrom(currentCpsRow);
            }
        };
        template.query("SELECT cps1.ID, cps1.CONTENT_ID, cps1.CONT_PERM_TYPE FROM CONTENT_PERM_SET cps1, CONTENT_PERM_SET cps2 WHERE cps1.ID <> cps2.ID AND cps1.CONTENT_ID = cps2.CONTENT_ID AND cps1.CONT_PERM_TYPE = cps2.CONT_PERM_TYPE ORDER BY cps1.CONTENT_ID, cps1.CONT_PERM_TYPE, cps1.CREATIONDATE ASC", handler);
        log.debug("Found {} duplicate permission set entries.", (Object)numRemovedDuplicatePermSets);
        if (numRemovedDuplicatePermSets.get() > 0L) {
            log.warn("Removed {} duplicate permission set entries. These should not exist.You may be using a plugin that is corrupting permissions.", (Object)numRemovedDuplicatePermSets);
        }
    }

    private void fixInconsistentPermissionEntries(final Session session, final JdbcTemplate template) {
        log.debug("Looking for inconsistent permission entries...");
        String inconsistentPermissionEntriesSql = "SELECT cp.ID, cp.CP_TYPE, cps.CONTENT_ID, (SELECT scps.ID FROM CONTENT_PERM_SET scps WHERE scps.CONTENT_ID = cps.CONTENT_ID AND scps.CONT_PERM_TYPE = cp.CP_TYPE) AS suggested_cps_id FROM CONTENT_PERM cp, CONTENT_PERM_SET cps WHERE cp.CPS_ID = cps.ID AND cp.CP_TYPE <> cps.CONT_PERM_TYPE";
        RowCountCallbackHandler handlerWithCount = new RowCountCallbackHandler(){

            public void processRow(ResultSet rs, int rowNum) throws SQLException {
                long cpsId;
                long badContentPermId = rs.getLong(1);
                String cpType = rs.getString(2);
                long contentId = rs.getLong(3);
                String suggestedCpsId = rs.getString(4);
                log.debug("Found inconsistent permission entry #{}. ID: {}, Content ID: {}, Permission Type: {}", new Object[]{rowNum, badContentPermId, contentId, cpType});
                if (suggestedCpsId == null) {
                    ContentPermissionSet cps = new ContentPermissionSet(cpType, ContentPermissionConstraintsCleaner.this.contentEntityManager.getById(contentId));
                    ContentPermissionConstraintsCleaner.this.contentPermissionSetDao.save(cps);
                    session.flush();
                    cpsId = cps.getId();
                    log.debug("No existing matching content permission set exists. Created new set with ID: {}", (Object)cpsId);
                } else {
                    log.debug("Found existing matching content permission set entry ID: {}", (Object)suggestedCpsId);
                    cpsId = Long.parseLong(suggestedCpsId);
                }
                template.update("UPDATE CONTENT_PERM SET CPS_ID = ? WHERE ID = ?", new Object[]{cpsId, badContentPermId}, new int[]{-5, -5});
            }
        };
        template.query("SELECT cp.ID, cp.CP_TYPE, cps.CONTENT_ID, (SELECT scps.ID FROM CONTENT_PERM_SET scps WHERE scps.CONTENT_ID = cps.CONTENT_ID AND scps.CONT_PERM_TYPE = cp.CP_TYPE) AS suggested_cps_id FROM CONTENT_PERM cp, CONTENT_PERM_SET cps WHERE cp.CPS_ID = cps.ID AND cp.CP_TYPE <> cps.CONT_PERM_TYPE", (RowCallbackHandler)handlerWithCount);
        int numInconsistentPermissionEntriesFixed = handlerWithCount.getRowCount();
        log.debug("Found {} inconsistent permission entries.", (Object)handlerWithCount.getRowCount());
        if (numInconsistentPermissionEntriesFixed > 0) {
            log.warn("Fixed {} inconsistent permission entries. These should not exist.You may be using a plugin that is corrupting permissions.", (Object)numInconsistentPermissionEntriesFixed);
        }
    }

    private void removeDuplicateUserContentPermissionEntries(JdbcTemplate template) {
        log.debug("Looking for duplicate user permission entries...");
        String duplicateCpSql = "SELECT DISTINCT cp1.ID, cp1.CP_TYPE, cp1.USERNAME, cp1.GROUPNAME, cp1.CPS_ID, cp1.CREATOR, cp1.CREATIONDATE, cp1.LASTMODIFIER, cp1.LASTMODDATE FROM CONTENT_PERM cp1, CONTENT_PERM_SET cps1, CONTENT_PERM cp2, CONTENT_PERM_SET cps2 WHERE cp1.CPS_ID = cps1.ID AND cp2.CPS_ID = cps2.ID AND cp1.ID <> cp2.ID AND cps1.CONTENT_ID = cps2.CONTENT_ID AND cp1.CP_TYPE = cp2.CP_TYPE AND cp1.USERNAME = cp2.USERNAME ORDER BY cp1.CPS_ID, cp1.CP_TYPE, cp1.USERNAME, cp1.CREATIONDATE";
        AtomicLong numRemovedDuplicatePerms = new AtomicLong(0L);
        RowCountCallbackHandler handler = this.removeDuplicateContentPermissionCallbackHandler(template, numRemovedDuplicatePerms);
        template.query("SELECT DISTINCT cp1.ID, cp1.CP_TYPE, cp1.USERNAME, cp1.GROUPNAME, cp1.CPS_ID, cp1.CREATOR, cp1.CREATIONDATE, cp1.LASTMODIFIER, cp1.LASTMODDATE FROM CONTENT_PERM cp1, CONTENT_PERM_SET cps1, CONTENT_PERM cp2, CONTENT_PERM_SET cps2 WHERE cp1.CPS_ID = cps1.ID AND cp2.CPS_ID = cps2.ID AND cp1.ID <> cp2.ID AND cps1.CONTENT_ID = cps2.CONTENT_ID AND cp1.CP_TYPE = cp2.CP_TYPE AND cp1.USERNAME = cp2.USERNAME ORDER BY cp1.CPS_ID, cp1.CP_TYPE, cp1.USERNAME, cp1.CREATIONDATE", (RowCallbackHandler)handler);
        log.debug("Found {} duplicate user permission entries.", (Object)numRemovedDuplicatePerms);
        if (numRemovedDuplicatePerms.get() > 0L) {
            log.warn("Removed {} duplicate user permission entries. These should not exist.You may be using a plugin that is corrupting permissions.", (Object)numRemovedDuplicatePerms);
        }
    }

    private void removeDuplicateGroupContentPermissionEntries(JdbcTemplate template) {
        log.debug("Looking for duplicate group permission entries...");
        String duplicateCpSql = "SELECT DISTINCT cp1.ID, cp1.CP_TYPE, cp1.USERNAME, cp1.GROUPNAME, cp1.CPS_ID, cp1.CREATOR, cp1.CREATIONDATE, cp1.LASTMODIFIER, cp1.LASTMODDATE FROM CONTENT_PERM cp1, CONTENT_PERM_SET cps1, CONTENT_PERM cp2, CONTENT_PERM_SET cps2 WHERE cp1.CPS_ID = cps1.ID AND cp2.CPS_ID = cps2.ID AND cp1.ID <> cp2.ID AND cps1.CONTENT_ID = cps2.CONTENT_ID AND cp1.CP_TYPE = cp2.CP_TYPE AND cp1.GROUPNAME = cp2.GROUPNAME ORDER BY cp1.CPS_ID, cp1.CP_TYPE, cp1.GROUPNAME, cp1.CREATIONDATE";
        AtomicLong numRemovedDuplicatePerms = new AtomicLong(0L);
        RowCountCallbackHandler handler = this.removeDuplicateContentPermissionCallbackHandler(template, numRemovedDuplicatePerms);
        template.query("SELECT DISTINCT cp1.ID, cp1.CP_TYPE, cp1.USERNAME, cp1.GROUPNAME, cp1.CPS_ID, cp1.CREATOR, cp1.CREATIONDATE, cp1.LASTMODIFIER, cp1.LASTMODDATE FROM CONTENT_PERM cp1, CONTENT_PERM_SET cps1, CONTENT_PERM cp2, CONTENT_PERM_SET cps2 WHERE cp1.CPS_ID = cps1.ID AND cp2.CPS_ID = cps2.ID AND cp1.ID <> cp2.ID AND cps1.CONTENT_ID = cps2.CONTENT_ID AND cp1.CP_TYPE = cp2.CP_TYPE AND cp1.GROUPNAME = cp2.GROUPNAME ORDER BY cp1.CPS_ID, cp1.CP_TYPE, cp1.GROUPNAME, cp1.CREATIONDATE", (RowCallbackHandler)handler);
        log.debug("Found {} duplicate group permission entries.", (Object)numRemovedDuplicatePerms);
        if (numRemovedDuplicatePerms.get() > 0L) {
            log.warn("Removed {} duplicate group permission entries. These should not exist.You may be using a plugin that is corrupting permissions.", (Object)numRemovedDuplicatePerms);
        }
    }

    private RowCountCallbackHandler removeDuplicateContentPermissionCallbackHandler(final JdbcTemplate template, final AtomicLong numRemovedDuplicatePerms) {
        final ContentPermissionRowHolder lastUniqueCpRow = new ContentPermissionRowHolder();
        final ContentPermissionRowHolder currentCpRow = new ContentPermissionRowHolder();
        return new RowCountCallbackHandler(){

            public void processRow(ResultSet rs, int rowNum) throws SQLException {
                currentCpRow.assignFrom(rs);
                if (lastUniqueCpRow.duplicates(currentCpRow)) {
                    log.debug("Removing duplicate content permission ID: {}", (Object)currentCpRow.id);
                    template.update("DELETE FROM CONTENT_PERM WHERE ID = ?", new Object[]{currentCpRow.id}, new int[]{-5});
                    numRemovedDuplicatePerms.incrementAndGet();
                } else {
                    lastUniqueCpRow.assignFrom(currentCpRow);
                }
            }
        };
    }

    private void removeEmptyContentPermissionSets(JdbcTemplate template) {
        log.debug("Cleaning up redundant permission set entries...");
        String childlessCpsSql = "DELETE FROM CONTENT_PERM_SET WHERE ID NOT IN (SELECT DISTINCT CPS_ID FROM CONTENT_PERM)";
        int numChildlessCps = template.update("DELETE FROM CONTENT_PERM_SET WHERE ID NOT IN (SELECT DISTINCT CPS_ID FROM CONTENT_PERM)");
        log.debug("Found {} redundant permission set entries (no children).", (Object)numChildlessCps);
        if (numChildlessCps > 0) {
            log.warn("Removed {} redundant permission set entries. These should not exist.You may be using a plugin that is corrupting permissions.", (Object)numChildlessCps);
        }
    }

    private static class ContentPermissionRowHolder {
        public long id = -1L;
        public String cpType = null;
        public String username = null;
        public String groupName = null;
        public long cpsId = -1L;
        public String creator = null;
        public Date creationdate = null;
        public String lastmodifier = null;
        public Date lastmoddate = null;

        public void assignFrom(ResultSet rs) throws SQLException {
            this.id = rs.getLong(1);
            this.cpType = rs.getString(2);
            this.username = rs.getString(3);
            this.groupName = rs.getString(4);
            this.cpsId = rs.getLong(5);
            this.creator = rs.getString(6);
            this.creationdate = rs.getDate(7);
            this.lastmodifier = rs.getString(8);
            this.lastmoddate = rs.getDate(9);
        }

        public void assignFrom(ContentPermissionRowHolder that) {
            this.id = that.id;
            this.cpType = that.cpType;
            this.username = that.username;
            this.groupName = that.groupName;
            this.cpsId = that.cpsId;
            this.creator = that.creator;
            this.creationdate = that.creationdate;
            this.lastmodifier = that.lastmodifier;
            this.lastmoddate = that.lastmoddate;
        }

        public boolean duplicates(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ContentPermissionRowHolder)) {
                return false;
            }
            ContentPermissionRowHolder that = (ContentPermissionRowHolder)o;
            return that.cpsId == this.cpsId && ObjectUtils.equals((Object)this.username, (Object)that.username) && ObjectUtils.equals((Object)this.groupName, (Object)that.groupName);
        }
    }

    private static class ContentPermissionSetRowHolder {
        public long id = -1L;
        public long contentId = -1L;
        public String permType = "invalid";

        public void assignFrom(ResultSet rs) throws SQLException {
            this.id = rs.getLong(1);
            this.contentId = rs.getLong(2);
            this.permType = rs.getString(3);
        }

        public void assignFrom(ContentPermissionSetRowHolder that) {
            this.id = that.id;
            this.contentId = that.contentId;
            this.permType = that.permType;
        }

        public boolean duplicates(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ContentPermissionSetRowHolder)) {
                return false;
            }
            ContentPermissionSetRowHolder that = (ContentPermissionSetRowHolder)o;
            return that.contentId == this.contentId && that.permType.equals(this.permType);
        }
    }
}

