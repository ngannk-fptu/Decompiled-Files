/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.RemoteKeySourceException
 *  com.nimbusds.jose.jwk.JWKSet
 *  com.nimbusds.jose.jwk.source.ImmutableJWKSet
 *  com.nimbusds.jose.jwk.source.JWKSource
 *  com.nimbusds.jose.jwk.source.RemoteJWKSet
 *  com.nimbusds.jose.proc.BadJOSEException
 *  com.nimbusds.jose.proc.JWSKeySelector
 *  com.nimbusds.jose.proc.JWSVerificationKeySelector
 *  com.nimbusds.jose.proc.SecurityContext
 *  com.nimbusds.jose.util.DefaultResourceRetriever
 *  com.nimbusds.jose.util.ResourceRetriever
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.JWTClaimsSet$Builder
 *  com.nimbusds.jwt.SignedJWT
 *  com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
 *  com.nimbusds.jwt.proc.DefaultJWTProcessor
 *  com.nimbusds.jwt.proc.JWTClaimsSetVerifier
 *  net.jcip.annotations.ThreadSafe
 *  net.minidev.json.JSONObject
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
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import com.nimbusds.openid.connect.sdk.rp.statement.InvalidSoftwareStatementException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;
import net.minidev.json.JSONObject;

@ThreadSafe
public class SoftwareStatementProcessor<C extends SecurityContext> {
    private final boolean required;
    private final DefaultJWTProcessor<C> processor;

    public SoftwareStatementProcessor(Issuer issuer, boolean required, Set<JWSAlgorithm> jwsAlgs, JWKSet jwkSet) {
        this(issuer, required, jwsAlgs, (JWKSource<C>)new ImmutableJWKSet(jwkSet));
    }

    public SoftwareStatementProcessor(Issuer issuer, boolean required, Set<JWSAlgorithm> jwsAlgs, URL jwkSetURL, int connectTimeoutMs, int readTimeoutMs, int sizeLimitBytes) {
        this(issuer, required, jwsAlgs, (JWKSource<C>)new RemoteJWKSet(jwkSetURL, (ResourceRetriever)new DefaultResourceRetriever(connectTimeoutMs, readTimeoutMs, sizeLimitBytes)));
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
        this.processor.setJWSKeySelector((JWSKeySelector)new JWSVerificationKeySelector(jwsAlgs, jwkSource));
        this.processor.setJWTClaimsSetVerifier((JWTClaimsSetVerifier)new DefaultJWTClaimsVerifier(new JWTClaimsSet.Builder().issuer(issuer.getValue()).build(), allRequiredClaims));
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
        mergedMetadataJSONObject.putAll((Map)clientMetadata.toJSONObject());
        mergedMetadataJSONObject.remove((Object)"software_statement");
        JSONObject statementJSONObject = JSONObjectUtils.toJSONObject(statementClaims);
        statementJSONObject.remove((Object)"iss");
        mergedMetadataJSONObject.putAll((Map)statementJSONObject);
        try {
            return OIDCClientMetadata.parse(mergedMetadataJSONObject);
        }
        catch (ParseException e) {
            throw new InvalidSoftwareStatementException("Error merging software statement: " + e.getMessage(), e);
        }
    }
}

