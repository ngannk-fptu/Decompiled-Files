/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.cors;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

public abstract class CorsUtils {
    public static boolean isCorsRequest(HttpServletRequest request) {
        return request.getHeader("Origin") != null;
    }

    public static boolean isPreFlightRequest(HttpServletRequest request) {
        return CorsUtils.isCorsRequest(request) && HttpMethod.OPTIONS.matches(request.getMethod()) && request.getHeader("Access-Control-Request-Method") != null;
    }
}

