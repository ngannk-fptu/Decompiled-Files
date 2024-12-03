/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.AssertionValidationProcessor
 *  com.sun.xml.ws.policy.EffectiveAlternativeSelector
 *  com.sun.xml.ws.policy.EffectivePolicyModifier
 *  com.sun.xml.ws.policy.PolicyException
 */
package com.sun.xml.ws.api.policy;

import com.sun.xml.ws.api.policy.ValidationProcessor;
import com.sun.xml.ws.policy.AssertionValidationProcessor;
import com.sun.xml.ws.policy.EffectiveAlternativeSelector;
import com.sun.xml.ws.policy.EffectivePolicyModifier;
import com.sun.xml.ws.policy.PolicyException;

public class AlternativeSelector
extends EffectiveAlternativeSelector {
    public static void doSelection(EffectivePolicyModifier modifier) throws PolicyException {
        ValidationProcessor validationProcessor = ValidationProcessor.getInstance();
        AlternativeSelector.selectAlternatives((EffectivePolicyModifier)modifier, (AssertionValidationProcessor)validationProcessor);
    }
}

