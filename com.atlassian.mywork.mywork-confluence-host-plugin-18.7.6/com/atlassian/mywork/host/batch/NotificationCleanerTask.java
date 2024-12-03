/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.scheduling.PluginJob
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.host.batch;

import com.atlassian.mywork.host.dao.NotificationDao;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationCleanerTask
implements PluginJob {
    private static final Logger log = LoggerFactory.getLogger(NotificationCleanerTask.class);
    private static final int DAYS_TO_KEEP = 14;
    private static final int DAYS_TO_KEEP_UNREAD = Integer.getInteger("atlassian.mywork.notificationcleaner.expiry.days", 28);

    public void execute(Map<String, Object> jobDataMap) {
        NotificationDao notificationDao = (NotificationDao)jobDataMap.get(NotificationDao.class.getName());
        TransactionTemplate transactionTemplate = (TransactionTemplate)jobDataMap.get(TransactionTemplate.class.getName());
        transactionTemplate.execute(() -> {
            log.info("Deleted {} old notifications", (Object)notificationDao.deleteOldNotifications(14, true));
            log.info("Deleted {} old unread notifications", (Object)notificationDao.deleteOldNotifications(DAYS_TO_KEEP_UNREAD, false));
            return null;
        });
    }
}

