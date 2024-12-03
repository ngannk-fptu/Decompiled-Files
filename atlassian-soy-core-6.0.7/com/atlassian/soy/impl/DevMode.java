/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.soy.impl;

public class DevMode {
    private static final String JIRA_DEV_MODE = "jira.dev.mode";
    private static final String ATLASSIAN_DEV_MODE = "atlassian.dev.mode";
    private static final String CONFLUENCE_DEV_MODE = "confluence.dev.mode";

    public static boolean isDevMode() {
        return Boolean.getBoolean(JIRA_DEV_MODE) || Boolean.getBoolean(CONFLUENCE_DEV_MODE) || Boolean.getBoolean(ATLASSIAN_DEV_MODE);
    }
}

