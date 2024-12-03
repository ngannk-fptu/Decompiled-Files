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
 *  javax.xml.ws.soap.MTOMFeature
 */
package com.sun.xml.ws.encoding.policy;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.encoding.policy.EncodingConstants;
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
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.MTOMFeature;

public class MtomPolicyMapConfigurator
implements PolicyMapConfigurator {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(MtomPolicyMapConfigurator.class);

    @Override
    public Collection<PolicySubject> update(PolicyMap policyMap, SEIModel model, WSBinding wsBinding) throws PolicyException {
        LOGGER.entering(new Object[]{policyMap, model, wsBinding});
        ArrayList<PolicySubject> subjects = new ArrayList<PolicySubject>();
        if (policyMap != null) {
            MTOMFeature mtomFeature = wsBinding.getFeature(MTOMFeature.class);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("mtomFeature = " + mtomFeature);
            }
            if (mtomFeature != null && mtomFeature.isEnabled()) {
                QName bindingName = model.getBoundPortTypeName();
                WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingSubject((QName)bindingName);
                Policy mtomPolicy = this.createMtomPolicy(bindingName);
                PolicySubject mtomPolicySubject = new PolicySubject((Object)wsdlSubject, mtomPolicy);
                subjects.add(mtomPolicySubject);
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.fine("Added MTOM policy with ID \"" + mtomPolicy.getIdOrName() + "\" to binding element \"" + bindingName + "\"");
                }
            }
        }
        LOGGER.exiting(subjects);
        return subjects;
    }

    private Policy createMtomPolicy(QName bindingName) {
        ArrayList<AssertionSet> assertionSets = new ArrayList<AssertionSet>(1);
        ArrayList<MtomAssertion> assertions = new ArrayList<MtomAssertion>(1);
        assertions.add(new MtomAssertion());
        assertionSets.add(AssertionSet.createAssertionSet(assertions));
        return Policy.createPolicy(null, (String)(bindingName.getLocalPart() + "_MTOM_Policy"), assertionSets);
    }

    static class MtomAssertion
    extends PolicyAssertion {
        private static final AssertionData mtomData = AssertionData.createAssertionData((QName)EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION);

        MtomAssertion() {
            super(mtomData, null, null);
        }

        static {
            mtomData.setOptionalAttribute(true);
        }
    }
}

