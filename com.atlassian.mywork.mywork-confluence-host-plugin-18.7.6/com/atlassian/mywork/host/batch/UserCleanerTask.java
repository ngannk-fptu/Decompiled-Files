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

import com.atlassian.mywork.host.dao.UserDao;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCleanerTask
implements PluginJob {
    private static final Logger log = LoggerFactory.getLogger(UserCleanerTask.class);

    public void execute(Map<String, Object> jobDataMap) {
        UserDao userDao = (UserDao)jobDataMap.get(UserDao.class.getName());
        TransactionTemplate transactionTemplate = (TransactionTemplate)jobDataMap.get(TransactionTemplate.class.getName());
        transactionTemplate.execute(() -> {
            log.info("Deleted {} removed users", (Object)userDao.deleteRemovedUsers());
            return null;
        });
    }
}

