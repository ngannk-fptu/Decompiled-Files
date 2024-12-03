/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapExtender
 *  com.sun.xml.ws.policy.PolicyMapMutator
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapExtender;
import com.sun.xml.ws.policy.PolicyMapMutator;
import com.sun.xml.ws.policy.jaxws.BuilderHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

class PolicyMapBuilder {
    private List<BuilderHandler> policyBuilders = new LinkedList<BuilderHandler>();

    PolicyMapBuilder() {
    }

    void registerHandler(BuilderHandler builder) {
        if (null != builder) {
            this.policyBuilders.add(builder);
        }
    }

    PolicyMap getPolicyMap(PolicyMapMutator ... externalMutators) throws PolicyException {
        return this.getNewPolicyMap(externalMutators);
    }

    private PolicyMap getNewPolicyMap(PolicyMapMutator ... externalMutators) throws PolicyException {
        HashSet<Object> mutators = new HashSet<Object>();
        PolicyMapExtender myExtender = PolicyMapExtender.createPolicyMapExtender();
        mutators.add(myExtender);
        if (null != externalMutators) {
            mutators.addAll(Arrays.asList(externalMutators));
        }
        PolicyMap policyMap = PolicyMap.createPolicyMap(mutators);
        for (BuilderHandler builder : this.policyBuilders) {
            builder.populate(myExtender);
        }
        return policyMap;
    }

    void unregisterAll() {
        this.policyBuilders = null;
    }
}

