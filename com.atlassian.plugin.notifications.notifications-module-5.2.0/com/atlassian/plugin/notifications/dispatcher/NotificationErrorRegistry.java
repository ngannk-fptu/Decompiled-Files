/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.plugin.notifications.api.queue.NotificationTask;
import com.atlassian.plugin.notifications.dispatcher.NotificationError;
import com.atlassian.plugin.notifications.dispatcher.TaskErrors;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public interface NotificationErrorRegistry {
    public static final int MAX_TASKS_PER_SERVER = 10;
    public static final int MAX_ERRORS_PER_TASK = 20;

    public Logger getLogger();

    public void addError(int var1, NotificationTask var2, NotificationError var3);

    public void addUnknownError(int var1, NotificationTask var2, NotificationError var3);

    public int getUnknownErrorCount();

    public void removeErrors(int var1);

    public void removeTaskErrors(String var1);

    public Map<Integer, List<TaskErrors>> getServerErrors();

    public void logSuccess(int var1);

    public Date getLastEventDate(int var1);
}

