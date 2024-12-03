/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.jwk.JWKSet
 *  com.nimbusds.jose.proc.BadJOSEException
 */
package com.nimbusds.openid.connect.sdk.federation.trust;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.oauth2.sdk.util.MapUtils;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import com.nimbusds.openid.connect.sdk.federation.trust.DefaultEntityStatementRetriever;
import com.nimbusds.openid.connect.sdk.federation.trust.DefaultTrustChainRetriever;
import com.nimbusds.openid.connect.sdk.federation.trust.EntityMetadataValidator;
import com.nimbusds.openid.connect.sdk.federation.trust.EntityStatementRetriever;
import com.nimbusds.openid.connect.sdk.federation.trust.InvalidEntityMetadataException;
import com.nimbusds.openid.connect.sdk.federation.trust.ResolveException;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChain;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChainSet;
import com.nimbusds.openid.connect.sdk.federation.trust.constraints.TrustChainConstraints;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrustChainResolver {
    private final Map<EntityID, JWKSet> trustAnchors;
    private final EntityStatementRetriever statementRetriever;
    private final TrustChainConstraints constraints;

    public TrustChainResolver(EntityID trustAnchor) {
        this(trustAnchor, null);
    }

    public TrustChainResolver(EntityID trustAnchor, JWKSet trustAnchorJWKSet) {
        this(Collections.singletonMap(trustAnchor, trustAnchorJWKSet), TrustChainConstraints.NO_CONSTRAINTS, new DefaultEntityStatementRetriever());
    }

    public TrustChainResolver(Map<EntityID, JWKSet> trustAnchors, int httpConnectTimeoutMs, int httpReadTimeoutMs) {
        this(trustAnchors, TrustChainConstraints.NO_CONSTRAINTS, new DefaultEntityStatementRetriever(httpConnectTimeoutMs, httpReadTimeoutMs));
    }

    public TrustChainResolver(Map<EntityID, JWKSet> trustAnchors, TrustChainConstraints constraints, EntityStatementRetriever statementRetriever) {
        if (MapUtils.isEmpty(trustAnchors)) {
            throw new IllegalArgumentException("The trust anchors map must not be empty or null");
        }
        this.trustAnchors = trustAnchors;
        if (constraints == null) {
            throw new IllegalArgumentException("The trust chain constraints must not be null");
        }
        this.constraints = constraints;
        if (statementRetriever == null) {
            throw new IllegalArgumentException("The entity statement retriever must not be null");
        }
        this.statementRetriever = statementRetriever;
    }

    public Map<EntityID, JWKSet> getTrustAnchors() {
        return Collections.unmodifiableMap(this.trustAnchors);
    }

    public EntityStatementRetriever getEntityStatementRetriever() {
        return this.statementRetriever;
    }

    public TrustChainConstraints getConstraints() {
        return this.constraints;
    }

    public TrustChainSet resolveTrustChains(EntityID target) throws ResolveException {
        try {
            return this.resolveTrustChains(target, null);
        }
        catch (InvalidEntityMetadataException e) {
            throw new IllegalStateException("Unexpected exception: " + e.getMessage(), e);
        }
    }

    public TrustChainSet resolveTrustChains(EntityID target, EntityMetadataValidator targetMetadataValidator) throws ResolveException, InvalidEntityMetadataException {
        if (this.trustAnchors.get(target) != null) {
            throw new ResolveException("Target is trust anchor");
        }
        DefaultTrustChainRetriever retriever = new DefaultTrustChainRetriever(this.statementRetriever, this.constraints);
        TrustChainSet fetchedTrustChains = retriever.retrieve(target, targetMetadataValidator, this.trustAnchors.keySet());
        return this.verifyTrustChains(fetchedTrustChains, retriever.getAccumulatedTrustAnchorJWKSets(), retriever.getAccumulatedExceptions());
    }

    public TrustChainSet resolveTrustChains(EntityStatement targetStatement) throws ResolveException {
        if (this.trustAnchors.get(targetStatement.getEntityID()) != null) {
            throw new ResolveException("Target is trust anchor");
        }
        DefaultTrustChainRetriever retriever = new DefaultTrustChainRetriever(this.statementRetriever, this.constraints);
        TrustChainSet fetchedTrustChains = retriever.retrieve(targetStatement, this.trustAnchors.keySet());
        return this.verifyTrustChains(fetchedTrustChains, retriever.getAccumulatedTrustAnchorJWKSets(), retriever.getAccumulatedExceptions());
    }

    private TrustChainSet verifyTrustChains(Set<TrustChain> fetchedTrustChains, Map<EntityID, JWKSet> accumulatedTrustAnchorJWKSets, List<Throwable> accumulatedExceptions) throws ResolveException {
        if (fetchedTrustChains.isEmpty()) {
            if (accumulatedExceptions.isEmpty()) {
                throw new ResolveException("No trust chain leading up to a trust anchor");
            }
            if (accumulatedExceptions.size() == 1) {
                Throwable cause = accumulatedExceptions.get(0);
                throw new ResolveException("Couldn't resolve trust chain: " + cause.getMessage(), cause);
            }
            throw new ResolveException("Couldn't resolve trust chain due to multiple causes", accumulatedExceptions);
        }
        LinkedList<Throwable> verificationExceptions = new LinkedList<Throwable>();
        TrustChainSet verifiedTrustChains = new TrustChainSet();
        for (TrustChain chain : fetchedTrustChains) {
            EntityID anchor = chain.getTrustAnchorEntityID();
            JWKSet anchorJWKSet = this.trustAnchors.get(anchor);
            if (anchorJWKSet == null) {
                anchorJWKSet = accumulatedTrustAnchorJWKSets.get(anchor);
            }
            try {
                chain.verifySignatures(anchorJWKSet);
            }
            catch (JOSEException | BadJOSEException e) {
                verificationExceptions.add(e);
                continue;
            }
            verifiedTrustChains.add(chain);
        }
        if (verifiedTrustChains.isEmpty()) {
            LinkedList<Throwable> moreAccumulatedExceptions = new LinkedList<Throwable>(accumulatedExceptions);
            moreAccumulatedExceptions.addAll(verificationExceptions);
            if (verificationExceptions.size() == 1) {
                Throwable cause = (Throwable)verificationExceptions.get(0);
                throw new ResolveException("Couldn't resolve trust chain: " + cause.getMessage(), moreAccumulatedExceptions);
            }
            throw new ResolveException("Couldn't resolve trust chain due to multiple causes", moreAccumulatedExceptions);
        }
        return verifiedTrustChains;
    }
}

