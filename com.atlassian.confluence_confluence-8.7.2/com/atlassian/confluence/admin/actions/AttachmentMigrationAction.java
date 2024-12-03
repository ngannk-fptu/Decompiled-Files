/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.admin.actions.AttachmentStorageSetupAction;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.util.longrunning.AttachmentMigrationLongRunningTask;
import com.atlassian.confluence.util.longrunning.LongRunningTaskUtils;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.sal.api.websudo.WebSudoRequired;

@Deprecated
@WebSudoRequired
@SystemAdminOnly
public class AttachmentMigrationAction
extends AttachmentStorageSetupAction {
    private boolean nonBackgroundTask;

    public LongRunningTask getTask() {
        return LongRunningTaskUtils.retrieveTask();
    }

    public String doConfirm() {
        return "input";
    }

    public String doMigration() {
        AttachmentMigrationLongRunningTask task = new AttachmentMigrationLongRunningTask(this.attachmentStorageType);
        if (this.isNonBackgroundTask()) {
            task.run();
        } else {
            LongRunningTaskUtils.startTask((LongRunningTask)task);
        }
        return "success";
    }

    public boolean isNonBackgroundTask() {
        return this.nonBackgroundTask;
    }

    public void setNonBackgroundTask(boolean nonBackgroundTask) {
        this.nonBackgroundTask = nonBackgroundTask;
    }
}

