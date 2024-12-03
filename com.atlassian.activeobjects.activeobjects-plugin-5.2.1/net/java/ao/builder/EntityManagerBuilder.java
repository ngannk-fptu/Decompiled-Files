/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import net.java.ao.builder.EntityManagerBuilderWithUrl;

public final class EntityManagerBuilder {
    public static EntityManagerBuilderWithUrl url(String url) {
        return new EntityManagerBuilderWithUrl(url);
    }
}

