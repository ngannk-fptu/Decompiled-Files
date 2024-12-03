/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.AssertionValidationProcessor;
import com.sun.xml.ws.policy.EffectivePolicyModifier;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import java.util.LinkedList;

public class EffectiveAlternativeSelector {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(EffectiveAlternativeSelector.class);

    public static void doSelection(EffectivePolicyModifier modifier) throws PolicyException {
        AssertionValidationProcessor validationProcessor = AssertionValidationProcessor.getInstance();
        EffectiveAlternativeSelector.selectAlternatives(modifier, validationProcessor);
    }

    protected static void selectAlternatives(EffectivePolicyModifier modifier, AssertionValidationProcessor validationProcessor) throws PolicyException {
        Policy oldPolicy;
        PolicyMap map = modifier.getMap();
        for (PolicyMapKey mapKey : map.getAllServiceScopeKeys()) {
            oldPolicy = map.getServiceEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForServiceScope(mapKey, EffectiveAlternativeSelector.selectBestAlternative(oldPolicy, validationProcessor));
        }
        for (PolicyMapKey mapKey : map.getAllEndpointScopeKeys()) {
            oldPolicy = map.getEndpointEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForEndpointScope(mapKey, EffectiveAlternativeSelector.selectBestAlternative(oldPolicy, validationProcessor));
        }
        for (PolicyMapKey mapKey : map.getAllOperationScopeKeys()) {
            oldPolicy = map.getOperationEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForOperationScope(mapKey, EffectiveAlternativeSelector.selectBestAlternative(oldPolicy, validationProcessor));
        }
        for (PolicyMapKey mapKey : map.getAllInputMessageScopeKeys()) {
            oldPolicy = map.getInputMessageEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForInputMessageScope(mapKey, EffectiveAlternativeSelector.selectBestAlternative(oldPolicy, validationProcessor));
        }
        for (PolicyMapKey mapKey : map.getAllOutputMessageScopeKeys()) {
            oldPolicy = map.getOutputMessageEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForOutputMessageScope(mapKey, EffectiveAlternativeSelector.selectBestAlternative(oldPolicy, validationProcessor));
        }
        for (PolicyMapKey mapKey : map.getAllFaultMessageScopeKeys()) {
            oldPolicy = map.getFaultMessageEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForFaultMessageScope(mapKey, EffectiveAlternativeSelector.selectBestAlternative(oldPolicy, validationProcessor));
        }
    }

    private static Policy selectBestAlternative(Policy policy, AssertionValidationProcessor validationProcessor) throws PolicyException {
        AssertionSet bestAlternative = null;
        AlternativeFitness bestAlternativeFitness = AlternativeFitness.UNEVALUATED;
        for (AssertionSet alternative : policy) {
            AlternativeFitness alternativeFitness = alternative.isEmpty() ? AlternativeFitness.SUPPORTED_EMPTY : AlternativeFitness.UNEVALUATED;
            for (PolicyAssertion assertion : alternative) {
                PolicyAssertionValidator.Fitness assertionFitness = validationProcessor.validateClientSide(assertion);
                switch (assertionFitness) {
                    case UNKNOWN: 
                    case UNSUPPORTED: 
                    case INVALID: {
                        LOGGER.warning(LocalizationMessages.WSP_0075_PROBLEMATIC_ASSERTION_STATE(assertion.getName(), (Object)assertionFitness));
                        break;
                    }
                }
                alternativeFitness = alternativeFitness.combine(assertionFitness);
            }
            if (bestAlternativeFitness.compareTo(alternativeFitness) < 0) {
                bestAlternative = alternative;
                bestAlternativeFitness = alternativeFitness;
            }
            if (bestAlternativeFitness != AlternativeFitness.SUPPORTED) continue;
            break;
        }
        switch (bestAlternativeFitness) {
            case INVALID: {
                throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0053_INVALID_CLIENT_SIDE_ALTERNATIVE()));
            }
            case UNKNOWN: 
            case UNSUPPORTED: 
            case PARTIALLY_SUPPORTED: {
                LOGGER.warning(LocalizationMessages.WSP_0019_SUBOPTIMAL_ALTERNATIVE_SELECTED((Object)bestAlternativeFitness));
                break;
            }
        }
        LinkedList<AssertionSet> alternativeSet = null;
        if (bestAlternative != null) {
            alternativeSet = new LinkedList<AssertionSet>();
            alternativeSet.add(bestAlternative);
        }
        return Policy.createPolicy(policy.getNamespaceVersion(), policy.getName(), policy.getId(), alternativeSet);
    }

    private static enum AlternativeFitness {
        UNEVALUATED{

            @Override
            AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
                switch (assertionFitness) {
                    case UNKNOWN: {
                        return UNKNOWN;
                    }
                    case UNSUPPORTED: {
                        return UNSUPPORTED;
                    }
                    case SUPPORTED: {
                        return SUPPORTED;
                    }
                    case INVALID: {
                        return INVALID;
                    }
                }
                return UNEVALUATED;
            }
        }
        ,
        INVALID{

            @Override
            AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
                return INVALID;
            }
        }
        ,
        UNKNOWN{

            @Override
            AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
                switch (assertionFitness) {
                    case UNKNOWN: {
                        return UNKNOWN;
                    }
                    case UNSUPPORTED: {
                        return UNSUPPORTED;
                    }
                    case SUPPORTED: {
                        return PARTIALLY_SUPPORTED;
                    }
                    case INVALID: {
                        return INVALID;
                    }
                }
                return UNEVALUATED;
            }
        }
        ,
        UNSUPPORTED{

            @Override
            AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
                switch (assertionFitness) {
                    case UNKNOWN: 
                    case UNSUPPORTED: {
                        return UNSUPPORTED;
                    }
                    case SUPPORTED: {
                        return PARTIALLY_SUPPORTED;
                    }
                    case INVALID: {
                        return INVALID;
                    }
                }
                return UNEVALUATED;
            }
        }
        ,
        PARTIALLY_SUPPORTED{

            @Override
            AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
                switch (assertionFitness) {
                    case UNKNOWN: 
                    case UNSUPPORTED: 
                    case SUPPORTED: {
                        return PARTIALLY_SUPPORTED;
                    }
                    case INVALID: {
                        return INVALID;
                    }
                }
                return UNEVALUATED;
            }
        }
        ,
        SUPPORTED_EMPTY{

            @Override
            AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
                throw new UnsupportedOperationException("Combine operation was called unexpectedly on 'SUPPORTED_EMPTY' alternative fitness enumeration state.");
            }
        }
        ,
        SUPPORTED{

            @Override
            AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
                switch (assertionFitness) {
                    case UNKNOWN: 
                    case UNSUPPORTED: {
                        return PARTIALLY_SUPPORTED;
                    }
                    case SUPPORTED: {
                        return SUPPORTED;
                    }
                    case INVALID: {
                        return INVALID;
                    }
                }
                return UNEVALUATED;
            }
        };


        abstract AlternativeFitness combine(PolicyAssertionValidator.Fitness var1);
    }
}

