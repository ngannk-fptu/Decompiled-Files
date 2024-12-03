/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import net.java.ao.builder.EntityManagerBuilderWithUrlAndUsername;

public final class EntityManagerBuilderWithUrl {
    private final String url;

    EntityManagerBuilderWithUrl(String url) {
        this.url = url;
    }

    public EntityManagerBuilderWithUrlAndUsername username(String username) {
        return new EntityManagerBuilderWithUrlAndUsername(this.url, username);
    }
}

