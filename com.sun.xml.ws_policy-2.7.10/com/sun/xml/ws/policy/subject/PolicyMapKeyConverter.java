/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.subject;

import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.subject.WsdlBindingSubject;
import javax.xml.namespace.QName;

public class PolicyMapKeyConverter {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapKeyConverter.class);
    private final QName serviceName;
    private final QName portName;

    public PolicyMapKeyConverter(QName serviceName, QName portName) {
        this.serviceName = serviceName;
        this.portName = portName;
    }

    public PolicyMapKey getPolicyMapKey(WsdlBindingSubject subject) {
        LOGGER.entering(new Object[]{subject});
        PolicyMapKey key = null;
        if (subject.isBindingSubject()) {
            key = PolicyMap.createWsdlEndpointScopeKey(this.serviceName, this.portName);
        } else if (subject.isBindingOperationSubject()) {
            key = PolicyMap.createWsdlOperationScopeKey(this.serviceName, this.portName, subject.getName());
        } else if (subject.isBindingMessageSubject()) {
            key = subject.getMessageType() == WsdlBindingSubject.WsdlMessageType.FAULT ? PolicyMap.createWsdlFaultMessageScopeKey(this.serviceName, this.portName, subject.getParent().getName(), subject.getName()) : PolicyMap.createWsdlMessageScopeKey(this.serviceName, this.portName, subject.getParent().getName());
        }
        LOGGER.exiting(key);
        return key;
    }
}

