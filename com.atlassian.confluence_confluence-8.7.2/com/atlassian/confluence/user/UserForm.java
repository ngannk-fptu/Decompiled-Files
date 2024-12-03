/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.user;

import com.atlassian.sal.api.user.UserKey;

public class UserForm {
    private UserKey userKey;
    private String username;
    private String fullName;
    private String email;
    private String password;
    private String confirm;

    public UserForm() {
    }

    public UserForm(String username, String fullName, String email, String password, String confirm) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirm = confirm;
    }

    public UserForm(String username, String fullName, String email) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
    }

    public UserForm(UserKey userKey, String username, String fullName, String email) {
        this.userKey = userKey;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    public String getUsername() {
        return this.username;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getConfirm() {
        return this.confirm;
    }

    public void setUserKey(UserKey userKey) {
        this.userKey = userKey;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
}

