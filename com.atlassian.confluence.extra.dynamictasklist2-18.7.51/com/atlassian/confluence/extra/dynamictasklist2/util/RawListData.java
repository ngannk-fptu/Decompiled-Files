/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.dynamictasklist2.util;

import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListConfig;

public class RawListData {
    public final String name;
    public final String body;
    public final String macro;
    public final TaskListConfig config;

    public RawListData(String name, String body, String macro, TaskListConfig config) {
        this.name = name;
        this.body = body;
        this.macro = macro;
        this.config = config;
    }
}

