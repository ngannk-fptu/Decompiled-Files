/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.protocol.HttpContext
 */
package com.amazonaws.http.apache.utils;

import com.amazonaws.annotation.SdkInternalApi;
import org.apache.http.protocol.HttpContext;

@SdkInternalApi
public final class HttpContextUtils {
    public static final String DISABLE_SOCKET_PROXY_PROPERTY = "com.amazonaws.disableSocketProxy";

    private HttpContextUtils() {
    }

    public static boolean disableSocketProxy(HttpContext ctx) {
        Object v = ctx.getAttribute(DISABLE_SOCKET_PROXY_PROPERTY);
        return v != null && (Boolean)v != false;
    }
}

