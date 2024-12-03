/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.service;

import java.util.concurrent.Executor;

public interface SystemStatusService {
    public void runWhenCompletelyUp(Runnable var1, Executor var2);
}

