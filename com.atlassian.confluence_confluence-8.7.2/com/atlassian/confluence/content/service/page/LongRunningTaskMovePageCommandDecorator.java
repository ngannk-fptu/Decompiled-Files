/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.core.util.ProgressMeter
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.MovePageCommand;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.internal.longrunning.LongRunningTaskManagerInternal;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.core.util.ProgressMeter;
import java.util.Collection;
import java.util.Objects;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class LongRunningTaskMovePageCommandDecorator
extends ConfluenceAbstractLongRunningTask
implements MovePageCommand {
    private final MovePageCommand commandDelegate;
    private final LongRunningTaskManagerInternal longRunningTaskManager;
    private LongRunningTaskId longTaskId;

    public LongRunningTaskMovePageCommandDecorator(MovePageCommand commandDelegate, LongRunningTaskManagerInternal longRunningTaskManager) {
        this.commandDelegate = Objects.requireNonNull(commandDelegate);
        this.longRunningTaskManager = Objects.requireNonNull(longRunningTaskManager);
    }

    public MovePageCommand getCommandDelegate() {
        return this.commandDelegate;
    }

    @Override
    public Page getPage() {
        return this.commandDelegate.getPage();
    }

    @Override
    public ProgressMeter getProgressMeter() {
        return this.progress;
    }

    @Override
    public boolean isValid() {
        return this.commandDelegate.isValid();
    }

    @Override
    public Collection<ValidationError> getValidationErrors() {
        return this.commandDelegate.getValidationErrors();
    }

    @Override
    public boolean isAuthorized() {
        return this.commandDelegate.isAuthorized();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    protected void runInternal() {
        try {
            this.commandDelegate.execute();
            this.progress.setCompletedSuccessfully(true);
            this.progress.setStatus(this.getI18nBean().getText("progress.move.page.success"));
            this.progress.setPercentage(100);
        }
        catch (Exception ex) {
            log.error("move page failed.", (Throwable)ex);
            this.progress.setCompletedSuccessfully(false);
            this.progress.setStatus(this.getI18nBean().getText("progress.move.page.failed"));
            throw ex;
        }
        log.info("Page Move completed.");
    }

    private I18NBean getI18nBean() {
        return GeneralUtil.getI18n();
    }

    @Override
    public void execute() {
        this.longTaskId = this.longRunningTaskManager.startLongRunningTask(AuthenticatedUserThreadLocal.get(), (LongRunningTask)this);
    }

    public String getName() {
        return "Move Page";
    }

    public String getLongTaskId() {
        return this.longTaskId != null ? this.longTaskId.toString() : "0";
    }
}

