/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.NestedPolicy
 *  com.sun.xml.ws.policy.PolicyAssertion
 *  com.sun.xml.ws.policy.privateutil.PolicyLogger
 *  com.sun.xml.ws.policy.spi.PolicyAssertionValidator
 *  com.sun.xml.ws.policy.spi.PolicyAssertionValidator$Fitness
 */
package com.sun.xml.ws.addressing.policy;

import com.sun.xml.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import java.util.ArrayList;
import javax.xml.namespace.QName;

public class AddressingPolicyValidator
implements PolicyAssertionValidator {
    private static final ArrayList<QName> supportedAssertions = new ArrayList();
    private static final PolicyLogger LOGGER;

    public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion assertion) {
        return supportedAssertions.contains(assertion.getName()) ? PolicyAssertionValidator.Fitness.SUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN;
    }

    public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion assertion) {
        NestedPolicy nestedPolicy;
        if (!supportedAssertions.contains(assertion.getName())) {
            return PolicyAssertionValidator.Fitness.UNKNOWN;
        }
        if (assertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION) && (nestedPolicy = assertion.getNestedPolicy()) != null) {
            boolean requiresAnonymousResponses = false;
            boolean requiresNonAnonymousResponses = false;
            for (PolicyAssertion nestedAsser : nestedPolicy.getAssertionSet()) {
                if (nestedAsser.getName().equals(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION)) {
                    requiresAnonymousResponses = true;
                    continue;
                }
                if (nestedAsser.getName().equals(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION)) {
                    requiresNonAnonymousResponses = true;
                    continue;
                }
                LOGGER.warning("Found unsupported assertion:\n" + nestedAsser + "\nnested into assertion:\n" + assertion);
                return PolicyAssertionValidator.Fitness.UNSUPPORTED;
            }
            if (requiresAnonymousResponses && requiresNonAnonymousResponses) {
                LOGGER.warning("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion");
                return PolicyAssertionValidator.Fitness.INVALID;
            }
        }
        return PolicyAssertionValidator.Fitness.SUPPORTED;
    }

    public String[] declareSupportedDomains() {
        return new String[]{AddressingVersion.MEMBER.policyNsUri, AddressingVersion.W3C.policyNsUri, "http://www.w3.org/2007/05/addressing/metadata"};
    }

    static {
        supportedAssertions.add(new QName(AddressingVersion.MEMBER.policyNsUri, "UsingAddressing"));
        supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
        supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
        supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
        LOGGER = PolicyLogger.getLogger(AddressingPolicyValidator.class);
    }
}

