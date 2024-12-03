/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AbstractRequest;
import com.nimbusds.oauth2.sdk.WellKnownPathComposeStrategy;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import java.net.URI;

public abstract class AbstractConfigurationRequest
extends AbstractRequest {
    public AbstractConfigurationRequest(URI baseURI, String wellKnownPath, WellKnownPathComposeStrategy strategy) {
        super(WellKnownPathComposeStrategy.POSTFIX.equals((Object)strategy) ? URI.create(URIUtils.removeTrailingSlash(baseURI) + wellKnownPath) : URIUtils.prependPath(baseURI, wellKnownPath));
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        return new HTTPRequest(HTTPRequest.Method.GET, this.getEndpointURI());
    }
}

