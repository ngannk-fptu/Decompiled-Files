/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth.verifier;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.proc.JWSVerifierFactory;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretJWT;
import com.nimbusds.oauth2.sdk.auth.PKITLSClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.PlainClientSecret;
import com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.auth.SelfSignedTLSClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.verifier.ClientCredentialsSelector;
import com.nimbusds.oauth2.sdk.auth.verifier.ClientX509CertificateBindingVerifier;
import com.nimbusds.oauth2.sdk.auth.verifier.Context;
import com.nimbusds.oauth2.sdk.auth.verifier.Hint;
import com.nimbusds.oauth2.sdk.auth.verifier.InvalidClientException;
import com.nimbusds.oauth2.sdk.auth.verifier.JWTAuthenticationClaimsSetVerifier;
import com.nimbusds.oauth2.sdk.auth.verifier.PKIClientX509CertificateBindingVerifier;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.ListUtils;
import com.nimbusds.oauth2.sdk.util.X509CertificateUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ClientAuthenticationVerifier<T> {
    private final ClientCredentialsSelector<T> clientCredentialsSelector;
    @Deprecated
    private final ClientX509CertificateBindingVerifier<T> certBindingVerifier;
    private final PKIClientX509CertificateBindingVerifier<T> pkiCertBindingVerifier;
    private final JWTAuthenticationClaimsSetVerifier claimsSetVerifier;
    private final JWSVerifierFactory jwsVerifierFactory = new DefaultJWSVerifierFactory();

    @Deprecated
    public ClientAuthenticationVerifier(ClientCredentialsSelector<T> clientCredentialsSelector, ClientX509CertificateBindingVerifier<T> certBindingVerifier, Set<Audience> expectedAudience) {
        this.claimsSetVerifier = new JWTAuthenticationClaimsSetVerifier(expectedAudience);
        if (clientCredentialsSelector == null) {
            throw new IllegalArgumentException("The client credentials selector must not be null");
        }
        this.certBindingVerifier = certBindingVerifier;
        this.pkiCertBindingVerifier = null;
        this.clientCredentialsSelector = clientCredentialsSelector;
    }

    public ClientAuthenticationVerifier(ClientCredentialsSelector<T> clientCredentialsSelector, Set<Audience> expectedAudience) {
        this.claimsSetVerifier = new JWTAuthenticationClaimsSetVerifier(expectedAudience);
        if (clientCredentialsSelector == null) {
            throw new IllegalArgumentException("The client credentials selector must not be null");
        }
        this.certBindingVerifier = null;
        this.pkiCertBindingVerifier = null;
        this.clientCredentialsSelector = clientCredentialsSelector;
    }

    public ClientAuthenticationVerifier(ClientCredentialsSelector<T> clientCredentialsSelector, PKIClientX509CertificateBindingVerifier<T> pkiCertBindingVerifier, Set<Audience> expectedAudience) {
        this.claimsSetVerifier = new JWTAuthenticationClaimsSetVerifier(expectedAudience);
        if (clientCredentialsSelector == null) {
            throw new IllegalArgumentException("The client credentials selector must not be null");
        }
        this.certBindingVerifier = null;
        this.pkiCertBindingVerifier = pkiCertBindingVerifier;
        this.clientCredentialsSelector = clientCredentialsSelector;
    }

    public ClientCredentialsSelector<T> getClientCredentialsSelector() {
        return this.clientCredentialsSelector;
    }

    @Deprecated
    public ClientX509CertificateBindingVerifier<T> getClientX509CertificateBindingVerifier() {
        return this.certBindingVerifier;
    }

    public PKIClientX509CertificateBindingVerifier<T> getPKIClientX509CertificateBindingVerifier() {
        return this.pkiCertBindingVerifier;
    }

    public Set<Audience> getExpectedAudience() {
        return this.claimsSetVerifier.getExpectedAudience();
    }

    private static List<Secret> removeNullOrErased(List<Secret> secrets) {
        List<Secret> allSet = ListUtils.removeNullItems(secrets);
        if (allSet == null) {
            return null;
        }
        LinkedList<Secret> out = new LinkedList<Secret>();
        for (Secret secret : secrets) {
            if (secret.getValue() == null || secret.getValueBytes() == null) continue;
            out.add(secret);
        }
        return out;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void verify(ClientAuthentication clientAuth, Set<Hint> hints, Context<T> context) throws InvalidClientException, JOSEException {
        if (clientAuth instanceof PlainClientSecret) {
            List<Secret> secretCandidates = ListUtils.removeNullItems(this.clientCredentialsSelector.selectClientSecrets(clientAuth.getClientID(), clientAuth.getMethod(), context));
            if (CollectionUtils.isEmpty(secretCandidates)) {
                throw InvalidClientException.NO_REGISTERED_SECRET;
            }
            PlainClientSecret plainAuth = (PlainClientSecret)clientAuth;
            for (Secret candidate : secretCandidates) {
                if (!candidate.equals(plainAuth.getClientSecret())) continue;
                return;
            }
            throw InvalidClientException.BAD_SECRET;
        }
        if (clientAuth instanceof ClientSecretJWT) {
            ClientSecretJWT jwtAuth = (ClientSecretJWT)clientAuth;
            try {
                this.claimsSetVerifier.verify(jwtAuth.getJWTAuthenticationClaimsSet().toJWTClaimsSet());
            }
            catch (BadJWTException e) {
                throw new InvalidClientException("Bad / expired JWT claims: " + e.getMessage());
            }
            List<Secret> secretCandidates = ClientAuthenticationVerifier.removeNullOrErased(this.clientCredentialsSelector.selectClientSecrets(clientAuth.getClientID(), clientAuth.getMethod(), context));
            if (CollectionUtils.isEmpty(secretCandidates)) {
                throw InvalidClientException.NO_REGISTERED_SECRET;
            }
            SignedJWT assertion = jwtAuth.getClientAssertion();
            for (Secret candidate : secretCandidates) {
                boolean valid = assertion.verify(new MACVerifier(candidate.getValueBytes()));
                if (!valid) continue;
                return;
            }
            throw InvalidClientException.BAD_JWT_HMAC;
        }
        if (clientAuth instanceof PrivateKeyJWT) {
            boolean valid;
            JWSVerifier jwsVerifier;
            PrivateKeyJWT jwtAuth = (PrivateKeyJWT)clientAuth;
            try {
                this.claimsSetVerifier.verify(jwtAuth.getJWTAuthenticationClaimsSet().toJWTClaimsSet());
            }
            catch (BadJWTException e) {
                throw new InvalidClientException("Bad / expired JWT claims: " + e.getMessage());
            }
            List<PublicKey> keyCandidates = ListUtils.removeNullItems(this.clientCredentialsSelector.selectPublicKeys(jwtAuth.getClientID(), jwtAuth.getMethod(), jwtAuth.getClientAssertion().getHeader(), false, context));
            if (CollectionUtils.isEmpty(keyCandidates)) {
                throw InvalidClientException.NO_MATCHING_JWK;
            }
            SignedJWT assertion = jwtAuth.getClientAssertion();
            for (PublicKey candidate : keyCandidates) {
                jwsVerifier = this.jwsVerifierFactory.createJWSVerifier(jwtAuth.getClientAssertion().getHeader(), candidate);
                valid = assertion.verify(jwsVerifier);
                if (!valid) continue;
                return;
            }
            if (hints == null || !hints.contains((Object)Hint.CLIENT_HAS_REMOTE_JWK_SET)) throw InvalidClientException.BAD_JWT_SIGNATURE;
            keyCandidates = ListUtils.removeNullItems(this.clientCredentialsSelector.selectPublicKeys(jwtAuth.getClientID(), jwtAuth.getMethod(), jwtAuth.getClientAssertion().getHeader(), true, context));
            if (CollectionUtils.isEmpty(keyCandidates)) {
                throw InvalidClientException.NO_MATCHING_JWK;
            }
            assertion = jwtAuth.getClientAssertion();
            for (PublicKey candidate : keyCandidates) {
                jwsVerifier = this.jwsVerifierFactory.createJWSVerifier(jwtAuth.getClientAssertion().getHeader(), candidate);
                valid = assertion.verify(jwsVerifier);
                if (!valid) continue;
                return;
            }
            throw InvalidClientException.BAD_JWT_SIGNATURE;
        }
        if (clientAuth instanceof SelfSignedTLSClientAuthentication) {
            boolean valid;
            SelfSignedTLSClientAuthentication tlsClientAuth = (SelfSignedTLSClientAuthentication)clientAuth;
            X509Certificate clientCert = tlsClientAuth.getClientX509Certificate();
            if (clientCert == null) {
                throw new InvalidClientException("Missing client X.509 certificate");
            }
            List<PublicKey> keyCandidates = ListUtils.removeNullItems(this.clientCredentialsSelector.selectPublicKeys(tlsClientAuth.getClientID(), tlsClientAuth.getMethod(), null, false, context));
            if (CollectionUtils.isEmpty(keyCandidates)) {
                throw InvalidClientException.NO_MATCHING_JWK;
            }
            for (PublicKey candidate : keyCandidates) {
                valid = X509CertificateUtils.publicKeyMatches(clientCert, candidate);
                if (!valid) continue;
                return;
            }
            if (hints == null || !hints.contains((Object)Hint.CLIENT_HAS_REMOTE_JWK_SET)) throw InvalidClientException.BAD_SELF_SIGNED_CLIENT_CERTIFICATE;
            keyCandidates = ListUtils.removeNullItems(this.clientCredentialsSelector.selectPublicKeys(tlsClientAuth.getClientID(), tlsClientAuth.getMethod(), null, true, context));
            if (CollectionUtils.isEmpty(keyCandidates)) {
                throw InvalidClientException.NO_MATCHING_JWK;
            }
            for (PublicKey candidate : keyCandidates) {
                if (candidate == null || !(valid = X509CertificateUtils.publicKeyMatches(clientCert, candidate))) continue;
                return;
            }
            throw InvalidClientException.BAD_SELF_SIGNED_CLIENT_CERTIFICATE;
        }
        if (!(clientAuth instanceof PKITLSClientAuthentication)) throw new RuntimeException("Unexpected client authentication: " + clientAuth.getMethod());
        PKITLSClientAuthentication tlsClientAuth = (PKITLSClientAuthentication)clientAuth;
        if (this.pkiCertBindingVerifier != null) {
            this.pkiCertBindingVerifier.verifyCertificateBinding(clientAuth.getClientID(), tlsClientAuth.getClientX509Certificate(), context);
            return;
        } else {
            if (this.certBindingVerifier == null) throw new InvalidClientException("Mutual TLS client Authentication (tls_client_auth) not supported");
            this.certBindingVerifier.verifyCertificateBinding(clientAuth.getClientID(), tlsClientAuth.getClientX509CertificateSubjectDN(), context);
        }
    }
}

