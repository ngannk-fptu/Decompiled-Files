/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.IOUtils;
import java.io.Closeable;
import java.util.List;
import java.util.Objects;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JWKSourceWithFailover<C extends SecurityContext>
implements JWKSource<C>,
Closeable {
    private final JWKSource<C> jwkSource;
    private final JWKSource<C> failoverJWKSource;

    public JWKSourceWithFailover(JWKSource<C> jwkSource, JWKSource<C> failoverJWKSource) {
        Objects.requireNonNull(jwkSource, "The primary JWK source must not be null");
        this.jwkSource = jwkSource;
        this.failoverJWKSource = failoverJWKSource;
    }

    private List<JWK> failover(Exception exception, JWKSelector jwkSelector, C context) throws KeySourceException {
        try {
            return this.failoverJWKSource.get(jwkSelector, context);
        }
        catch (KeySourceException kse) {
            throw new KeySourceException(exception.getMessage() + "; Failover JWK source retrieval failed with: " + kse.getMessage(), kse);
        }
    }

    @Override
    public List<JWK> get(JWKSelector jwkSelector, C context) throws KeySourceException {
        try {
            return this.jwkSource.get(jwkSelector, context);
        }
        catch (Exception e) {
            return this.failover(e, jwkSelector, context);
        }
    }

    @Override
    public void close() {
        if (this.jwkSource instanceof Closeable) {
            IOUtils.closeSilently((Closeable)((Object)this.jwkSource));
        }
        if (this.failoverJWKSource instanceof Closeable) {
            IOUtils.closeSilently((Closeable)((Object)this.failoverJWKSource));
        }
    }
}

