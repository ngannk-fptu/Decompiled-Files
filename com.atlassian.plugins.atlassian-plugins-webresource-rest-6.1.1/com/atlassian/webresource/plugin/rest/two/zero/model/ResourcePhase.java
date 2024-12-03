/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webresource.plugin.rest.two.zero.model;

import javax.annotation.Nonnull;

public class ResourcePhase {
    private final String name;

    public ResourcePhase(@Nonnull String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

