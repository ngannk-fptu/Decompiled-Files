/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.rest.entity;

import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.queue.RecipientDescription;
import com.atlassian.plugin.notifications.dispatcher.NotificationError;
import com.atlassian.plugin.notifications.rest.entity.TaskState;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class QueueItem
implements Comparable<QueueItem> {
    @JsonProperty
    private final String id;
    @JsonProperty
    private final Long duration;
    @JsonProperty
    private final String prettyDuration;
    @JsonProperty
    private final Long lastStateChangeDuration;
    @JsonProperty
    private final String prettyStateChangeDuration;
    @JsonProperty
    private final TaskState state;
    @JsonProperty
    private final List<RecipientDescription> recipients;
    @JsonProperty
    private final RecipientType recipientType;
    @JsonProperty
    private final String subject;
    @JsonProperty
    private final int retryAttempts;
    @JsonProperty
    private final int secondsUntilNextRun;
    @JsonProperty
    private final String prettyDurationNextRun;
    @JsonProperty
    private final List<NotificationError> errors;

    public QueueItem(String id, Long duration, String prettyDuration, TaskState state, List<RecipientDescription> recipients, Long lastStateChangeDuration, String prettyStateChangeDuration, RecipientType recipientType, String subject, int retryAttempts, int secondsUntilNextRun, String prettyDurationNextRun, List<NotificationError> errors) {
        this.id = id;
        this.duration = duration;
        this.prettyDuration = prettyDuration;
        this.state = state;
        this.recipients = recipients;
        this.lastStateChangeDuration = lastStateChangeDuration;
        this.prettyStateChangeDuration = prettyStateChangeDuration;
        this.recipientType = recipientType;
        this.subject = subject;
        this.retryAttempts = retryAttempts;
        this.secondsUntilNextRun = secondsUntilNextRun;
        this.prettyDurationNextRun = prettyDurationNextRun;
        this.errors = errors;
    }

    public String getId() {
        return this.id;
    }

    public Long getDuration() {
        return this.duration;
    }

    public String getPrettyDuration() {
        return this.prettyDuration;
    }

    public TaskState getState() {
        return this.state;
    }

    public List<RecipientDescription> getRecipients() {
        return this.recipients;
    }

    public Long getLastStateChangeDuration() {
        return this.lastStateChangeDuration;
    }

    public String getPrettyStateChangeDuration() {
        return this.prettyStateChangeDuration;
    }

    public RecipientType getRecipientType() {
        return this.recipientType;
    }

    public String getSubject() {
        return this.subject;
    }

    public int getRetryAttempts() {
        return this.retryAttempts;
    }

    public List<NotificationError> getErrors() {
        return this.errors;
    }

    public int getMinutesUntilNextRun() {
        return this.secondsUntilNextRun;
    }

    @Override
    public int compareTo(QueueItem o) {
        return o != null ? o.getDuration().compareTo(this.getDuration()) : 0;
    }

    public String getPrettyDurationNextRun() {
        return this.prettyDurationNextRun;
    }
}

