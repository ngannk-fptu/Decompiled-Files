/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.sourcemodel;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.policy.spi.PolicyAssertionCreator;
import java.util.Collection;

class DefaultPolicyAssertionCreator
implements PolicyAssertionCreator {
    DefaultPolicyAssertionCreator() {
    }

    @Override
    public String[] getSupportedDomainNamespaceURIs() {
        return null;
    }

    @Override
    public PolicyAssertion createAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative, PolicyAssertionCreator defaultCreator) throws AssertionCreationException {
        return new DefaultPolicyAssertion(data, assertionParameters, nestedAlternative);
    }

    private static final class DefaultPolicyAssertion
    extends PolicyAssertion {
        DefaultPolicyAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
            super(data, assertionParameters, nestedAlternative);
        }
    }
}

