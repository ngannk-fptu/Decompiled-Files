/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createjiracontent.rest.beans;

public class JiraIssue {
    private String key;

    public JiraIssue() {
    }

    public JiraIssue(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

