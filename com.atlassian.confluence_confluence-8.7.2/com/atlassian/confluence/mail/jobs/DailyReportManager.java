/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.mail.jobs;

import org.springframework.transaction.annotation.Transactional;

public interface DailyReportManager {
    @Transactional(readOnly=true)
    public void generateDailyReports();
}

