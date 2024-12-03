/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.util.List;
import net.jcip.annotations.Immutable;

@Immutable
public class ImmutableJWKSet<C extends SecurityContext>
implements JWKSource<C> {
    private final JWKSet jwkSet;

    public ImmutableJWKSet(JWKSet jwkSet) {
        if (jwkSet == null) {
            throw new IllegalArgumentException("The JWK set must not be null");
        }
        this.jwkSet = jwkSet;
    }

    public JWKSet getJWKSet() {
        return this.jwkSet;
    }

    @Override
    public List<JWK> get(JWKSelector jwkSelector, C context) {
        return jwkSelector.select(this.jwkSet);
    }
}

