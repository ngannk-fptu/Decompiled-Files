/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.internal.common.net;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;

public final class ResponseHeaderUtil {
    @VisibleForTesting
    static final String HEADER_XFRAME_OPTIONS = "X-Frame-Options";
    @VisibleForTesting
    static final String HEADER_CONTENT_SECURITY_POLICY = "Content-Security-Policy";

    private ResponseHeaderUtil() {
    }

    public static void preventCrossFrameClickJacking(@Nonnull HttpServletResponse response) {
        Objects.requireNonNull(response, "response");
        response.setHeader(HEADER_XFRAME_OPTIONS, "SAMEORIGIN");
        response.setHeader(HEADER_CONTENT_SECURITY_POLICY, "frame-ancestors 'self'");
    }
}

