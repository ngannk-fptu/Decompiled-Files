/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.mobile.webresource;

public final class MobileResourceServerServletUrlRewriter {
    private static final String MOBILE_RESOURCE_SERVER_PREFIX = "/plugins/servlet/mobile";
    private static final String CONTEXT_BATCH_SEGMENT = "/download/contextbatch/";
    private static final String BATCH_SEGMENT = "/download/batch";
    private static final String RESOURCE_SEGMENT = "/download/resources";

    public static String apply(String url) {
        if (url.indexOf(CONTEXT_BATCH_SEGMENT) != -1) {
            url = url.replace(CONTEXT_BATCH_SEGMENT, "/plugins/servlet/mobile/download/contextbatch/");
        }
        if (url.indexOf(BATCH_SEGMENT) != -1) {
            url = url.replace(BATCH_SEGMENT, "/plugins/servlet/mobile/download/batch");
        }
        if (url.indexOf(RESOURCE_SEGMENT) != -1) {
            url = url.replace(RESOURCE_SEGMENT, "/plugins/servlet/mobile/download/resources");
        }
        return url;
    }
}

