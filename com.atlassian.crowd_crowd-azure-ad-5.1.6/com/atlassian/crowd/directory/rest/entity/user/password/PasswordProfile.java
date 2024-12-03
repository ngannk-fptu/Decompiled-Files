/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.user.password;

import org.codehaus.jackson.annotate.JsonProperty;

public class PasswordProfile {
    @JsonProperty(value="forceChangePasswordNextSignIn")
    private static final boolean forceChangePasswordNextSignIn = false;
    @JsonProperty(value="password")
    private final String password;

    private PasswordProfile() {
        this.password = null;
    }

    public PasswordProfile(String password) {
        this.password = password;
    }

    public boolean isForceChangePasswordNextSignIn() {
        return false;
    }

    public String getPassword() {
        return this.password;
    }
}

