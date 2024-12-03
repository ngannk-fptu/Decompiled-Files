/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.ExpiringValue
 *  org.apache.http.client.ResponseHandler
 */
package com.atlassian.plugins.navlink.consumer.http;

import com.atlassian.failurecache.ExpiringValue;
import java.io.IOException;
import org.apache.http.client.ResponseHandler;

public interface HttpRequestFactory {
    public <T> ExpiringValue<T> executeGetRequest(String var1, ResponseHandler<T> var2) throws IOException;
}

