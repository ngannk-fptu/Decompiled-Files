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

final class BuilderHandlerOperationScope
extends BuilderHandler {
    private final QName service;
    private final QName port;
    private final QName operation;

    BuilderHandlerOperationScope(Collection<String> policyURIs, Map<String, PolicySourceModel> policyStore, Object policySubject, QName service, QName port, QName operation) {
        super(policyURIs, policyStore, policySubject);
        this.service = service;
        this.port = port;
        this.operation = operation;
    }

    @Override
    protected void doPopulate(PolicyMapExtender policyMapExtender) throws PolicyException {
        PolicyMapKey mapKey = PolicyMap.createWsdlOperationScopeKey((QName)this.service, (QName)this.port, (QName)this.operation);
        for (PolicySubject subject : this.getPolicySubjects()) {
            policyMapExtender.putOperationSubject(mapKey, subject);
        }
    }
}

