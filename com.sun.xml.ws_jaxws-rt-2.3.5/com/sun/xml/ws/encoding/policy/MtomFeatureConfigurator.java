/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.AssertionSet
 *  com.sun.xml.ws.policy.Policy
 *  com.sun.xml.ws.policy.PolicyAssertion
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapKey
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.soap.MTOMFeature
 */
package com.sun.xml.ws.encoding.policy;

import com.sun.xml.ws.encoding.policy.EncodingConstants;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class MtomFeatureConfigurator
implements PolicyFeatureConfigurator {
    @Override
    public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
        Policy policy;
        LinkedList<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if (key != null && policyMap != null && null != (policy = policyMap.getEndpointEffectivePolicy(key)) && policy.contains(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION)) {
            for (AssertionSet assertionSet : policy) {
                for (PolicyAssertion assertion : assertionSet) {
                    if (!EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION.equals(assertion.getName())) continue;
                    features.add((WebServiceFeature)new MTOMFeature(true));
                }
            }
        }
        return features;
    }
}

