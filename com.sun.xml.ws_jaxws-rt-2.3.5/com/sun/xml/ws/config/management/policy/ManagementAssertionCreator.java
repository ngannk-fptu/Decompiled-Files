/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.AssertionSet
 *  com.sun.xml.ws.policy.PolicyAssertion
 *  com.sun.xml.ws.policy.sourcemodel.AssertionData
 *  com.sun.xml.ws.policy.spi.AssertionCreationException
 *  com.sun.xml.ws.policy.spi.PolicyAssertionCreator
 */
package com.sun.xml.ws.config.management.policy;

import com.sun.xml.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.policy.spi.PolicyAssertionCreator;
import java.util.Collection;
import javax.xml.namespace.QName;

public class ManagementAssertionCreator
implements PolicyAssertionCreator {
    public String[] getSupportedDomainNamespaceURIs() {
        return new String[]{"http://java.sun.com/xml/ns/metro/management"};
    }

    public PolicyAssertion createAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative, PolicyAssertionCreator defaultCreator) throws AssertionCreationException {
        QName name = data.getName();
        if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(name)) {
            return new ManagedServiceAssertion(data, assertionParameters);
        }
        if (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(name)) {
            return new ManagedClientAssertion(data, assertionParameters);
        }
        return defaultCreator.createAssertion(data, assertionParameters, nestedAlternative, null);
    }
}

