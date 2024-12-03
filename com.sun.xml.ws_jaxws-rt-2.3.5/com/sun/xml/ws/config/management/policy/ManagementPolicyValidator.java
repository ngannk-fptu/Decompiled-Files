/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.PolicyAssertion
 *  com.sun.xml.ws.policy.spi.PolicyAssertionValidator
 *  com.sun.xml.ws.policy.spi.PolicyAssertionValidator$Fitness
 */
package com.sun.xml.ws.config.management.policy;

import com.sun.xml.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import javax.xml.namespace.QName;

public class ManagementPolicyValidator
implements PolicyAssertionValidator {
    public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion assertion) {
        QName assertionName = assertion.getName();
        if (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(assertionName)) {
            return PolicyAssertionValidator.Fitness.SUPPORTED;
        }
        if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(assertionName)) {
            return PolicyAssertionValidator.Fitness.UNSUPPORTED;
        }
        return PolicyAssertionValidator.Fitness.UNKNOWN;
    }

    public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion assertion) {
        QName assertionName = assertion.getName();
        if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(assertionName)) {
            return PolicyAssertionValidator.Fitness.SUPPORTED;
        }
        if (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(assertionName)) {
            return PolicyAssertionValidator.Fitness.UNSUPPORTED;
        }
        return PolicyAssertionValidator.Fitness.UNKNOWN;
    }

    public String[] declareSupportedDomains() {
        return new String[]{"http://java.sun.com/xml/ns/metro/management"};
    }
}

