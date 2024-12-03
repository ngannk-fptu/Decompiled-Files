/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.PolicyAssertion
 *  com.sun.xml.ws.policy.spi.PolicyAssertionValidator
 *  com.sun.xml.ws.policy.spi.PolicyAssertionValidator$Fitness
 */
package com.sun.xml.ws.encoding.policy;

import com.sun.xml.ws.encoding.policy.EncodingConstants;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import java.util.ArrayList;
import javax.xml.namespace.QName;

public class EncodingPolicyValidator
implements PolicyAssertionValidator {
    private static final ArrayList<QName> serverSideSupportedAssertions = new ArrayList(3);
    private static final ArrayList<QName> clientSideSupportedAssertions = new ArrayList(4);

    public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion assertion) {
        return clientSideSupportedAssertions.contains(assertion.getName()) ? PolicyAssertionValidator.Fitness.SUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN;
    }

    public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion assertion) {
        QName assertionName = assertion.getName();
        if (serverSideSupportedAssertions.contains(assertionName)) {
            return PolicyAssertionValidator.Fitness.SUPPORTED;
        }
        if (clientSideSupportedAssertions.contains(assertionName)) {
            return PolicyAssertionValidator.Fitness.UNSUPPORTED;
        }
        return PolicyAssertionValidator.Fitness.UNKNOWN;
    }

    public String[] declareSupportedDomains() {
        return new String[]{"http://schemas.xmlsoap.org/ws/2004/09/policy/optimizedmimeserialization", "http://schemas.xmlsoap.org/ws/2004/09/policy/encoding", "http://java.sun.com/xml/ns/wsit/2006/09/policy/encoding/client", "http://java.sun.com/xml/ns/wsit/2006/09/policy/fastinfoset/service"};
    }

    static {
        serverSideSupportedAssertions.add(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION);
        serverSideSupportedAssertions.add(EncodingConstants.UTF816FFFE_CHARACTER_ENCODING_ASSERTION);
        serverSideSupportedAssertions.add(EncodingConstants.OPTIMIZED_FI_SERIALIZATION_ASSERTION);
        clientSideSupportedAssertions.add(EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION);
        clientSideSupportedAssertions.addAll(serverSideSupportedAssertions);
    }
}

