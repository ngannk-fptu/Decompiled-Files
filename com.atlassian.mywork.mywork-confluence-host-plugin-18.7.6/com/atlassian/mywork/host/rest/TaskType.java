/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.host.rest;

public enum TaskType {
    inline_task("inline-task"),
    personal_task("notes");

    public final String entityType;

    private TaskType(String entityType) {
        this.entityType = entityType;
    }
}

