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
 */
package com.sun.xml.ws.encoding.policy;

import com.sun.xml.ws.api.client.SelectOptimalEncodingFeature;
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
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;

public class SelectOptimalEncodingFeatureConfigurator
implements PolicyFeatureConfigurator {
    public static final QName enabled = new QName("enabled");

    @Override
    public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
        Policy policy;
        LinkedList<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if (key != null && policyMap != null && null != (policy = policyMap.getEndpointEffectivePolicy(key)) && policy.contains(EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION)) {
            for (AssertionSet assertionSet : policy) {
                for (PolicyAssertion assertion : assertionSet) {
                    if (!EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION.equals(assertion.getName())) continue;
                    String value = assertion.getAttributeValue(enabled);
                    boolean isSelectOptimalEncodingEnabled = value == null || Boolean.valueOf(value.trim()) != false;
                    features.add(new SelectOptimalEncodingFeature(isSelectOptimalEncodingEnabled));
                }
            }
        }
        return features;
    }
}

