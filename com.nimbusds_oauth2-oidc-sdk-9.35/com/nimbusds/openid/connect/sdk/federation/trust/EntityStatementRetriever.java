/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust;

import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import com.nimbusds.openid.connect.sdk.federation.trust.ResolveException;
import java.net.URI;

public interface EntityStatementRetriever {
    public EntityStatement fetchSelfIssuedEntityStatement(EntityID var1) throws ResolveException;

    public EntityStatement fetchEntityStatement(URI var1, EntityID var2, EntityID var3) throws ResolveException;
}

