/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.PolicyMapMutator;

public final class EffectivePolicyModifier
extends PolicyMapMutator {
    public static EffectivePolicyModifier createEffectivePolicyModifier() {
        return new EffectivePolicyModifier();
    }

    private EffectivePolicyModifier() {
    }

    public void setNewEffectivePolicyForServiceScope(PolicyMapKey key, Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.SERVICE, key, newEffectivePolicy);
    }

    public void setNewEffectivePolicyForEndpointScope(PolicyMapKey key, Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.ENDPOINT, key, newEffectivePolicy);
    }

    public void setNewEffectivePolicyForOperationScope(PolicyMapKey key, Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.OPERATION, key, newEffectivePolicy);
    }

    public void setNewEffectivePolicyForInputMessageScope(PolicyMapKey key, Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.INPUT_MESSAGE, key, newEffectivePolicy);
    }

    public void setNewEffectivePolicyForOutputMessageScope(PolicyMapKey key, Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.OUTPUT_MESSAGE, key, newEffectivePolicy);
    }

    public void setNewEffectivePolicyForFaultMessageScope(PolicyMapKey key, Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.FAULT_MESSAGE, key, newEffectivePolicy);
    }
}

