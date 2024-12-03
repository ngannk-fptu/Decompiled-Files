/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.id;

import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.util.JSONArrayUtils;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

public class SectorIDURIValidator {
    private final ResourceRetriever resourceRetriever;

    public SectorIDURIValidator(ResourceRetriever resourceRetriever) {
        if (resourceRetriever == null) {
            throw new IllegalArgumentException("The resource retriever must not be null");
        }
        this.resourceRetriever = resourceRetriever;
    }

    public ResourceRetriever getResourceRetriever() {
        return this.resourceRetriever;
    }

    public void validate(URI sectorURI, Set<URI> redirectURIs) throws GeneralException {
        Resource resource;
        try {
            resource = this.resourceRetriever.retrieveResource(sectorURI.toURL());
        }
        catch (IOException e) {
            throw new GeneralException("Couldn't retrieve the sector ID JSON document: " + e.getMessage(), e);
        }
        if (resource.getContentType() == null) {
            throw new GeneralException("Couldn't validate sector ID URI: Missing Content-Type");
        }
        if (!resource.getContentType().toLowerCase().startsWith("application/json")) {
            throw new GeneralException("Couldn't validate sector ID URI: Content-Type must be application/json, found " + resource.getContentType());
        }
        List<URI> uriList = JSONArrayUtils.toURIList(JSONArrayUtils.parse(resource.getContent()));
        for (URI uri : redirectURIs) {
            if (uriList.contains(uri)) continue;
            throw new GeneralException("Sector ID URI validation failed: Redirect URI " + uri + " is missing from published JSON array at sector ID URI " + sectorURI);
        }
    }
}

