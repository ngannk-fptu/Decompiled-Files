/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.DefaultJWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class RemoteJWKSet<C extends SecurityContext>
implements JWKSource<C> {
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 500;
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 500;
    public static final int DEFAULT_HTTP_SIZE_LIMIT = 51200;
    private final URL jwkSetURL;
    private final JWKSetCache jwkSetCache;
    private final ResourceRetriever jwkSetRetriever;

    public RemoteJWKSet(URL jwkSetURL) {
        this(jwkSetURL, null);
    }

    public RemoteJWKSet(URL jwkSetURL, ResourceRetriever resourceRetriever) {
        this(jwkSetURL, resourceRetriever, null);
    }

    public RemoteJWKSet(URL jwkSetURL, ResourceRetriever resourceRetriever, JWKSetCache jwkSetCache) {
        if (jwkSetURL == null) {
            throw new IllegalArgumentException("The JWK set URL must not be null");
        }
        this.jwkSetURL = jwkSetURL;
        this.jwkSetRetriever = resourceRetriever != null ? resourceRetriever : new DefaultResourceRetriever(500, 500, 51200);
        this.jwkSetCache = jwkSetCache != null ? jwkSetCache : new DefaultJWKSetCache();
    }

    private JWKSet updateJWKSetFromURL() throws RemoteKeySourceException {
        JWKSet jwkSet;
        Resource res;
        try {
            res = this.jwkSetRetriever.retrieveResource(this.jwkSetURL);
        }
        catch (IOException e) {
            throw new RemoteKeySourceException("Couldn't retrieve remote JWK set: " + e.getMessage(), e);
        }
        try {
            jwkSet = JWKSet.parse(res.getContent());
        }
        catch (ParseException e) {
            throw new RemoteKeySourceException("Couldn't parse remote JWK set: " + e.getMessage(), e);
        }
        this.jwkSetCache.put(jwkSet);
        return jwkSet;
    }

    public URL getJWKSetURL() {
        return this.jwkSetURL;
    }

    public ResourceRetriever getResourceRetriever() {
        return this.jwkSetRetriever;
    }

    public JWKSetCache getJWKSetCache() {
        return this.jwkSetCache;
    }

    public JWKSet getCachedJWKSet() {
        return this.jwkSetCache.get();
    }

    protected static String getFirstSpecifiedKeyID(JWKMatcher jwkMatcher) {
        Set<String> keyIDs = jwkMatcher.getKeyIDs();
        if (keyIDs == null || keyIDs.isEmpty()) {
            return null;
        }
        for (String id : keyIDs) {
            if (id == null) continue;
            return id;
        }
        return null;
    }

    @Override
    public List<JWK> get(JWKSelector jwkSelector, C context) throws RemoteKeySourceException {
        List<JWK> matches;
        JWKSet jwkSet;
        block7: {
            jwkSet = this.jwkSetCache.get();
            if (this.jwkSetCache.requiresRefresh() || jwkSet == null) {
                try {
                    jwkSet = this.updateJWKSetFromURL();
                }
                catch (Exception ex) {
                    if (jwkSet != null) break block7;
                    throw ex;
                }
            }
        }
        if (!(matches = jwkSelector.select(jwkSet)).isEmpty()) {
            return matches;
        }
        String soughtKeyID = RemoteJWKSet.getFirstSpecifiedKeyID(jwkSelector.getMatcher());
        if (soughtKeyID == null) {
            return Collections.emptyList();
        }
        if (jwkSet.getKeyByKeyId(soughtKeyID) != null) {
            return Collections.emptyList();
        }
        jwkSet = this.updateJWKSetFromURL();
        if (jwkSet == null) {
            return Collections.emptyList();
        }
        return jwkSelector.select(jwkSet);
    }
}

