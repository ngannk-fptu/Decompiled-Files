/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.PolicyMapMutator;
import com.sun.xml.ws.policy.PolicySubject;

public final class PolicyMapExtender
extends PolicyMapMutator {
    private PolicyMapExtender() {
    }

    public static PolicyMapExtender createPolicyMapExtender() {
        return new PolicyMapExtender();
    }

    public void putServiceSubject(PolicyMapKey key, PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.SERVICE, key, subject);
    }

    public void putEndpointSubject(PolicyMapKey key, PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.ENDPOINT, key, subject);
    }

    public void putOperationSubject(PolicyMapKey key, PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.OPERATION, key, subject);
    }

    public void putInputMessageSubject(PolicyMapKey key, PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.INPUT_MESSAGE, key, subject);
    }

    public void putOutputMessageSubject(PolicyMapKey key, PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.OUTPUT_MESSAGE, key, subject);
    }

    public void putFaultMessageSubject(PolicyMapKey key, PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.FAULT_MESSAGE, key, subject);
    }
}

