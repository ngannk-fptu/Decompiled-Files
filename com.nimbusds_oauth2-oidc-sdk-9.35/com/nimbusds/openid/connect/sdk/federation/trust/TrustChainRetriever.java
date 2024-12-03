/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.jwk.JWKSet
 *  net.jcip.annotations.NotThreadSafe
 */
package com.nimbusds.openid.connect.sdk.federation.trust;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import com.nimbusds.openid.connect.sdk.federation.trust.EntityMetadataValidator;
import com.nimbusds.openid.connect.sdk.federation.trust.InvalidEntityMetadataException;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChainSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
interface TrustChainRetriever {
    public TrustChainSet retrieve(EntityID var1, EntityMetadataValidator var2, Set<EntityID> var3) throws InvalidEntityMetadataException;

    public TrustChainSet retrieve(EntityStatement var1, Set<EntityID> var2);

    public Map<EntityID, JWKSet> getAccumulatedTrustAnchorJWKSets();

    public List<Throwable> getAccumulatedExceptions();
}

