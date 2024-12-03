/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.common;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface IdGenerator {
    @Nonnull
    public String generate();
}

