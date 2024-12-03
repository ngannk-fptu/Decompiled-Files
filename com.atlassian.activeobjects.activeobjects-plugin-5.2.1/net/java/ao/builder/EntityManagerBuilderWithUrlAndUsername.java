/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import net.java.ao.builder.EntityManagerBuilderWithUrlAndUsernameAndPassword;

public final class EntityManagerBuilderWithUrlAndUsername {
    private final String url;
    private final String username;

    EntityManagerBuilderWithUrlAndUsername(String url, String username) {
        this.url = url;
        this.username = username;
    }

    public EntityManagerBuilderWithUrlAndUsernameAndPassword password(String password) {
        return new EntityManagerBuilderWithUrlAndUsernameAndPassword(this.url, this.username, password);
    }
}

