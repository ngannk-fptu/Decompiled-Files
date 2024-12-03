/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierSet
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping;

import com.atlassian.crowd.embedded.impl.IdentifierSet;
import java.util.Objects;
import java.util.Set;

public class JitUserData {
    private final String identityProviderId;
    private final String username;
    private final String displayName;
    private final String email;
    private final IdentifierSet groups;

    public JitUserData(String identityProviderId, String username, String displayName, String email, Set<String> groups) {
        this.identityProviderId = identityProviderId;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.groups = new IdentifierSet(groups);
    }

    public String getIdentityProviderId() {
        return this.identityProviderId;
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

    public IdentifierSet getGroups() {
        return this.groups;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JitUserData that = (JitUserData)o;
        return Objects.equals(this.identityProviderId, that.identityProviderId) && Objects.equals(this.username, that.username) && Objects.equals(this.displayName, that.displayName) && Objects.equals(this.email, that.email) && Objects.equals(this.groups, that.groups);
    }

    public int hashCode() {
        return Objects.hash(this.identityProviderId, this.username, this.displayName, this.email, this.groups);
    }
}

