/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.event;

public class MyWorkEvent {
    private final String username;

    public MyWorkEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}

