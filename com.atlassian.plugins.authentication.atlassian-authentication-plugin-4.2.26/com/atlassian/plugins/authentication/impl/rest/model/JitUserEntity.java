/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class JitUserEntity {
    @JsonProperty(value="username")
    private final String username;
    @JsonProperty(value="display-name")
    private final String displayName;
    @JsonProperty(value="email")
    private final String email;

    public JitUserEntity(String username, String displayName, String email) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JitUserEntity that = (JitUserEntity)o;
        return Objects.equals(this.username, that.username) && Objects.equals(this.displayName, that.displayName) && Objects.equals(this.email, that.email);
    }

    public int hashCode() {
        return Objects.hash(this.username, this.displayName, this.email);
    }
}

