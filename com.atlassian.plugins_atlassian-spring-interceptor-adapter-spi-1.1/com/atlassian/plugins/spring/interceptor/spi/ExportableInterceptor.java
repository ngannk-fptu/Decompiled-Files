/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.spring.interceptor.spi;

import com.atlassian.plugins.spring.interceptor.spi.ExportableMethodInvocation;

public interface ExportableInterceptor {
    public Object invoke(ExportableMethodInvocation var1) throws Throwable;
}

