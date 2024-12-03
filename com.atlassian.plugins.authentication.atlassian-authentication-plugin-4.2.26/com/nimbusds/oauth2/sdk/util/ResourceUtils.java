/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import java.net.URI;

public final class ResourceUtils {
    public static boolean isValidResourceURI(URI resourceURI) {
        return resourceURI.getHost() != null && resourceURI.getQuery() == null && resourceURI.getFragment() == null;
    }

    private ResourceUtils() {
    }
}

