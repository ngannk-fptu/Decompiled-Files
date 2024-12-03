/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSetCacheRefreshEvaluator;
import com.nimbusds.jose.jwk.source.JWKSetParseException;
import com.nimbusds.jose.jwk.source.JWKSetRetrievalException;
import com.nimbusds.jose.jwk.source.JWKSetSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class URLBasedJWKSetSource<C extends SecurityContext>
implements JWKSetSource<C> {
    private final URL url;
    private final ResourceRetriever resourceRetriever;

    public URLBasedJWKSetSource(URL url, ResourceRetriever resourceRetriever) {
        Objects.requireNonNull(url, "The URL must not be null");
        this.url = url;
        Objects.requireNonNull(resourceRetriever, "The resource retriever must not be null");
        this.resourceRetriever = resourceRetriever;
    }

    @Override
    public JWKSet getJWKSet(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
        Resource resource;
        try {
            resource = this.resourceRetriever.retrieveResource(this.url);
        }
        catch (IOException e) {
            throw new JWKSetRetrievalException("Couldn't retrieve JWK set from URL: " + e.getMessage(), e);
        }
        try {
            return JWKSet.parse(resource.getContent());
        }
        catch (Exception e) {
            throw new JWKSetParseException("Unable to parse JWK set", e);
        }
    }

    @Override
    public void close() throws IOException {
    }
}

