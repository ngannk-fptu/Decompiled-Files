/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.spi;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import java.util.Collection;

public interface PolicyAssertionCreator {
    public String[] getSupportedDomainNamespaceURIs();

    public PolicyAssertion createAssertion(AssertionData var1, Collection<PolicyAssertion> var2, AssertionSet var3, PolicyAssertionCreator var4) throws AssertionCreationException;
}

