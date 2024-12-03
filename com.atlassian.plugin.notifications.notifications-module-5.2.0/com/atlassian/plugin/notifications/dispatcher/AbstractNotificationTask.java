/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.log4j.Logger
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.DefaultNotificationAddress;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.api.queue.NotificationTask;
import com.atlassian.plugin.notifications.api.queue.TaskStatus;
import com.atlassian.plugin.notifications.dispatcher.NotificationError;
import com.atlassian.plugin.notifications.dispatcher.NotificationQueueMonitor;
import com.atlassian.plugin.notifications.dispatcher.TaskComponents;
import com.atlassian.plugin.notifications.dispatcher.util.DeliveryStatus;
import com.atlassian.sal.api.user.UserKey;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public abstract class AbstractNotificationTask
implements NotificationTask {
    private static final Logger log = Logger.getLogger(AbstractNotificationTask.class);
    private final String id;
    private final RecipientType recipientType;
    private long nextAttemptTime;
    protected final NotificationEvent event;
    protected TaskStatus status;
    private int sendCount = 0;
    protected final TaskComponents components;
    private final NotificationQueueMonitor monitor;

    protected AbstractNotificationTask(TaskComponents components, NotificationEvent event, RecipientType recipientType, NotificationQueueMonitor monitor) {
        this.monitor = monitor;
        this.id = UUID.randomUUID().toString();
        this.event = event;
        this.recipientType = recipientType;
        this.status = new TaskStatus(this.id, TaskStatus.State.NEW);
        this.components = components;
        this.monitor.taskAdded(this);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public TaskStatus getStatus() {
        return this.status;
    }

    @Override
    public void setState(TaskStatus.State state) {
        this.status.setState(state);
    }

    @Override
    public long getNextAttemptTime() {
        return this.nextAttemptTime;
    }

    @Override
    public void setQueuedForRetry(long time) {
        this.nextAttemptTime = time;
        this.setState(TaskStatus.State.AWAITING_RESEND);
    }

    @Override
    public RecipientType getRecipientType() {
        return this.recipientType;
    }

    @Override
    public NotificationEvent getEvent() {
        return this.event;
    }

    @Override
    public int getSendCount() {
        return this.sendCount;
    }

    @Override
    public final void run() {
        try {
            this.components.getErrorRegistry().removeTaskErrors(this.getId());
            ++this.sendCount;
            DeliveryStatus lastDelivery = this.execute();
            if (lastDelivery.equals((Object)DeliveryStatus.ERROR)) {
                this.setState(TaskStatus.State.ERROR);
                this.monitor.taskError(this);
            } else {
                this.setState(TaskStatus.State.DONE);
                this.monitor.taskCompleted(this);
            }
        }
        catch (Throwable e) {
            this.setState(TaskStatus.State.ERROR);
            this.components.getErrorRegistry().addUnknownError(-1, this, new NotificationError("Unknown error '" + e.getMessage() + "' sending a notification.", e));
            this.monitor.taskError(this);
        }
    }

    protected NotificationAddress resolveAddressFor(RoleRecipient recipient, Map<String, Object> context, ServerConfiguration serverConfig) {
        UserKey userKey = recipient.getUserKey();
        UserNotificationPreferences pref = this.components.getNotificationPreferencesManager().getPreferences(userKey);
        String usernameToServerMapping = pref.getServerMapping(serverConfig);
        if (StringUtils.isBlank((CharSequence)usernameToServerMapping) && log.isDebugEnabled()) {
            log.debug((Object)("Neither global nor default user ID templates could be found for server '" + serverConfig.getServerName() + "' of medium '" + this + "' for user '" + userKey + "'. Will try global ID template."));
        }
        String resolvedAddressName = this.components.getMacroResolver().resolveAll(usernameToServerMapping, context);
        return new DefaultNotificationAddress((Option<String>)Option.none(), resolvedAddressName);
    }

    public abstract DeliveryStatus execute();
}

