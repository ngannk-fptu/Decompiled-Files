/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.PolicyMerger;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.subject.PolicyMapKeyConverter;
import com.sun.xml.ws.policy.subject.WsdlBindingSubject;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.namespace.QName;

public class PolicyMapUtil {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapUtil.class);
    private static final PolicyMerger MERGER = PolicyMerger.getMerger();

    private PolicyMapUtil() {
    }

    public static void rejectAlternatives(PolicyMap map) throws PolicyException {
        for (Policy policy : map) {
            if (policy.getNumberOfAssertionSets() <= 1) continue;
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0035_RECONFIGURE_ALTERNATIVES(policy.getIdOrName())));
        }
    }

    public static void insertPolicies(PolicyMap policyMap, Collection<PolicySubject> policySubjects, QName serviceName, QName portName) throws PolicyException {
        WsdlBindingSubject wsdlSubject;
        LOGGER.entering(new Object[]{policyMap, policySubjects, serviceName, portName});
        HashMap subjectToPolicies = new HashMap();
        for (PolicySubject subject : policySubjects) {
            Object actualSubject = subject.getSubject();
            if (!(actualSubject instanceof WsdlBindingSubject)) continue;
            wsdlSubject = (WsdlBindingSubject)actualSubject;
            LinkedList<Policy> subjectPolicies = new LinkedList<Policy>();
            subjectPolicies.add(subject.getEffectivePolicy(MERGER));
            Collection existingPolicies = subjectToPolicies.put(wsdlSubject, subjectPolicies);
            if (existingPolicies == null) continue;
            subjectPolicies.addAll(existingPolicies);
        }
        PolicyMapKeyConverter converter = new PolicyMapKeyConverter(serviceName, portName);
        for (Map.Entry entry : subjectToPolicies.entrySet()) {
            wsdlSubject = (WsdlBindingSubject)entry.getKey();
            Collection policySet = (Collection)entry.getValue();
            PolicySubject newSubject = new PolicySubject((Object)wsdlSubject, policySet);
            PolicyMapKey mapKey = converter.getPolicyMapKey(wsdlSubject);
            if (wsdlSubject.isBindingSubject()) {
                policyMap.putSubject(PolicyMap.ScopeType.ENDPOINT, mapKey, newSubject);
                continue;
            }
            if (wsdlSubject.isBindingOperationSubject()) {
                policyMap.putSubject(PolicyMap.ScopeType.OPERATION, mapKey, newSubject);
                continue;
            }
            if (!wsdlSubject.isBindingMessageSubject()) continue;
            switch (wsdlSubject.getMessageType()) {
                case INPUT: {
                    policyMap.putSubject(PolicyMap.ScopeType.INPUT_MESSAGE, mapKey, newSubject);
                    break;
                }
                case OUTPUT: {
                    policyMap.putSubject(PolicyMap.ScopeType.OUTPUT_MESSAGE, mapKey, newSubject);
                    break;
                }
                case FAULT: {
                    policyMap.putSubject(PolicyMap.ScopeType.FAULT_MESSAGE, mapKey, newSubject);
                    break;
                }
            }
        }
        LOGGER.exiting();
    }
}

