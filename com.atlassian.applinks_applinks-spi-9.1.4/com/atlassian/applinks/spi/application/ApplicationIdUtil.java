/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 */
package com.atlassian.applinks.spi.application;

import com.atlassian.applinks.api.ApplicationId;
import java.net.URI;
import java.util.UUID;

public class ApplicationIdUtil {
    public static ApplicationId generate(URI baseUri) {
        String normalisedUri = baseUri.normalize().toASCIIString();
        while (normalisedUri.endsWith("/") && normalisedUri.length() > 1) {
            normalisedUri = normalisedUri.substring(0, normalisedUri.length() - 1);
        }
        String idString = UUID.nameUUIDFromBytes(normalisedUri.getBytes()).toString();
        return new ApplicationId(idString);
    }
}

