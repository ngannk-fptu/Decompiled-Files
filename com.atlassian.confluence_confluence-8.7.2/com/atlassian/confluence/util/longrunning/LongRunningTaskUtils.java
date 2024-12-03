/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.opensymphony.xwork2.ActionContext
 */
package com.atlassian.confluence.util.longrunning;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.opensymphony.xwork2.ActionContext;
import java.util.Map;

@Deprecated
public class LongRunningTaskUtils {
    public static String startTask(LongRunningTask task, User user) {
        LongRunningTaskManager manager = LongRunningTaskUtils.getLongRunningTaskManager();
        Map session = ActionContext.getContext().getSession();
        LongRunningTaskId oldId = (LongRunningTaskId)session.get("confluence.task.longrunning");
        if (oldId != null) {
            manager.stopTrackingLongRunningTask(oldId);
        }
        LongRunningTaskId newId = manager.startLongRunningTask(user, task);
        ActionContext.getContext().getSession().put("confluence.task.longrunning", newId);
        return newId.toString();
    }

    public static void startTask(LongRunningTask task) {
        LongRunningTaskUtils.startTask(task, AuthenticatedUserThreadLocal.get());
    }

    public static LongRunningTask retrieveTask() {
        LongRunningTaskId id = (LongRunningTaskId)ActionContext.getContext().getSession().get("confluence.task.longrunning");
        if (id == null) {
            return null;
        }
        LongRunningTask task = LongRunningTaskUtils.getLongRunningTaskManager().getLongRunningTask(AuthenticatedUserThreadLocal.get(), id);
        if (task == null) {
            task = LongRunningTaskUtils.getLongRunningTaskManager().getLongRunningTask(null, id);
        }
        return task;
    }

    public static void removeTask() {
        LongRunningTaskId id = (LongRunningTaskId)ActionContext.getContext().getSession().get("confluence.task.longrunning");
        LongRunningTaskUtils.getLongRunningTaskManager().stopTrackingLongRunningTask(id);
        ActionContext.getContext().getSession().put("confluence.task.longrunning", null);
    }

    private static LongRunningTaskManager getLongRunningTaskManager() {
        return (LongRunningTaskManager)ContainerManager.getComponent((String)"longRunningTaskManager");
    }
}

