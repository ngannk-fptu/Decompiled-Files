/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.policy;

import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.policy.jaxws.DefaultPolicyResolver;
import com.sun.xml.ws.util.ServiceFinder;

public abstract class PolicyResolverFactory {
    public static final PolicyResolver DEFAULT_POLICY_RESOLVER = new DefaultPolicyResolver();

    public abstract PolicyResolver doCreate();

    public static PolicyResolver create() {
        for (PolicyResolverFactory factory : ServiceFinder.find(PolicyResolverFactory.class)) {
            PolicyResolver policyResolver = factory.doCreate();
            if (policyResolver == null) continue;
            return policyResolver;
        }
        return DEFAULT_POLICY_RESOLVER;
    }
}

