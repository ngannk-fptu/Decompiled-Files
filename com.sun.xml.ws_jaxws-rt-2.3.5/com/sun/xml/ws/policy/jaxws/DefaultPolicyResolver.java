/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.AssertionSet
 *  com.sun.xml.ws.policy.EffectivePolicyModifier
 *  com.sun.xml.ws.policy.Policy
 *  com.sun.xml.ws.policy.PolicyAssertion
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.spi.PolicyAssertionValidator$Fitness
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.ws.api.policy.AlternativeSelector;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.policy.ValidationProcessor;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.EffectivePolicyModifier;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.ws.resources.PolicyMessages;
import javax.xml.ws.WebServiceException;

public class DefaultPolicyResolver
implements PolicyResolver {
    @Override
    public PolicyMap resolve(PolicyResolver.ServerContext context) {
        PolicyMap map = context.getPolicyMap();
        if (map != null) {
            this.validateServerPolicyMap(map);
        }
        return map;
    }

    @Override
    public PolicyMap resolve(PolicyResolver.ClientContext context) {
        PolicyMap map = context.getPolicyMap();
        if (map != null) {
            map = this.doAlternativeSelection(map);
        }
        return map;
    }

    private void validateServerPolicyMap(PolicyMap policyMap) {
        try {
            ValidationProcessor validationProcessor = ValidationProcessor.getInstance();
            for (Policy policy : policyMap) {
                for (AssertionSet assertionSet : policy) {
                    for (PolicyAssertion assertion : assertionSet) {
                        PolicyAssertionValidator.Fitness validationResult = validationProcessor.validateServerSide(assertion);
                        if (validationResult == PolicyAssertionValidator.Fitness.SUPPORTED) continue;
                        throw new PolicyException(PolicyMessages.WSP_1015_SERVER_SIDE_ASSERTION_VALIDATION_FAILED(assertion.getName(), validationResult));
                    }
                }
            }
        }
        catch (PolicyException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private PolicyMap doAlternativeSelection(PolicyMap policyMap) {
        EffectivePolicyModifier modifier = EffectivePolicyModifier.createEffectivePolicyModifier();
        modifier.connect(policyMap);
        try {
            AlternativeSelector.doSelection(modifier);
        }
        catch (PolicyException e) {
            throw new WebServiceException((Throwable)e);
        }
        return policyMap;
    }
}

