/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.user.User;

@ExperimentalApi
public class SystemUser
implements User {
    private String fullname;
    private String name;
    private String email;

    public SystemUser(String fullname, String name, String email) {
        this.fullname = fullname;
        this.name = name;
        this.email = email;
    }

    public String getFullName() {
        return this.fullname;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }
}

