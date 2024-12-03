/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.util.Which
 *  com.sun.xml.ws.policy.AssertionSet
 *  com.sun.xml.ws.policy.NestedPolicy
 *  com.sun.xml.ws.policy.Policy
 *  com.sun.xml.ws.policy.PolicyAssertion
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapKey
 *  com.sun.xml.ws.policy.privateutil.PolicyLogger
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.soap.AddressingFeature
 *  javax.xml.ws.soap.AddressingFeature$Responses
 */
package com.sun.xml.ws.addressing.policy;

import com.sun.xml.bind.util.Which;
import com.sun.xml.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.resources.ModelerMessages;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;

public class AddressingFeatureConfigurator
implements PolicyFeatureConfigurator {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingFeatureConfigurator.class);
    private static final QName[] ADDRESSING_ASSERTIONS = new QName[]{new QName(AddressingVersion.MEMBER.policyNsUri, "UsingAddressing")};

    @Override
    public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
        LOGGER.entering(new Object[]{key, policyMap});
        LinkedList<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if (key != null && policyMap != null) {
            Policy policy = policyMap.getEndpointEffectivePolicy(key);
            for (QName addressingAssertionQName : ADDRESSING_ASSERTIONS) {
                if (policy == null || !policy.contains(addressingAssertionQName)) continue;
                for (AssertionSet assertionSet : policy) {
                    for (PolicyAssertion assertion : assertionSet) {
                        if (!assertion.getName().equals(addressingAssertionQName)) continue;
                        WebServiceFeature feature = AddressingVersion.getFeature(addressingAssertionQName.getNamespaceURI(), true, !assertion.isOptional());
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.fine("Added addressing feature \"" + feature + "\" for element \"" + key + "\"");
                        }
                        features.add(feature);
                    }
                }
            }
            if (policy != null && policy.contains(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
                for (AssertionSet assertions : policy) {
                    for (PolicyAssertion assertion : assertions) {
                        AddressingFeature feature;
                        if (!assertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) continue;
                        NestedPolicy nestedPolicy = assertion.getNestedPolicy();
                        boolean requiresAnonymousResponses = false;
                        boolean requiresNonAnonymousResponses = false;
                        if (nestedPolicy != null) {
                            requiresAnonymousResponses = nestedPolicy.contains(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
                            requiresNonAnonymousResponses = nestedPolicy.contains(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
                        }
                        if (requiresAnonymousResponses && requiresNonAnonymousResponses) {
                            throw new WebServiceException("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion");
                        }
                        try {
                            feature = requiresAnonymousResponses ? new AddressingFeature(true, !assertion.isOptional(), AddressingFeature.Responses.ANONYMOUS) : (requiresNonAnonymousResponses ? new AddressingFeature(true, !assertion.isOptional(), AddressingFeature.Responses.NON_ANONYMOUS) : new AddressingFeature(true, !assertion.isOptional()));
                        }
                        catch (NoSuchMethodError e) {
                            throw (PolicyException)LOGGER.logSevereException((Throwable)new PolicyException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(AddressingFeatureConfigurator.toJar(Which.which(AddressingFeature.class))), (Throwable)e));
                        }
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.fine("Added addressing feature \"" + feature + "\" for element \"" + key + "\"");
                        }
                        features.add((WebServiceFeature)feature);
                    }
                }
            }
        }
        LOGGER.exiting(features);
        return features;
    }

    private static String toJar(String url) {
        if (!url.startsWith("jar:")) {
            return url;
        }
        url = url.substring(4);
        return url.substring(0, url.lastIndexOf(33));
    }
}

