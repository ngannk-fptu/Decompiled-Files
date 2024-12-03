/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.CrowdException
 */
package com.atlassian.crowd.emailchange;

import com.atlassian.crowd.exception.CrowdException;

public class SameEmailAddressException
extends CrowdException {
    private final String username;
    private final String emailAddress;

    public SameEmailAddressException(String username, String emailAddress) {
        super(String.format("User %s is already using email %s", username, emailAddress));
        this.username = username;
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getUsername() {
        return this.username;
    }
}

