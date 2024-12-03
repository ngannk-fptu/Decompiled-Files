/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.util;

import javax.servlet.http.HttpServletResponse;

public final class SecurityHeadersHelper {
    private static final String DISABLE_CLICKJACKING_PROTECTION_PROPERTY = "confluence.clickjacking.protection.disable";

    public static void intercept(HttpServletResponse response) {
        if (response != null) {
            response.setHeader("X-XSS-Protection", "1; mode=block");
            response.setHeader("X-Content-Type-Options", "nosniff");
            if (!Boolean.getBoolean(DISABLE_CLICKJACKING_PROTECTION_PROPERTY)) {
                response.setHeader("X-Frame-Options", "SAMEORIGIN");
                response.setHeader("Content-Security-Policy", "frame-ancestors 'self'");
            }
        }
    }
}

