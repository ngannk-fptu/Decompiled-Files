/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public final class PolicyIntersector {
    private static final PolicyIntersector STRICT_INTERSECTOR = new PolicyIntersector(CompatibilityMode.STRICT);
    private static final PolicyIntersector LAX_INTERSECTOR = new PolicyIntersector(CompatibilityMode.LAX);
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyIntersector.class);
    private CompatibilityMode mode;

    private PolicyIntersector(CompatibilityMode intersectionMode) {
        this.mode = intersectionMode;
    }

    public static PolicyIntersector createStrictPolicyIntersector() {
        return STRICT_INTERSECTOR;
    }

    public static PolicyIntersector createLaxPolicyIntersector() {
        return LAX_INTERSECTOR;
    }

    public Policy intersect(Policy ... policies) {
        if (policies == null || policies.length == 0) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0056_NEITHER_NULL_NOR_EMPTY_POLICY_COLLECTION_EXPECTED()));
        }
        if (policies.length == 1) {
            return policies[0];
        }
        boolean found = false;
        boolean allPoliciesEmpty = true;
        NamespaceVersion latestVersion = null;
        for (Policy tested : policies) {
            if (tested.isEmpty()) {
                found = true;
            } else {
                if (tested.isNull()) {
                    found = true;
                }
                allPoliciesEmpty = false;
            }
            if (latestVersion == null) {
                latestVersion = tested.getNamespaceVersion();
            } else if (latestVersion.compareTo(tested.getNamespaceVersion()) < 0) {
                latestVersion = tested.getNamespaceVersion();
            }
            if (!found || allPoliciesEmpty) continue;
            return Policy.createNullPolicy(latestVersion, null, null);
        }
        NamespaceVersion namespaceVersion = latestVersion = latestVersion != null ? latestVersion : NamespaceVersion.getLatestVersion();
        if (allPoliciesEmpty) {
            return Policy.createEmptyPolicy(latestVersion, null, null);
        }
        LinkedList<AssertionSet> finalAlternatives = new LinkedList<AssertionSet>(policies[0].getContent());
        LinkedList<AssertionSet> testedAlternatives = new LinkedList<AssertionSet>();
        ArrayList<AssertionSet> alternativesToMerge = new ArrayList<AssertionSet>(2);
        for (int i = 1; i < policies.length; ++i) {
            AssertionSet testedAlternative;
            Collection<AssertionSet> currentAlternatives = policies[i].getContent();
            testedAlternatives.clear();
            testedAlternatives.addAll(finalAlternatives);
            finalAlternatives.clear();
            while ((testedAlternative = (AssertionSet)testedAlternatives.poll()) != null) {
                for (AssertionSet currentAlternative : currentAlternatives) {
                    if (!testedAlternative.isCompatibleWith(currentAlternative, this.mode)) continue;
                    alternativesToMerge.add(testedAlternative);
                    alternativesToMerge.add(currentAlternative);
                    finalAlternatives.add(AssertionSet.createMergedAssertionSet(alternativesToMerge));
                    alternativesToMerge.clear();
                }
            }
        }
        return Policy.createPolicy(latestVersion, null, null, finalAlternatives);
    }

    static enum CompatibilityMode {
        STRICT,
        LAX;

    }
}

