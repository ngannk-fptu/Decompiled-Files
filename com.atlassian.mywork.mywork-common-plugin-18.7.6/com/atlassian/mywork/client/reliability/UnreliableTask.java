/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.client.reliability;

public final class UnreliableTask {
    public final String appLinkId;
    private final String taskData;

    public UnreliableTask(String appLinkId, String taskData) {
        this.appLinkId = appLinkId;
        this.taskData = taskData;
    }

    public String getTaskData() {
        return this.taskData;
    }
}

