/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.service.rememberme.RememberMeToken
 */
package com.atlassian.confluence.user.persistence.dao;

import com.atlassian.seraph.service.rememberme.RememberMeToken;

public class ConfluenceRememberMeToken
implements RememberMeToken {
    private String username;
    private String token;
    private Long id;
    private long createdTime;

    public ConfluenceRememberMeToken() {
    }

    public ConfluenceRememberMeToken(RememberMeToken token) {
        this.username = token.getUserName();
        this.token = token.getRandomString();
        this.id = token.getId();
        this.createdTime = token.getCreatedTime();
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getCreatedTime() {
        return this.createdTime;
    }

    public Long getId() {
        return this.id;
    }

    public String getRandomString() {
        return this.token;
    }

    public String getUserName() {
        return this.username;
    }
}

