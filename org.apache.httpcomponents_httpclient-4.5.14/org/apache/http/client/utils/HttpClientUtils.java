/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.util.EntityUtils
 */
package org.apache.http.client.utils;

import java.io.Closeable;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

public class HttpClientUtils {
    private HttpClientUtils() {
    }

    public static void closeQuietly(HttpResponse response) {
        HttpEntity entity;
        if (response != null && (entity = response.getEntity()) != null) {
            try {
                EntityUtils.consume((HttpEntity)entity);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void closeQuietly(CloseableHttpResponse response) {
        if (response != null) {
            try {
                try {
                    EntityUtils.consume((HttpEntity)response.getEntity());
                }
                finally {
                    response.close();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public static void closeQuietly(HttpClient httpClient) {
        if (httpClient != null && httpClient instanceof Closeable) {
            try {
                ((Closeable)((Object)httpClient)).close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }
}

