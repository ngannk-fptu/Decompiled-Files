/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.spi.HeaderDelegateProvider;
import java.net.URI;
import java.net.URISyntaxException;

public class URIProvider
implements HeaderDelegateProvider<URI> {
    @Override
    public boolean supports(Class<?> type) {
        return type == URI.class;
    }

    public String toString(URI header) {
        return header.toASCIIString();
    }

    public URI fromString(String header) {
        try {
            return new URI(header);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Error parsing uri '" + header + "'", e);
        }
    }
}

