/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.blacklist;

public interface Blacklist<T> {
    public boolean incrementAndCheckBlacklist(T var1);

    public boolean isBlackListed(T var1);
}

