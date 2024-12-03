/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.service.rememberme;

public interface RememberMeToken {
    public Long getId();

    public String getRandomString();

    public String getUserName();

    public long getCreatedTime();
}

