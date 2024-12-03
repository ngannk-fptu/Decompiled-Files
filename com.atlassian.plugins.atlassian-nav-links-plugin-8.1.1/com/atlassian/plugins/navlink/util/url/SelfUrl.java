/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.navlink.util.url;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class SelfUrl {
    static final String FORWARD_REQUEST_URI_ATTRIBUTE_NAME = "javax.servlet.forward.request_uri";

    @Nonnull
    public static String extractFrom(@Nonnull HttpServletRequest httpServletRequest) {
        String requestUri = SelfUrl.getRequestURI(httpServletRequest);
        String contextPath = httpServletRequest.getContextPath();
        return StringUtils.removeStart((String)requestUri, (String)contextPath);
    }

    private static String getRequestURI(HttpServletRequest httpServletRequest) {
        Object attribute = httpServletRequest.getAttribute(FORWARD_REQUEST_URI_ATTRIBUTE_NAME);
        if (attribute != null) {
            return attribute.toString();
        }
        return httpServletRequest.getRequestURI();
    }
}

