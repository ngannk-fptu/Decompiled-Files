/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.service;

import java.util.concurrent.Callable;

public interface ImpersonationService {
    public void runAs(String var1, Runnable var2);

    public <V> V runAs(String var1, Callable<V> var2) throws Exception;
}

