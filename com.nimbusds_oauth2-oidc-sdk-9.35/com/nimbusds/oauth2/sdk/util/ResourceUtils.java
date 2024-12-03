/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ResourceUtils {
    @Deprecated
    public static boolean isValidResourceURI(URI resourceURI) {
        return ResourceUtils.isLegalResourceURI(resourceURI);
    }

    public static boolean isLegalResourceURI(URI resourceURI) {
        return resourceURI == null || resourceURI.isAbsolute() && resourceURI.getFragment() == null;
    }

    public static List<URI> ensureLegalResourceURIs(List<URI> resourceURIs) {
        if (CollectionUtils.isEmpty(resourceURIs)) {
            return resourceURIs;
        }
        for (URI resourceURI : resourceURIs) {
            if (resourceURI == null || ResourceUtils.isValidResourceURI(resourceURI)) continue;
            throw new IllegalArgumentException("Resource URI must be absolute and without a fragment: " + resourceURI);
        }
        return resourceURIs;
    }

    public static List<URI> parseResourceURIs(List<String> stringList) throws ParseException {
        if (CollectionUtils.isEmpty(stringList)) {
            return null;
        }
        LinkedList<URI> resources = new LinkedList<URI>();
        for (String uriValue : stringList) {
            URI resourceURI;
            if (uriValue == null) continue;
            String errMsg = "Illegal resource parameter: Must be an absolute URI and with no query or fragment";
            try {
                resourceURI = new URI(uriValue);
            }
            catch (URISyntaxException e) {
                throw new ParseException(errMsg);
            }
            if (!ResourceUtils.isLegalResourceURI(resourceURI)) {
                throw new ParseException(errMsg);
            }
            resources.add(resourceURI);
        }
        return Collections.unmodifiableList(resources);
    }

    private ResourceUtils() {
    }
}

