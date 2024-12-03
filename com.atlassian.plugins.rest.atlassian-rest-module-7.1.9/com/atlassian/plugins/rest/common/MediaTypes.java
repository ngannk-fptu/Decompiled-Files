/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common;

import javax.ws.rs.core.MediaType;

public final class MediaTypes {
    public static final String APPLICATION_JAVASCRIPT = "application/javascript";
    public static final MediaType APPLICATION_JAVASCRIPT_TYPE = new MediaType("application", "javascript");
    public static final String MULTIPART_MIXED = "multipart/mixed";
    public static final MediaType MULTIPART_MIXED_TYPE = new MediaType("multipart", "mixed");

    private MediaTypes() {
    }
}

