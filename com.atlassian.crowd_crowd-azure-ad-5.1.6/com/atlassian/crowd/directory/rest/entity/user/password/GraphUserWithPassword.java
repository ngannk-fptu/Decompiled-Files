/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.user.password;

import com.atlassian.crowd.directory.rest.entity.user.GraphUser;
import com.atlassian.crowd.directory.rest.entity.user.password.PasswordProfile;
import org.codehaus.jackson.annotate.JsonProperty;

public class GraphUserWithPassword
extends GraphUser {
    @JsonProperty(value="passwordProfile")
    private final PasswordProfile passwordProfile;

    public GraphUserWithPassword(String id, String displayName, String givenName, String mail, String surname, String userPrincipalName, Boolean accountEnabled, PasswordProfile passwordProfile) {
        super(id, displayName, givenName, mail, surname, userPrincipalName, accountEnabled);
        this.passwordProfile = passwordProfile;
    }

    private GraphUserWithPassword() {
        this.passwordProfile = null;
    }

    public PasswordProfile getPasswordProfile() {
        return this.passwordProfile;
    }
}

