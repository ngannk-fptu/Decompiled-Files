/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.sourcemodel;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.sourcemodel.ModelNode;
import com.sun.xml.ws.policy.sourcemodel.PolicyModelGenerator;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;

class NormalizedModelGenerator
extends PolicyModelGenerator {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(NormalizedModelGenerator.class);
    private final PolicyModelGenerator.PolicySourceModelCreator sourceModelCreator;

    NormalizedModelGenerator(PolicyModelGenerator.PolicySourceModelCreator sourceModelCreator) {
        this.sourceModelCreator = sourceModelCreator;
    }

    @Override
    public PolicySourceModel translate(Policy policy) throws PolicyException {
        LOGGER.entering(new Object[]{policy});
        PolicySourceModel model = null;
        if (policy == null) {
            LOGGER.fine(LocalizationMessages.WSP_0047_POLICY_IS_NULL_RETURNING());
        } else {
            model = this.sourceModelCreator.create(policy);
            ModelNode rootNode = model.getRootNode();
            ModelNode exactlyOneNode = rootNode.createChildExactlyOneNode();
            for (AssertionSet set : policy) {
                ModelNode alternativeNode = exactlyOneNode.createChildAllNode();
                for (PolicyAssertion assertion : set) {
                    AssertionData data = AssertionData.createAssertionData(assertion.getName(), assertion.getValue(), assertion.getAttributes(), assertion.isOptional(), assertion.isIgnorable());
                    ModelNode assertionNode = alternativeNode.createChildAssertionNode(data);
                    if (assertion.hasNestedPolicy()) {
                        this.translate(assertionNode, assertion.getNestedPolicy());
                    }
                    if (!assertion.hasParameters()) continue;
                    this.translate(assertionNode, assertion.getParametersIterator());
                }
            }
        }
        LOGGER.exiting(model);
        return model;
    }

    @Override
    protected ModelNode translate(ModelNode parentAssertion, NestedPolicy policy) {
        ModelNode nestedPolicyRoot = parentAssertion.createChildPolicyNode();
        ModelNode exactlyOneNode = nestedPolicyRoot.createChildExactlyOneNode();
        AssertionSet set = policy.getAssertionSet();
        ModelNode alternativeNode = exactlyOneNode.createChildAllNode();
        this.translate(alternativeNode, set);
        return nestedPolicyRoot;
    }
}

