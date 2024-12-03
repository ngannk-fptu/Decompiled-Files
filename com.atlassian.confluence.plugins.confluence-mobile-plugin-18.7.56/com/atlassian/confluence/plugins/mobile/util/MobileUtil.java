/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.mobile.util;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;

public final class MobileUtil {
    private MobileUtil() {
    }

    public static boolean isNullOrEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static String extractURL(HttpServletRequest request) {
        return request.getRequestURI().replace(request.getContextPath(), "");
    }
}

