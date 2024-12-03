/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.Policy
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMapExtender
 *  com.sun.xml.ws.policy.PolicySubject
 *  com.sun.xml.ws.policy.privateutil.PolicyLogger
 *  com.sun.xml.ws.policy.sourcemodel.PolicySourceModel
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.ws.api.policy.ModelTranslator;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMapExtender;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.resources.PolicyMessages;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

abstract class BuilderHandler {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(BuilderHandler.class);
    Map<String, PolicySourceModel> policyStore;
    Collection<String> policyURIs;
    Object policySubject;

    BuilderHandler(Collection<String> policyURIs, Map<String, PolicySourceModel> policyStore, Object policySubject) {
        this.policyStore = policyStore;
        this.policyURIs = policyURIs;
        this.policySubject = policySubject;
    }

    final void populate(PolicyMapExtender policyMapExtender) throws PolicyException {
        if (null == policyMapExtender) {
            throw (PolicyException)LOGGER.logSevereException((Throwable)new PolicyException(PolicyMessages.WSP_1006_POLICY_MAP_EXTENDER_CAN_NOT_BE_NULL()));
        }
        this.doPopulate(policyMapExtender);
    }

    protected abstract void doPopulate(PolicyMapExtender var1) throws PolicyException;

    final Collection<Policy> getPolicies() throws PolicyException {
        if (null == this.policyURIs) {
            throw (PolicyException)LOGGER.logSevereException((Throwable)new PolicyException(PolicyMessages.WSP_1004_POLICY_URIS_CAN_NOT_BE_NULL()));
        }
        if (null == this.policyStore) {
            throw (PolicyException)LOGGER.logSevereException((Throwable)new PolicyException(PolicyMessages.WSP_1010_NO_POLICIES_DEFINED()));
        }
        ArrayList<Policy> result = new ArrayList<Policy>(this.policyURIs.size());
        for (String policyURI : this.policyURIs) {
            PolicySourceModel sourceModel = this.policyStore.get(policyURI);
            if (sourceModel == null) {
                throw (PolicyException)LOGGER.logSevereException((Throwable)new PolicyException(PolicyMessages.WSP_1005_POLICY_REFERENCE_DOES_NOT_EXIST(policyURI)));
            }
            result.add(ModelTranslator.getTranslator().translate(sourceModel));
        }
        return result;
    }

    final Collection<PolicySubject> getPolicySubjects() throws PolicyException {
        Collection<Policy> policies = this.getPolicies();
        ArrayList<PolicySubject> result = new ArrayList<PolicySubject>(policies.size());
        for (Policy policy : policies) {
            result.add(new PolicySubject(this.policySubject, policy));
        }
        return result;
    }
}

