/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.concurrent;

import com.atlassian.confluence.concurrent.Lock;

@Deprecated
public interface LockFactory {
    public Lock getLock(String var1);
}

