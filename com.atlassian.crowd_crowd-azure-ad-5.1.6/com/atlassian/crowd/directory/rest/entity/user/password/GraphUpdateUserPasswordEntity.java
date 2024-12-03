/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.user.password;

import com.atlassian.crowd.directory.rest.entity.user.password.PasswordProfile;
import org.codehaus.jackson.annotate.JsonProperty;

public class GraphUpdateUserPasswordEntity {
    @JsonProperty(value="passwordProfile")
    private final PasswordProfile passwordProfile;

    private GraphUpdateUserPasswordEntity() {
        this.passwordProfile = null;
    }

    public GraphUpdateUserPasswordEntity(PasswordProfile passwordProfile) {
        this.passwordProfile = passwordProfile;
    }

    public PasswordProfile getPasswordProfile() {
        return this.passwordProfile;
    }
}

