/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.rp.statement;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import com.nimbusds.openid.connect.sdk.rp.statement.InvalidSoftwareStatementException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;
import net.minidev.json.JSONObject;

@ThreadSafe
public class SoftwareStatementProcessor<C extends SecurityContext> {
    private final boolean required;
    private final DefaultJWTProcessor<C> processor;

    public SoftwareStatementProcessor(Issuer issuer, boolean required, Set<JWSAlgorithm> jwsAlgs, JWKSet jwkSet) {
        this(issuer, required, jwsAlgs, new ImmutableJWKSet(jwkSet));
    }

    public SoftwareStatementProcessor(Issuer issuer, boolean required, Set<JWSAlgorithm> jwsAlgs, URL jwkSetURL, int connectTimeoutMs, int readTimeoutMs, int sizeLimitBytes) {
        this(issuer, required, jwsAlgs, new RemoteJWKSet(jwkSetURL, new DefaultResourceRetriever(connectTimeoutMs, readTimeoutMs, sizeLimitBytes)));
    }

    public SoftwareStatementProcessor(Issuer issuer, boolean required, Set<JWSAlgorithm> jwsAlgs, JWKSource<C> jwkSource) {
        this(issuer, required, jwsAlgs, jwkSource, Collections.emptySet());
    }

    public SoftwareStatementProcessor(Issuer issuer, boolean required, Set<JWSAlgorithm> jwsAlgs, JWKSource<C> jwkSource, Set<String> additionalRequiredClaims) {
        this.required = required;
        HashSet<String> allRequiredClaims = new HashSet<String>();
        allRequiredClaims.add("iss");
        if (CollectionUtils.isNotEmpty(additionalRequiredClaims)) {
            allRequiredClaims.addAll(additionalRequiredClaims);
        }
        this.processor = new DefaultJWTProcessor();
        this.processor.setJWSKeySelector(new JWSVerificationKeySelector<C>(jwsAlgs, jwkSource));
        this.processor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier(new JWTClaimsSet.Builder().issuer(issuer.getValue()).build(), allRequiredClaims));
    }

    public OIDCClientMetadata process(OIDCClientMetadata clientMetadata) throws InvalidSoftwareStatementException, JOSEException {
        return this.process(clientMetadata, null);
    }

    public OIDCClientMetadata process(OIDCClientMetadata clientMetadata, C context) throws InvalidSoftwareStatementException, JOSEException {
        JWTClaimsSet statementClaims;
        SignedJWT softwareStatement = clientMetadata.getSoftwareStatement();
        if (softwareStatement == null) {
            if (this.required) {
                throw new InvalidSoftwareStatementException("Missing required software statement");
            }
            return clientMetadata;
        }
        try {
            statementClaims = this.processor.process(softwareStatement, context);
        }
        catch (BadJOSEException e) {
            throw new InvalidSoftwareStatementException("Invalid software statement JWT: " + e.getMessage(), e);
        }
        catch (RemoteKeySourceException e) {
            throw new InvalidSoftwareStatementException("Software statement JWT validation failed: " + e.getMessage(), e);
        }
        JSONObject mergedMetadataJSONObject = new JSONObject();
        mergedMetadataJSONObject.putAll(clientMetadata.toJSONObject());
        mergedMetadataJSONObject.remove("software_statement");
        JSONObject statementJSONObject = statementClaims.toJSONObject();
        statementJSONObject.remove("iss");
        mergedMetadataJSONObject.putAll(statementJSONObject);
        try {
            return OIDCClientMetadata.parse(mergedMetadataJSONObject);
        }
        catch (ParseException e) {
            throw new InvalidSoftwareStatementException("Error merging software statement: " + e.getMessage(), e);
        }
    }
}

