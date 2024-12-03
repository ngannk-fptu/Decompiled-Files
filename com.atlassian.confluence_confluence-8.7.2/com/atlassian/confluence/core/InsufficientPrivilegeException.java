/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.core;

import org.apache.commons.lang3.StringUtils;

public class InsufficientPrivilegeException
extends RuntimeException {
    private final String username;

    public InsufficientPrivilegeException(String username) {
        this(username, "");
    }

    public InsufficientPrivilegeException(String username, String message) {
        super(message);
        this.username = username;
    }

    @Override
    public String getMessage() {
        return "User [" + (StringUtils.isNotBlank((CharSequence)this.username) ? this.username : "Anonymous") + "] does not have the required privileges." + super.getMessage();
    }
}

