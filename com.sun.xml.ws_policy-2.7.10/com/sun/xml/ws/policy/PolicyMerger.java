/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public final class PolicyMerger {
    private static final PolicyMerger merger = new PolicyMerger();

    private PolicyMerger() {
    }

    public static PolicyMerger getMerger() {
        return merger;
    }

    public Policy merge(Collection<Policy> policies) {
        if (policies == null || policies.isEmpty()) {
            return null;
        }
        if (policies.size() == 1) {
            return policies.iterator().next();
        }
        LinkedList<Collection<AssertionSet>> alternativeSets = new LinkedList<Collection<AssertionSet>>();
        StringBuilder id = new StringBuilder();
        NamespaceVersion mergedVersion = policies.iterator().next().getNamespaceVersion();
        for (Policy policy : policies) {
            String policyId;
            alternativeSets.add(policy.getContent());
            if (mergedVersion.compareTo(policy.getNamespaceVersion()) < 0) {
                mergedVersion = policy.getNamespaceVersion();
            }
            if ((policyId = policy.getId()) == null) continue;
            if (id.length() > 0) {
                id.append('-');
            }
            id.append(policyId);
        }
        Collection combinedAlternatives = PolicyUtils.Collections.combine(null, alternativeSets, false);
        if (combinedAlternatives == null || combinedAlternatives.isEmpty()) {
            return Policy.createNullPolicy(mergedVersion, null, id.length() == 0 ? null : id.toString());
        }
        ArrayList<AssertionSet> mergedSetList = new ArrayList<AssertionSet>(combinedAlternatives.size());
        for (Collection<AssertionSet> collection : combinedAlternatives) {
            mergedSetList.add(AssertionSet.createMergedAssertionSet(collection));
        }
        return Policy.createPolicy(mergedVersion, null, id.length() == 0 ? null : id.toString(), mergedSetList);
    }
}

