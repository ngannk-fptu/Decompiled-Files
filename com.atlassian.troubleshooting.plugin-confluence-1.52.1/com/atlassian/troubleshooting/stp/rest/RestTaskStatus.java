/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.rest;

import com.atlassian.troubleshooting.stp.action.Message;
import com.atlassian.troubleshooting.stp.rest.RestMessage;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class RestTaskStatus
extends LinkedHashMap<String, Serializable> {
    public static final String ID = "id";
    public static final String AVAILABLE = "available";
    public static final String PROGRESS_PERCENTAGE = "progressPercentage";
    public static final String PROGRESS_MESSAGE = "progressMessage";
    public static final String WARNINGS = "warnings";
    public static final String ERRORS = "errors";
    private static final long serialVersionUID = 2518975556106668099L;

    public RestTaskStatus(TaskMonitor<?> monitor) {
        this.put(ID, monitor.getTaskId());
        this.put(AVAILABLE, monitor.isDone());
        this.put(PROGRESS_PERCENTAGE, monitor.getProgressPercentage());
        this.put(PROGRESS_MESSAGE, monitor.getProgressMessage());
        this.addMessages(WARNINGS, monitor.getWarnings());
        this.addMessages(ERRORS, monitor.getErrors());
    }

    private void addMessages(String key, Collection<Message> messages) {
        if (!messages.isEmpty()) {
            Serializable restMessages = messages.stream().map(RestMessage::new).collect(Collectors.toCollection(ArrayList::new));
            this.put(key, restMessages);
        }
    }
}

