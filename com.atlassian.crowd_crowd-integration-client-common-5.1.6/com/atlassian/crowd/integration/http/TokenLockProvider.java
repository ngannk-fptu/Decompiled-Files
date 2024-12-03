/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.integration.http;

import java.util.concurrent.locks.Lock;

@FunctionalInterface
public interface TokenLockProvider {
    public Lock getLock(String var1);
}

