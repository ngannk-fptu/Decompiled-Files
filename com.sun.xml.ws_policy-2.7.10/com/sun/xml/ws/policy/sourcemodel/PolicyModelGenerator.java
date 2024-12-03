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
import com.sun.xml.ws.policy.sourcemodel.CompactModelGenerator;
import com.sun.xml.ws.policy.sourcemodel.ModelNode;
import com.sun.xml.ws.policy.sourcemodel.NormalizedModelGenerator;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import java.util.Iterator;

public abstract class PolicyModelGenerator {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyModelGenerator.class);

    protected PolicyModelGenerator() {
    }

    public static PolicyModelGenerator getGenerator() {
        return PolicyModelGenerator.getNormalizedGenerator(new PolicySourceModelCreator());
    }

    protected static PolicyModelGenerator getCompactGenerator(PolicySourceModelCreator creator) {
        return new CompactModelGenerator(creator);
    }

    protected static PolicyModelGenerator getNormalizedGenerator(PolicySourceModelCreator creator) {
        return new NormalizedModelGenerator(creator);
    }

    public abstract PolicySourceModel translate(Policy var1) throws PolicyException;

    protected abstract ModelNode translate(ModelNode var1, NestedPolicy var2);

    protected void translate(ModelNode node, AssertionSet assertions) {
        for (PolicyAssertion assertion : assertions) {
            AssertionData data = AssertionData.createAssertionData(assertion.getName(), assertion.getValue(), assertion.getAttributes(), assertion.isOptional(), assertion.isIgnorable());
            ModelNode assertionNode = node.createChildAssertionNode(data);
            if (assertion.hasNestedPolicy()) {
                this.translate(assertionNode, assertion.getNestedPolicy());
            }
            if (!assertion.hasParameters()) continue;
            this.translate(assertionNode, assertion.getParametersIterator());
        }
    }

    protected void translate(ModelNode assertionNode, Iterator<PolicyAssertion> assertionParametersIterator) {
        while (assertionParametersIterator.hasNext()) {
            PolicyAssertion assertionParameter = assertionParametersIterator.next();
            AssertionData data = AssertionData.createAssertionParameterData(assertionParameter.getName(), assertionParameter.getValue(), assertionParameter.getAttributes());
            ModelNode assertionParameterNode = assertionNode.createChildAssertionParameterNode(data);
            if (assertionParameter.hasNestedPolicy()) {
                throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0005_UNEXPECTED_POLICY_ELEMENT_FOUND_IN_ASSERTION_PARAM(assertionParameter)));
            }
            if (!assertionParameter.hasNestedAssertions()) continue;
            this.translate(assertionParameterNode, assertionParameter.getNestedAssertionsIterator());
        }
    }

    protected static class PolicySourceModelCreator {
        protected PolicySourceModelCreator() {
        }

        protected PolicySourceModel create(Policy policy) {
            return PolicySourceModel.createPolicySourceModel(policy.getNamespaceVersion(), policy.getId(), policy.getName());
        }
    }
}

