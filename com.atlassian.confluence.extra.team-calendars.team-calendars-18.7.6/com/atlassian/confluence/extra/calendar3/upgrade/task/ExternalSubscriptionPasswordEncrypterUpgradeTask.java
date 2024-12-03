/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.confluence.extra.calendar3.upgrade.task.CalendarModelVersion;
import com.atlassian.confluence.extra.calendar3.upgrade.task.PasswordEncryptionTask;
import com.atlassian.confluence.extra.calendar3.util.EncryptKeyHolder;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalSubscriptionPasswordEncrypterUpgradeTask
implements ActiveObjectsUpgradeTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalSubscriptionPasswordEncrypterUpgradeTask.class);
    private final EncryptKeyHolder keyHolder;

    public ExternalSubscriptionPasswordEncrypterUpgradeTask(EncryptKeyHolder keyHolder) {
        this.keyHolder = keyHolder;
    }

    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)CalendarModelVersion.CALENDAR_MODEL_VERSION_15);
    }

    public void upgrade(ModelVersion currentVersion, ActiveObjects ao) {
        try {
            PasswordEncryptionTask passwordEncryptionTask = new PasswordEncryptionTask(new TransactionTemplate(){

                public <T> T execute(TransactionCallback<T> transactionCallback) {
                    return (T)transactionCallback.doInTransaction();
                }
            }, this.keyHolder, ao);
            passwordEncryptionTask.runInternal();
        }
        catch (Exception e) {
            LOGGER.error("Exception happens when running ExternalSubscriptionPasswordEncrypterUpgradeTask ", (Throwable)e);
        }
        LOGGER.info("====================Finish Upgrade Task To Encrypt Password for External Subscription Calendar==============================");
    }
}

