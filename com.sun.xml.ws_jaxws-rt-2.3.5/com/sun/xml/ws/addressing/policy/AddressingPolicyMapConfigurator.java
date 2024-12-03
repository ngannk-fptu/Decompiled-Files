/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.AssertionSet
 *  com.sun.xml.ws.policy.Policy
 *  com.sun.xml.ws.policy.PolicyAssertion
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicySubject
 *  com.sun.xml.ws.policy.privateutil.PolicyLogger
 *  com.sun.xml.ws.policy.sourcemodel.AssertionData
 *  com.sun.xml.ws.policy.subject.WsdlBindingSubject
 *  javax.xml.ws.soap.AddressingFeature
 *  javax.xml.ws.soap.AddressingFeature$Responses
 */
package com.sun.xml.ws.addressing.policy;

import com.sun.xml.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.subject.WsdlBindingSubject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.AddressingFeature;

public class AddressingPolicyMapConfigurator
implements PolicyMapConfigurator {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AddressingPolicyMapConfigurator.class);

    @Override
    public Collection<PolicySubject> update(PolicyMap policyMap, SEIModel model, WSBinding wsBinding) throws PolicyException {
        LOGGER.entering(new Object[]{policyMap, model, wsBinding});
        ArrayList<PolicySubject> subjects = new ArrayList<PolicySubject>();
        if (policyMap != null) {
            AddressingFeature addressingFeature = wsBinding.getFeature(AddressingFeature.class);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("addressingFeature = " + addressingFeature);
            }
            if (addressingFeature != null && addressingFeature.isEnabled()) {
                this.addWsamAddressing(subjects, policyMap, model, addressingFeature);
            }
        }
        LOGGER.exiting(subjects);
        return subjects;
    }

    private void addWsamAddressing(Collection<PolicySubject> subjects, PolicyMap policyMap, SEIModel model, AddressingFeature addressingFeature) throws PolicyException {
        QName bindingName = model.getBoundPortTypeName();
        WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingSubject((QName)bindingName);
        Policy addressingPolicy = this.createWsamAddressingPolicy(bindingName, addressingFeature);
        PolicySubject addressingPolicySubject = new PolicySubject((Object)wsdlSubject, addressingPolicy);
        subjects.add(addressingPolicySubject);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Added addressing policy with ID \"" + addressingPolicy.getIdOrName() + "\" to binding element \"" + bindingName + "\"");
        }
    }

    private Policy createWsamAddressingPolicy(QName bindingName, AddressingFeature af) {
        ArrayList<AssertionSet> assertionSets = new ArrayList<AssertionSet>(1);
        ArrayList<AddressingAssertion> assertions = new ArrayList<AddressingAssertion>(1);
        AssertionData addressingData = AssertionData.createAssertionData((QName)W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
        if (!af.isRequired()) {
            addressingData.setOptionalAttribute(true);
        }
        try {
            AddressingFeature.Responses responses = af.getResponses();
            if (responses == AddressingFeature.Responses.ANONYMOUS) {
                AssertionData nestedAsserData = AssertionData.createAssertionData((QName)W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
                AddressingAssertion nestedAsser = new AddressingAssertion(nestedAsserData, null);
                assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(Collections.singleton(nestedAsser))));
            } else if (responses == AddressingFeature.Responses.NON_ANONYMOUS) {
                AssertionData nestedAsserData = AssertionData.createAssertionData((QName)W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
                AddressingAssertion nestedAsser = new AddressingAssertion(nestedAsserData, null);
                assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(Collections.singleton(nestedAsser))));
            } else {
                assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(null)));
            }
        }
        catch (NoSuchMethodError e) {
            assertions.add(new AddressingAssertion(addressingData, AssertionSet.createAssertionSet(null)));
        }
        assertionSets.add(AssertionSet.createAssertionSet(assertions));
        return Policy.createPolicy(null, (String)(bindingName.getLocalPart() + "_WSAM_Addressing_Policy"), assertionSets);
    }

    private static final class AddressingAssertion
    extends PolicyAssertion {
        AddressingAssertion(AssertionData assertionData, AssertionSet nestedAlternative) {
            super(assertionData, null, nestedAlternative);
        }

        AddressingAssertion(AssertionData assertionData) {
            super(assertionData, null, null);
        }
    }
}

