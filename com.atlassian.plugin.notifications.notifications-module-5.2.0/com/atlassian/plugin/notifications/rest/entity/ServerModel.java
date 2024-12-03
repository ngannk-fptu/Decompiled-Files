/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.Lists
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.rest.entity;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.queue.TaskStatus;
import com.atlassian.plugin.notifications.dispatcher.NotificationError;
import com.atlassian.plugin.notifications.dispatcher.TaskErrors;
import com.atlassian.plugin.notifications.rest.entity.TaskState;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class ServerModel {
    @JsonProperty
    private final int id;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final List<NotificationError> errors;
    @JsonProperty
    private final TaskState state;
    @JsonProperty
    private final long lastChecked;
    @JsonProperty
    private final String lastCheckedDuration;

    public ServerModel(ServerConfiguration config, I18nResolver i18n, List<TaskErrors> taskErrors, long lastChecked, String lastCheckedDuration) {
        this.lastChecked = lastChecked;
        this.lastCheckedDuration = lastCheckedDuration;
        this.id = config.getId();
        this.name = config.getFullName(i18n);
        ArrayList allErrors = Lists.newArrayList();
        boolean resending = false;
        boolean error = false;
        for (TaskErrors taskError : taskErrors) {
            if (taskError.getStatus().getState().equals((Object)TaskStatus.State.AWAITING_RESEND)) {
                resending = true;
            }
            if (taskError.getStatus().getState().equals((Object)TaskStatus.State.ERROR)) {
                error = true;
            }
            allErrors.addAll(taskError.getErrors());
        }
        this.state = error ? new TaskState(TaskStatus.State.ERROR.toString(), i18n.getText(TaskStatus.State.ERROR.getI18nKey())) : (resending ? new TaskState(TaskStatus.State.AWAITING_RESEND.toString(), i18n.getText(TaskStatus.State.AWAITING_RESEND.getI18nKey())) : new TaskState("OK", i18n.getText("notifications.plugin.status.ok")));
        this.errors = allErrors;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<NotificationError> getErrors() {
        return this.errors;
    }

    public TaskState getState() {
        return this.state;
    }

    public long getLastChecked() {
        return this.lastChecked;
    }

    public String getLastCheckedDuration() {
        return this.lastCheckedDuration;
    }
}

