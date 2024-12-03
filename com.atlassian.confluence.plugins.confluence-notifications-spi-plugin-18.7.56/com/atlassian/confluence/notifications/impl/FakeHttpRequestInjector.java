/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.impl;

import java.util.concurrent.Callable;

public interface FakeHttpRequestInjector {
    public <T> T withRequest(Callable<T> var1);
}

