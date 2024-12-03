/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import java.net.URI;

public final class ResourceUtils {
    public static boolean isValidResourceURI(URI resourceURI) {
        return resourceURI.isAbsolute() && resourceURI.getFragment() == null;
    }

    private ResourceUtils() {
    }
}

