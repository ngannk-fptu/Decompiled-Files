/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.security.simple;

import org.apache.axis.security.AuthenticatedUser;

public class SimpleAuthenticatedUser
implements AuthenticatedUser {
    private String name;

    public SimpleAuthenticatedUser(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

