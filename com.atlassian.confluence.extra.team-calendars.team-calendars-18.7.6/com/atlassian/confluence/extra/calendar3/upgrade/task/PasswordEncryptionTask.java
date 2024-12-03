/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.extra.calendar3.model.persistence.ExtraSubCalendarPropertyEntity;
import com.atlassian.confluence.extra.calendar3.util.EncryptKeyHolder;
import com.atlassian.confluence.extra.calendar3.util.EncryptionException;
import com.atlassian.confluence.extra.calendar3.util.EncryptionUtils;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import net.java.ao.Query;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordEncryptionTask
extends ConfluenceAbstractLongRunningTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordEncryptionTask.class);
    private ActiveObjects ao;
    private final EncryptKeyHolder keyHolder;
    private TransactionTemplate transactionTemplate;

    public PasswordEncryptionTask(TransactionTemplate transactionTemplate, EncryptKeyHolder keyHolder, ActiveObjects ao) {
        this.ao = ao;
        this.keyHolder = keyHolder;
        this.transactionTemplate = transactionTemplate;
    }

    protected void runInternal() {
        Exception ex = (Exception)this.transactionTemplate.execute(() -> {
            try {
                LOGGER.info("====================Start Upgrade Task To Encrypt Password for External Subscription Calendar==============================");
                ExtraSubCalendarPropertyEntity[] passwordProperties = (ExtraSubCalendarPropertyEntity[])this.ao.find(ExtraSubCalendarPropertyEntity.class, Query.select().where("KEY = ?", new Object[]{"password"}));
                for (int index = 0; index < passwordProperties.length; ++index) {
                    ExtraSubCalendarPropertyEntity passwordProperty = passwordProperties[index];
                    String unencryptPass = passwordProperty.getValue();
                    if (EncryptionUtils.isEncrypted(unencryptPass) || StringUtils.isBlank(unencryptPass)) continue;
                    String encryptPass = null;
                    try {
                        encryptPass = EncryptionUtils.encrypt(this.keyHolder.getKey(), unencryptPass);
                    }
                    catch (EncryptionException e) {
                        LOGGER.error("Could not encrypt password on upgrading task", (Throwable)e);
                    }
                    passwordProperty.setValue(encryptPass);
                    passwordProperty.save();
                    this.progress.setPercentage(index, passwordProperties.length);
                }
                this.progress.setCompletedSuccessfully(true);
                return null;
            }
            catch (Exception e) {
                LOGGER.error("Exception happens when running ExternalSubscriptionPasswordEncrypterUpgradeTask ", (Throwable)e);
                this.progress.setCompletedSuccessfully(false);
                return e;
            }
        });
        this.progress.setPercentage(100);
        this.progress.setCompletedSuccessfully(ex == null);
    }

    public String getName() {
        return "Encrypting external calendar password";
    }
}

