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

import com.atlassian.mywork.host.dao.TaskDao;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskCleanerTask
implements PluginJob {
    private static final Logger log = LoggerFactory.getLogger(TaskCleanerTask.class);
    private static final int DAYS_TO_KEEP_COMPLETED = 7;
    private static final int DAYS_TO_KEEP_EXPIRED = Integer.getInteger("atlassian.mywork.taskcleaner.expiry.days", 28);

    public void execute(Map<String, Object> jobDataMap) {
        TaskDao taskDao = (TaskDao)jobDataMap.get(TaskDao.class.getName());
        TransactionTemplate transactionTemplate = (TransactionTemplate)jobDataMap.get(TransactionTemplate.class.getName());
        transactionTemplate.execute(() -> {
            log.info("Deleted {} old tasks", (Object)taskDao.deleteOldCompletedTasks(7));
            log.info("Deleted {} expired tasks", (Object)taskDao.deleteExpiredTasks(DAYS_TO_KEEP_EXPIRED));
            return null;
        });
    }
}

