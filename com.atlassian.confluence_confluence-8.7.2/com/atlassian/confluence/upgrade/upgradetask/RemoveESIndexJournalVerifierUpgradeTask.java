/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.impl.hibernate.DataAccessUtils;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class RemoveESIndexJournalVerifierUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(RemoveESIndexJournalVerifierUpgradeTask.class);
    private final SessionFactory sessionFactory;

    public RemoveESIndexJournalVerifierUpgradeTask(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public String getBuildNumber() {
        return "7502";
    }

    public String getShortDescription() {
        return "Drop unused record ESIndexJournalVerifierJob in table scheduler_clustered_jobs";
    }

    public boolean runOnSpaceImport() {
        return true;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
        log.info("Removing unused record ESIndexJournalVerifierJob in table scheduler_clustered_jobs");
        JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate(this.sessionFactory.getCurrentSession());
        jdbcTemplate.update("DELETE FROM scheduler_clustered_jobs where job_id = 'ESIndexJournalVerifierJob'");
        log.info("Removed unused record ESIndexJournalVerifierJob in table scheduler_clustered_jobs");
    }
}

