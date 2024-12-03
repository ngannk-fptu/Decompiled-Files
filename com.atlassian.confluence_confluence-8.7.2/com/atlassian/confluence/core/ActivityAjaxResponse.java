/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

public class ActivityAjaxResponse {
    private String fullName;
    private String lastEditMessage;
    private String userName;

    public ActivityAjaxResponse(String fullName, String lastEditMessage, String userName) {
        this.fullName = fullName;
        this.lastEditMessage = lastEditMessage;
        this.userName = userName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getLastEditMessage() {
        return this.lastEditMessage;
    }

    public String getUserName() {
        return this.userName;
    }
}

