/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import com.nimbusds.openid.connect.sdk.federation.entities.FederationEntityMetadata;
import com.nimbusds.openid.connect.sdk.federation.entities.FederationMetadataType;
import com.nimbusds.openid.connect.sdk.federation.trust.EntityMetadataValidator;
import com.nimbusds.openid.connect.sdk.federation.trust.EntityStatementRetriever;
import com.nimbusds.openid.connect.sdk.federation.trust.InvalidEntityMetadataException;
import com.nimbusds.openid.connect.sdk.federation.trust.ResolveException;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChain;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChainRetriever;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChainSet;
import com.nimbusds.openid.connect.sdk.federation.trust.constraints.TrustChainConstraints;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DefaultTrustChainRetriever
implements TrustChainRetriever {
    private final EntityStatementRetriever retriever;
    private final TrustChainConstraints constraints;
    private final List<Throwable> accumulatedExceptions = new LinkedList<Throwable>();
    private final Map<EntityID, JWKSet> accumulatedTrustAnchorJWKSets = new HashMap<EntityID, JWKSet>();

    DefaultTrustChainRetriever(EntityStatementRetriever retriever) {
        this(retriever, TrustChainConstraints.NO_CONSTRAINTS);
    }

    DefaultTrustChainRetriever(EntityStatementRetriever retriever, TrustChainConstraints constraints) {
        if (retriever == null) {
            throw new IllegalArgumentException("The entity statement retriever must not be null");
        }
        this.retriever = retriever;
        if (constraints == null) {
            throw new IllegalArgumentException("The trust chain constraints must not be null");
        }
        this.constraints = constraints;
    }

    public TrustChainConstraints getConstraints() {
        return this.constraints;
    }

    @Override
    public TrustChainSet retrieve(EntityID target, EntityMetadataValidator targetMetadataValidator, Set<EntityID> trustAnchors) throws InvalidEntityMetadataException {
        EntityStatement targetStatement;
        if (CollectionUtils.isEmpty(trustAnchors)) {
            throw new IllegalArgumentException("The trust anchors must not be empty");
        }
        this.accumulatedExceptions.clear();
        this.accumulatedTrustAnchorJWKSets.clear();
        try {
            targetStatement = this.retriever.fetchSelfIssuedEntityStatement(target);
        }
        catch (ResolveException e) {
            this.accumulatedExceptions.add(e);
            return new TrustChainSet();
        }
        if (targetMetadataValidator != null) {
            FederationMetadataType type = targetMetadataValidator.getType();
            if (type == null) {
                throw new IllegalArgumentException("The target metadata validation doesn't specify a federation entity type");
            }
            targetMetadataValidator.validate(target, targetStatement.getClaimsSet().getMetadata(type));
        }
        return this.retrieve(targetStatement, trustAnchors);
    }

    @Override
    public TrustChainSet retrieve(EntityStatement targetStatement, Set<EntityID> trustAnchors) {
        EntityID subject;
        if (CollectionUtils.isEmpty(trustAnchors)) {
            throw new IllegalArgumentException("The trust anchors must not be empty");
        }
        this.accumulatedExceptions.clear();
        this.accumulatedTrustAnchorJWKSets.clear();
        List<EntityID> authorityHints = targetStatement.getClaimsSet().getAuthorityHints();
        if (CollectionUtils.isEmpty(authorityHints)) {
            this.accumulatedExceptions.add(new ResolveException("Entity " + targetStatement.getEntityID() + " has no authorities listed (authority_hints)"));
            return new TrustChainSet();
        }
        try {
            subject = EntityID.parse(targetStatement.getClaimsSet().getSubject());
        }
        catch (ParseException e) {
            this.accumulatedExceptions.add(new ResolveException("Entity " + targetStatement.getEntityID() + " subject is illegal: " + e.getMessage(), e));
            return new TrustChainSet();
        }
        Set<List<EntityStatement>> anchoredChains = this.fetchStatementsFromAuthorities(subject, authorityHints, trustAnchors, Collections.emptyList());
        TrustChainSet trustChains = new TrustChainSet();
        for (List<EntityStatement> chain : anchoredChains) {
            trustChains.add(new TrustChain(targetStatement, chain));
        }
        return trustChains;
    }

    private Set<List<EntityStatement>> fetchStatementsFromAuthorities(EntityID subject, List<EntityID> authorities, Set<EntityID> trustAnchors, List<EntityStatement> partialChain) {
        EntityStatement last;
        HashSet<List<EntityStatement>> updatedChains = new HashSet<List<EntityStatement>>();
        HashMap<EntityID, List<EntityID>> nextLevelAuthorityHints = new HashMap<EntityID, List<EntityID>>();
        for (EntityID authority : authorities) {
            EntityStatement entityStatement;
            FederationEntityMetadata federationEntityMetadata;
            EntityStatement superiorSelfStmt;
            if (authority == null) continue;
            if (!this.constraints.isPermitted(partialChain.size())) {
                this.accumulatedExceptions.add(new ResolveException("Reached max number of intermediates in chain at " + subject));
                continue;
            }
            if (!this.constraints.isPermitted(authority)) {
                this.accumulatedExceptions.add(new ResolveException("Reached authority which isn't permitted according to constraints: " + authority));
                continue;
            }
            try {
                superiorSelfStmt = this.retriever.fetchSelfIssuedEntityStatement(authority);
                nextLevelAuthorityHints.put(authority, superiorSelfStmt.getClaimsSet().getAuthorityHints());
            }
            catch (ResolveException resolveException) {
                this.accumulatedExceptions.add(new ResolveException("Couldn't fetch self-issued entity statement from " + authority + ": " + resolveException.getMessage(), resolveException));
                continue;
            }
            if (trustAnchors.contains(superiorSelfStmt.getEntityID())) {
                this.accumulatedTrustAnchorJWKSets.put(superiorSelfStmt.getEntityID(), superiorSelfStmt.getClaimsSet().getJWKSet());
            }
            if ((federationEntityMetadata = superiorSelfStmt.getClaimsSet().getFederationEntityMetadata()) == null) {
                this.accumulatedExceptions.add(new ResolveException("No federation entity metadata for " + authority));
                continue;
            }
            URI federationAPIURI = federationEntityMetadata.getFederationAPIEndpointURI();
            if (federationAPIURI == null) {
                this.accumulatedExceptions.add(new ResolveException("No federation API URI in metadata for " + authority));
                continue;
            }
            try {
                entityStatement = this.retriever.fetchEntityStatement(federationAPIURI, authority, subject);
            }
            catch (ResolveException e) {
                this.accumulatedExceptions.add(new ResolveException("Couldn't fetch entity statement from " + federationAPIURI + ": " + e.getMessage(), e));
                continue;
            }
            LinkedList<EntityStatement> updatedChain = new LinkedList<EntityStatement>(partialChain);
            updatedChain.add(entityStatement);
            updatedChains.add(Collections.unmodifiableList(updatedChain));
        }
        LinkedHashSet<List<EntityStatement>> anchoredChains = new LinkedHashSet<List<EntityStatement>>();
        LinkedHashSet<List> remainingPartialChains = new LinkedHashSet<List>();
        for (List list : updatedChains) {
            last = (EntityStatement)list.get(list.size() - 1);
            if (trustAnchors.contains(last.getClaimsSet().getIssuerEntityID())) {
                anchoredChains.add(list);
                continue;
            }
            if (CollectionUtils.isEmpty(last.getClaimsSet().getAuthorityHints())) continue;
            remainingPartialChains.add(list);
        }
        for (List list : remainingPartialChains) {
            last = (EntityStatement)list.get(list.size() - 1);
            List nextAuthorities = (List)nextLevelAuthorityHints.get(last.getClaimsSet().getIssuerEntityID());
            if (CollectionUtils.isEmpty(nextAuthorities)) continue;
            anchoredChains.addAll(this.fetchStatementsFromAuthorities(last.getClaimsSet().getIssuerEntityID(), nextAuthorities, trustAnchors, list));
        }
        return anchoredChains;
    }

    @Override
    public Map<EntityID, JWKSet> getAccumulatedTrustAnchorJWKSets() {
        return this.accumulatedTrustAnchorJWKSets;
    }

    @Override
    public List<Throwable> getAccumulatedExceptions() {
        return this.accumulatedExceptions;
    }
}

