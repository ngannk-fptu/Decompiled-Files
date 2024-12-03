/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.plugins.emailgateway.api.EmailStagingException;
import com.atlassian.user.User;

public class UserBlacklistedException
extends EmailStagingException {
    public UserBlacklistedException(User user) {
        super("User " + user.getName() + " has been temporarily blacklisted");
    }
}

