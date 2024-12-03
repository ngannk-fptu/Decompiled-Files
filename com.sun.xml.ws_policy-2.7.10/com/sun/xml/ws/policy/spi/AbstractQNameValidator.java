/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.spi;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract class AbstractQNameValidator
implements PolicyAssertionValidator {
    private final Set<String> supportedDomains = new HashSet<String>();
    private final Collection<QName> serverAssertions;
    private final Collection<QName> clientAssertions;

    protected AbstractQNameValidator(Collection<QName> serverSideAssertions, Collection<QName> clientSideAssertions) {
        if (serverSideAssertions != null) {
            this.serverAssertions = new HashSet<QName>(serverSideAssertions);
            for (QName assertion : this.serverAssertions) {
                this.supportedDomains.add(assertion.getNamespaceURI());
            }
        } else {
            this.serverAssertions = new HashSet<QName>(0);
        }
        if (clientSideAssertions != null) {
            this.clientAssertions = new HashSet<QName>(clientSideAssertions);
            for (QName assertion : this.clientAssertions) {
                this.supportedDomains.add(assertion.getNamespaceURI());
            }
        } else {
            this.clientAssertions = new HashSet<QName>(0);
        }
    }

    @Override
    public String[] declareSupportedDomains() {
        return this.supportedDomains.toArray(new String[this.supportedDomains.size()]);
    }

    @Override
    public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion assertion) {
        return this.validateAssertion(assertion, this.clientAssertions, this.serverAssertions);
    }

    @Override
    public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion assertion) {
        return this.validateAssertion(assertion, this.serverAssertions, this.clientAssertions);
    }

    private PolicyAssertionValidator.Fitness validateAssertion(PolicyAssertion assertion, Collection<QName> thisSideAssertions, Collection<QName> otherSideAssertions) {
        QName assertionName = assertion.getName();
        if (thisSideAssertions.contains(assertionName)) {
            return PolicyAssertionValidator.Fitness.SUPPORTED;
        }
        if (otherSideAssertions.contains(assertionName)) {
            return PolicyAssertionValidator.Fitness.UNSUPPORTED;
        }
        return PolicyAssertionValidator.Fitness.UNKNOWN;
    }
}

