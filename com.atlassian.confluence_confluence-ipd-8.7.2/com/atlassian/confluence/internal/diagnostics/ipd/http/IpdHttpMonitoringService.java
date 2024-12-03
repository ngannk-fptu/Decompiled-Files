/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http;

import javax.servlet.ServletRequest;

public interface IpdHttpMonitoringService {
    public void registerHttpRequest(ServletRequest var1);

    public long numberOfRecentRequests(Long var1);
}

