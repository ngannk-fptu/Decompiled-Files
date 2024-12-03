/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.network;

import java.util.concurrent.TimeUnit;

public interface ConnectivityTester {
    public boolean isReachable(String var1, int var2, TimeUnit var3);
}

