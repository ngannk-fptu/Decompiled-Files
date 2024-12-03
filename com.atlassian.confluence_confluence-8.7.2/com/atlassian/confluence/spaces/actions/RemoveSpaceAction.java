/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.impl.spaces.SpaceRemovalLongRunningTask;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.sal.api.websudo.WebSudoRequired;

@WebSudoRequired
public class RemoveSpaceAction
extends AbstractSpaceAdminAction {
    private IndexManager indexManager;
    private LongRunningTaskId taskId;
    private LongRunningTaskManager longRunningTaskManager;

    @Override
    public void validate() {
        super.validate();
        if (this.indexManager.isReIndexing()) {
            this.addCannotRemoveSpaceError();
        }
    }

    public String doRemove() throws Exception {
        if (this.getSpace() == null) {
            return "input";
        }
        ConfluenceUser remoteUser = this.getAuthenticatedUser();
        SpaceRemovalLongRunningTask spaceRemovalLongRunningTask = new SpaceRemovalLongRunningTask(this.getSpaceKey(), this.spaceManager, remoteUser, this.getI18n());
        this.taskId = this.longRunningTaskManager.startLongRunningTask(remoteUser, (LongRunningTask)spaceRemovalLongRunningTask);
        return "success";
    }

    private void addCannotRemoveSpaceError() {
        Object baseUrl = this.getGlobalSettings().getBaseUrl();
        if (!((String)baseUrl).endsWith("/")) {
            baseUrl = (String)baseUrl + "/";
        }
        this.addActionError(this.getText("com.atlassian.confluence.spaces.actions.RemoveSpaceAction.action.not.possible", new Object[]{(String)baseUrl + "admin/search-indexes.action"}));
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public LongRunningTaskId getTaskId() {
        return this.taskId;
    }

    public void setLongRunningTaskManager(LongRunningTaskManager longRunningTaskManager) {
        this.longRunningTaskManager = longRunningTaskManager;
    }
}

