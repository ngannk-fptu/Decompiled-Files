/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.dpop;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.dpop.DPoPJWTFactory;
import com.nimbusds.oauth2.sdk.dpop.DPoPUtils;
import com.nimbusds.oauth2.sdk.id.JWTID;
import java.net.URI;
import java.security.Provider;
import java.util.Date;

public class DefaultDPoPJWTFactory
implements DPoPJWTFactory {
    private final JWK publicJWK;
    private final JWSAlgorithm jwsAlg;
    private final JWSSigner jwsSigner;

    public DefaultDPoPJWTFactory(JWK jwk, JWSAlgorithm jwsAlg) throws JOSEException {
        this(jwk, jwsAlg, null);
    }

    public DefaultDPoPJWTFactory(JWK jwk, JWSAlgorithm jwsAlg, Provider jcaProvider) throws JOSEException {
        if (!jwk.isPrivate()) {
            throw new IllegalArgumentException("The JWK must include private parameters");
        }
        if (!JWSAlgorithm.Family.SIGNATURE.contains(jwsAlg)) {
            throw new IllegalArgumentException("The JWS algorithm must be for a digital signature");
        }
        this.jwsAlg = jwsAlg;
        DefaultJWSSignerFactory factory = new DefaultJWSSignerFactory();
        if (jcaProvider != null) {
            factory.getJCAContext().setProvider(jcaProvider);
        }
        this.jwsSigner = factory.createJWSSigner(jwk, jwsAlg);
        this.publicJWK = jwk.toPublicJWK();
    }

    public JWK getPublicJWK() {
        return this.publicJWK;
    }

    public JWSAlgorithm getJWSAlgorithm() {
        return this.jwsAlg;
    }

    public JWSSigner getJWSSigner() {
        return this.jwsSigner;
    }

    @Override
    public SignedJWT createDPoPJWT(String htm, URI htu) throws JOSEException {
        return this.createDPoPJWT(new JWTID(12), htm, htu, new Date());
    }

    @Override
    public SignedJWT createDPoPJWT(JWTID jti, String htm, URI htu, Date iat) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader.Builder(this.getJWSAlgorithm()).type(TYPE).jwk(this.getPublicJWK()).build();
        JWTClaimsSet jwtClaimsSet = DPoPUtils.createJWTClaimsSet(jti, htm, htu, iat);
        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
        signedJWT.sign(this.getJWSSigner());
        return signedJWT;
    }
}

