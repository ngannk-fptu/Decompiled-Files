/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.trust;

import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChain;
import com.nimbusds.openid.connect.sdk.federation.trust.constraints.TrustChainConstraints;
import java.util.HashSet;

public class TrustChainSet
extends HashSet<TrustChain> {
    public TrustChain getShortest() {
        TrustChain shortest = null;
        for (TrustChain chain : this) {
            if (chain.length() == 1) {
                return chain;
            }
            if (shortest == null) {
                shortest = chain;
                continue;
            }
            if (chain.length() >= shortest.length()) continue;
            shortest = chain;
        }
        return shortest;
    }

    public TrustChainSet filter(TrustChainConstraints constraints) {
        TrustChainSet permitted = new TrustChainSet();
        for (TrustChain chain : this) {
            if (constraints.getMaxPathLength() >= 0 && chain.length() - 1 > constraints.getMaxPathLength()) continue;
            boolean foundNonPermitted = false;
            for (EntityStatement stmt : chain.getSuperiorStatements()) {
                if (constraints.isPermitted(stmt.getClaimsSet().getIssuerEntityID())) continue;
                foundNonPermitted = true;
                break;
            }
            if (foundNonPermitted) continue;
            permitted.add(chain);
        }
        return permitted;
    }
}

