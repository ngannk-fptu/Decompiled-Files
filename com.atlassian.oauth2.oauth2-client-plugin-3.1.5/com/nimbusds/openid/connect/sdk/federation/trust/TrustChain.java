/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import com.nimbusds.openid.connect.sdk.federation.entities.FederationMetadataType;
import com.nimbusds.openid.connect.sdk.federation.policy.MetadataPolicy;
import com.nimbusds.openid.connect.sdk.federation.policy.MetadataPolicyEntry;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.PolicyOperationCombinationValidator;
import java.security.ProviderException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.jcip.annotations.Immutable;

@Immutable
public final class TrustChain {
    private final EntityStatement leaf;
    private final List<EntityStatement> superiors;
    private Date exp;

    public TrustChain(EntityStatement leaf, List<EntityStatement> superiors) {
        if (leaf == null) {
            throw new IllegalArgumentException("The leaf statement must not be null");
        }
        this.leaf = leaf;
        if (CollectionUtils.isEmpty(superiors)) {
            throw new IllegalArgumentException("There must be at least one superior statement (issued by the trust anchor)");
        }
        this.superiors = superiors;
        if (!TrustChain.hasValidIssuerSubjectChain(leaf, superiors)) {
            throw new IllegalArgumentException("Broken subject - issuer chain");
        }
    }

    private static boolean hasValidIssuerSubjectChain(EntityStatement leaf, List<EntityStatement> superiors) {
        Subject nextExpectedSubject = leaf.getClaimsSet().getSubject();
        for (EntityStatement superiorStmt : superiors) {
            if (!nextExpectedSubject.equals(superiorStmt.getClaimsSet().getSubject())) {
                return false;
            }
            nextExpectedSubject = new Subject(superiorStmt.getClaimsSet().getIssuer().getValue());
        }
        return true;
    }

    public EntityStatement getLeafSelfStatement() {
        return this.leaf;
    }

    public List<EntityStatement> getSuperiorStatements() {
        return this.superiors;
    }

    public EntityID getTrustAnchorEntityID() {
        return this.getSuperiorStatements().get(this.getSuperiorStatements().size() - 1).getClaimsSet().getIssuerEntityID();
    }

    public int length() {
        return this.getSuperiorStatements().size();
    }

    public MetadataPolicy resolveCombinedMetadataPolicy(FederationMetadataType type) throws PolicyViolationException {
        return this.resolveCombinedMetadataPolicy(type, MetadataPolicyEntry.DEFAULT_POLICY_COMBINATION_VALIDATOR);
    }

    public MetadataPolicy resolveCombinedMetadataPolicy(FederationMetadataType type, PolicyOperationCombinationValidator combinationValidator) throws PolicyViolationException {
        LinkedList<MetadataPolicy> policies = new LinkedList<MetadataPolicy>();
        for (EntityStatement stmt : this.getSuperiorStatements()) {
            MetadataPolicy metadataPolicy = stmt.getClaimsSet().getMetadataPolicy(type);
            if (metadataPolicy == null) continue;
            policies.add(metadataPolicy);
        }
        return MetadataPolicy.combine(policies, combinationValidator);
    }

    public Iterator<EntityStatement> iteratorFromLeaf() {
        final AtomicReference<EntityStatement> next = new AtomicReference<EntityStatement>(this.getLeafSelfStatement());
        final Iterator<EntityStatement> superiorsIterator = this.getSuperiorStatements().iterator();
        return new Iterator<EntityStatement>(){

            @Override
            public boolean hasNext() {
                return next.get() != null;
            }

            @Override
            public EntityStatement next() {
                EntityStatement toReturn = (EntityStatement)next.get();
                if (toReturn == null) {
                    return null;
                }
                if (toReturn.equals(TrustChain.this.getLeafSelfStatement())) {
                    next.set(superiorsIterator.next());
                } else if (superiorsIterator.hasNext()) {
                    next.set(superiorsIterator.next());
                } else {
                    next.set(null);
                }
                return toReturn;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Date resolveExpirationTime() {
        if (this.exp != null) {
            return this.exp;
        }
        Iterator<EntityStatement> it = this.iteratorFromLeaf();
        Date nearestExp = null;
        while (it.hasNext()) {
            Date stmtExp = it.next().getClaimsSet().getExpirationTime();
            if (nearestExp == null) {
                nearestExp = stmtExp;
                continue;
            }
            if (!stmtExp.before(nearestExp)) continue;
            nearestExp = stmtExp;
        }
        this.exp = nearestExp;
        return this.exp;
    }

    public void verifySignatures(JWKSet trustAnchorJWKSet) throws BadJOSEException, JOSEException {
        Base64URL signingJWKThumbprint;
        try {
            signingJWKThumbprint = this.leaf.verifySignatureOfSelfStatement();
        }
        catch (BadJOSEException e) {
            throw new BadJOSEException("Invalid leaf statement: " + e.getMessage(), e);
        }
        for (int i = 0; i < this.superiors.size(); ++i) {
            EntityStatement stmt = this.superiors.get(i);
            JWKSet verificationJWKSet = i + 1 == this.superiors.size() ? trustAnchorJWKSet : this.superiors.get(i + 1).getClaimsSet().getJWKSet();
            if (!TrustChain.hasJWKWithThumbprint(stmt.getClaimsSet().getJWKSet(), signingJWKThumbprint)) {
                throw new BadJOSEException("Signing JWK with thumbprint " + signingJWKThumbprint + " not found in entity statement issued from superior " + stmt.getClaimsSet().getIssuerEntityID());
            }
            try {
                signingJWKThumbprint = stmt.verifySignature(verificationJWKSet);
                continue;
            }
            catch (BadJOSEException e) {
                throw new BadJOSEException("Invalid statement from " + stmt.getClaimsSet().getIssuer() + ": " + e.getMessage(), e);
            }
        }
    }

    private static boolean hasJWKWithThumbprint(JWKSet jwkSet, Base64URL thumbprint) {
        if (jwkSet == null) {
            return false;
        }
        for (JWK jwk : jwkSet.getKeys()) {
            try {
                if (!thumbprint.equals(jwk.computeThumbprint())) continue;
                return true;
            }
            catch (JOSEException e) {
                throw new ProviderException(e.getMessage(), e);
            }
        }
        return false;
    }
}

