/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.DeferredUpgradeTask
 *  com.atlassian.confluence.upgrade.UpgradeError
 *  com.atlassian.confluence.upgrade.UpgradeTask
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.DurationFormatUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.admin.actions.upgrade;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.upgrade.DeferredUpgradeTask;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.UpgradeTask;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@WebSudoRequired
@SystemAdminOnly
public class ForceUpgradeAction
extends ConfluenceActionSupport
implements Beanable {
    private static final Logger log = LoggerFactory.getLogger(ForceUpgradeAction.class);
    private List<String> manuallyRunnableUpgradeTasks;
    private String upgradeTaskToRun;
    private PlatformTransactionManager transactionManager;
    private Collection<UpgradeError> upgradeErrors;
    private Exception upgradeException;

    @Override
    public String doDefault() {
        if (StringUtils.isBlank((CharSequence)this.upgradeTaskToRun)) {
            this.upgradeTaskToRun = this.manuallyRunnableUpgradeTasks.get(0);
        }
        return "input";
    }

    @Override
    public void validate() {
        super.validate();
        if (StringUtils.isBlank((CharSequence)this.upgradeTaskToRun)) {
            this.addFieldError("upgradeTaskToRun", "select.upgrade.task", new Object[0]);
        }
        if (!this.manuallyRunnableUpgradeTasks.contains(this.upgradeTaskToRun)) {
            this.addFieldError("upgradeTaskToRun", "invalid.upgrade.task", new Object[]{this.upgradeTaskToRun});
        }
    }

    @Override
    public Object getBean() {
        return ImmutableMap.of((Object)"description", (Object)this.getUpgradeTaskDescription());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String execute() {
        Date finishTime;
        Date startTime = new Date();
        log.info("Upgrade task {} starting", (Object)this.upgradeTaskToRun);
        try {
            final UpgradeTask upgradeTask = this.getUpgradeTask();
            DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
            TransactionTemplate template = new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionDefinition);
            TransactionCallbackWithoutResult callback = new TransactionCallbackWithoutResult(){

                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        if (upgradeTask instanceof DeferredUpgradeTask) {
                            ((DeferredUpgradeTask)upgradeTask).doDeferredUpgrade();
                        } else {
                            upgradeTask.doUpgrade();
                        }
                    }
                    catch (Exception e) {
                        ForceUpgradeAction.this.upgradeException = e;
                        log.error("Upgrade failed with exception: " + e.getMessage(), (Throwable)e);
                        ForceUpgradeAction.this.addActionError("upgrade.failed.exception", e.getClass().getName() + ": " + e.getMessage());
                    }
                    finally {
                        ForceUpgradeAction.this.upgradeErrors = upgradeTask.getErrors();
                        ForceUpgradeAction.this.logUpgradeErrors(ForceUpgradeAction.this.upgradeErrors);
                        ForceUpgradeAction.this.renderUpgradeErrors(ForceUpgradeAction.this.upgradeErrors);
                    }
                }
            };
            template.execute((TransactionCallback)callback);
        }
        finally {
            finishTime = new Date();
        }
        String durationText = DurationFormatUtils.formatDurationWords((long)this.getDuration(startTime, finishTime), (boolean)true, (boolean)false);
        if (this.upgradeErrors != null && !this.upgradeErrors.isEmpty() || this.upgradeException != null) {
            log.error("Upgrade task {} failed with errors in {}", (Object)this.upgradeTaskToRun, (Object)durationText);
            return "error";
        }
        this.addActionMessage(this.getText("upgrade.successful", durationText));
        log.info("Upgrade task {} completed successfully in {}", (Object)this.upgradeTaskToRun, (Object)durationText);
        return "success";
    }

    private void renderUpgradeErrors(Collection<UpgradeError> upgradeErrors) {
        if (upgradeErrors != null && !upgradeErrors.isEmpty()) {
            this.addActionError("upgrade.failed.errors", upgradeErrors.size());
            for (UpgradeError error : upgradeErrors) {
                this.addActionError("upgrade.failed.single.error", error.getMessage(), this.getThrowableMessage(error.getError()));
            }
        }
    }

    private void logUpgradeErrors(Collection<UpgradeError> upgradeErrors) {
        if (upgradeErrors != null && !upgradeErrors.isEmpty()) {
            log.error("The following upgrade errors occurred:");
            for (UpgradeError error : upgradeErrors) {
                log.error(error.getMessage(), error.getError());
            }
        }
    }

    private long getDuration(Date start, Date end) {
        return end.getTime() - start.getTime();
    }

    private String getThrowableMessage(Throwable t) {
        if (t == null) {
            return null;
        }
        return t.getMessage();
    }

    public List<String> getManuallyRunnableUpgradeTasks() {
        return this.manuallyRunnableUpgradeTasks;
    }

    public void setManuallyRunnableUpgradeTasks(List<String> manuallyRunnableUpgradeTasks) {
        this.manuallyRunnableUpgradeTasks = manuallyRunnableUpgradeTasks;
        Collections.sort(this.manuallyRunnableUpgradeTasks);
    }

    public UpgradeTask getUpgradeTask() {
        if (StringUtils.isBlank((CharSequence)this.upgradeTaskToRun)) {
            return null;
        }
        return (UpgradeTask)ContainerManager.getComponent((String)this.upgradeTaskToRun);
    }

    public String getUpgradeTaskDescription() {
        UpgradeTask task = this.getUpgradeTask();
        if (task == null) {
            return "";
        }
        return task.getShortDescription();
    }

    public String getUpgradeTaskToRun() {
        return this.upgradeTaskToRun;
    }

    public void setUpgradeTaskToRun(String upgradeTaskToRun) {
        this.upgradeTaskToRun = upgradeTaskToRun;
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}

