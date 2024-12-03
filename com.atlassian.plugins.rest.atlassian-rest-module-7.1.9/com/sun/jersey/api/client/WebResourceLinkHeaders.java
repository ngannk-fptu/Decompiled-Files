/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ViewResource;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.LinkHeader;
import com.sun.jersey.core.header.LinkHeaders;
import javax.ws.rs.core.MultivaluedMap;

public class WebResourceLinkHeaders
extends LinkHeaders {
    private final Client c;

    public WebResourceLinkHeaders(Client c, MultivaluedMap<String, String> headers) {
        super(headers);
        this.c = c;
    }

    public WebResource resource(String rel) {
        LinkHeader lh = this.getLink(rel);
        if (lh == null) {
            return null;
        }
        return this.c.resource(lh.getUri());
    }

    public ViewResource viewResource(String rel) {
        LinkHeader lh = this.getLink(rel);
        if (lh == null) {
            return null;
        }
        return this.c.viewResource(lh.getUri());
    }
}

