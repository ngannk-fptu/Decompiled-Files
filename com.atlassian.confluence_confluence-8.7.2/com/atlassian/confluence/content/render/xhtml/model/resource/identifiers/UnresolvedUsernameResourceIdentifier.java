/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;

public class UnresolvedUsernameResourceIdentifier
implements ResourceIdentifier {
    private final String userName;

    public UnresolvedUsernameResourceIdentifier(String userName) {
        this.userName = userName;
    }

    public String getUsername() {
        return this.userName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UnresolvedUsernameResourceIdentifier that = (UnresolvedUsernameResourceIdentifier)o;
        return this.userName.equals(that.userName);
    }

    public int hashCode() {
        return this.userName.hashCode();
    }

    public String toString() {
        return "UnresolvedUserResourceIdentifier{userName='" + this.userName + "'}";
    }
}

