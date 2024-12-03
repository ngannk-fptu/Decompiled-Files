/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.methods.HttpUriRequest
 */
package com.atlassian.confluence.plugins.synchrony.service.http;

import org.apache.http.client.methods.HttpUriRequest;

public interface SynchronyLockingApiRequest {
    public HttpUriRequest getHttpRequest();
}

