/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  org.hibernate.SessionFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import java.util.Collection;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class CorrectCsvAttachmentMimeTypeUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private final SessionFactory sessionFactory;

    public CorrectCsvAttachmentMimeTypeUpgradeTask(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public String getBuildNumber() {
        return "7104";
    }

    public String getShortDescription() {
        return "Set the mime type for csv files to text/csv";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
        log.debug("Start fixing mime types for csv files");
        JdbcTemplate template = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
        String findIds = "SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = ? AND LOWERTITLE LIKE ?";
        List ids = template.queryForList("SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = ? AND LOWERTITLE LIKE ?", Long.class, new Object[]{"attachment".toUpperCase(), "%.csv"});
        if (ids.isEmpty()) {
            log.debug("Set mime type to text/csv for 0 csv files. If you ran this task manually, you should flush the Attachments cache");
            return;
        }
        String updateCsv = "UPDATE CONTENTPROPERTIES SET STRINGVAL = ? where PROPERTYNAME = ? AND CONTENTID = ?";
        int[][] updatedRows = template.batchUpdate("UPDATE CONTENTPROPERTIES SET STRINGVAL = ? where PROPERTYNAME = ? AND CONTENTID = ?", (Collection)ids, 100, (ps, argument) -> {
            ps.setString(1, "text/csv");
            ps.setString(2, "MEDIA_TYPE");
            ps.setLong(3, (long)argument);
        });
        int totalRows = 0;
        int[][] nArray = updatedRows;
        int n = nArray.length;
        for (int i = 0; i < n; ++i) {
            int[] batch;
            for (int updatedRow : batch = nArray[i]) {
                totalRows += updatedRow;
            }
        }
        log.debug("Set mime type to text/csv for {} csv files. If you ran this task manually, you should flush the Attachments cache", (Object)totalRows);
    }
}

