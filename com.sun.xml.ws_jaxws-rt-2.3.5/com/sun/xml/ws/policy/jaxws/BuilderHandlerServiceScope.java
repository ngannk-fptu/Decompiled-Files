/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapExtender
 *  com.sun.xml.ws.policy.PolicyMapKey
 *  com.sun.xml.ws.policy.PolicySubject
 *  com.sun.xml.ws.policy.sourcemodel.PolicySourceModel
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapExtender;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.jaxws.BuilderHandler;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import java.util.Collection;
import java.util.Map;
import javax.xml.namespace.QName;

final class BuilderHandlerServiceScope
extends BuilderHandler {
    private final QName service;

    BuilderHandlerServiceScope(Collection<String> policyURIs, Map<String, PolicySourceModel> policyStore, Object policySubject, QName service) {
        super(policyURIs, policyStore, policySubject);
        this.service = service;
    }

    @Override
    protected void doPopulate(PolicyMapExtender policyMapExtender) throws PolicyException {
        PolicyMapKey mapKey = PolicyMap.createWsdlServiceScopeKey((QName)this.service);
        for (PolicySubject subject : this.getPolicySubjects()) {
            policyMapExtender.putServiceSubject(mapKey, subject);
        }
    }

    public String toString() {
        return this.service.toString();
    }
}

