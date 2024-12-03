/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.sourcemodel;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.sourcemodel.DefaultPolicyAssertionCreator;
import com.sun.xml.ws.policy.sourcemodel.ModelNode;
import com.sun.xml.ws.policy.sourcemodel.PolicyReferenceData;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.policy.spi.PolicyAssertionCreator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class PolicyModelTranslator {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyModelTranslator.class);
    private static final PolicyAssertionCreator defaultCreator = new DefaultPolicyAssertionCreator();
    private final Map<String, PolicyAssertionCreator> assertionCreators;

    private PolicyModelTranslator() throws PolicyException {
        this(null);
    }

    protected PolicyModelTranslator(Collection<PolicyAssertionCreator> creators) throws PolicyException {
        LOGGER.entering(new Object[]{creators});
        LinkedList<PolicyAssertionCreator> allCreators = new LinkedList<PolicyAssertionCreator>();
        for (PolicyAssertionCreator creator : ServiceLoader.load(PolicyAssertionCreator.class)) {
            allCreators.add(creator);
        }
        if (creators != null) {
            for (PolicyAssertionCreator creator : creators) {
                allCreators.add(creator);
            }
        }
        HashMap<String, PolicyAssertionCreator> pacMap = new HashMap<String, PolicyAssertionCreator>();
        for (PolicyAssertionCreator creator : allCreators) {
            String[] supportedURIs = creator.getSupportedDomainNamespaceURIs();
            String creatorClassName = creator.getClass().getName();
            if (supportedURIs == null || supportedURIs.length == 0) {
                LOGGER.warning(LocalizationMessages.WSP_0077_ASSERTION_CREATOR_DOES_NOT_SUPPORT_ANY_URI(creatorClassName));
                continue;
            }
            for (String supportedURI : supportedURIs) {
                LOGGER.config(LocalizationMessages.WSP_0078_ASSERTION_CREATOR_DISCOVERED(creatorClassName, supportedURI));
                if (supportedURI == null || supportedURI.length() == 0) {
                    throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0070_ERROR_REGISTERING_ASSERTION_CREATOR(creatorClassName)));
                }
                PolicyAssertionCreator oldCreator = pacMap.put(supportedURI, creator);
                if (oldCreator == null) continue;
                throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0071_ERROR_MULTIPLE_ASSERTION_CREATORS_FOR_NAMESPACE(supportedURI, oldCreator.getClass().getName(), creator.getClass().getName())));
            }
        }
        this.assertionCreators = Collections.unmodifiableMap(pacMap);
        LOGGER.exiting();
    }

    public static PolicyModelTranslator getTranslator() throws PolicyException {
        return new PolicyModelTranslator();
    }

    public Policy translate(PolicySourceModel model) throws PolicyException {
        PolicySourceModel localPolicyModelCopy;
        LOGGER.entering(new Object[]{model});
        if (model == null) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0043_POLICY_MODEL_TRANSLATION_ERROR_INPUT_PARAM_NULL()));
        }
        try {
            localPolicyModelCopy = model.clone();
        }
        catch (CloneNotSupportedException e) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0016_UNABLE_TO_CLONE_POLICY_SOURCE_MODEL(), e));
        }
        String policyId = localPolicyModelCopy.getPolicyId();
        String policyName = localPolicyModelCopy.getPolicyName();
        Collection<AssertionSet> alternatives = this.createPolicyAlternatives(localPolicyModelCopy);
        LOGGER.finest(LocalizationMessages.WSP_0052_NUMBER_OF_ALTERNATIVE_COMBINATIONS_CREATED(alternatives.size()));
        Policy policy = null;
        if (alternatives.size() == 0) {
            policy = Policy.createNullPolicy(model.getNamespaceVersion(), policyName, policyId);
            LOGGER.finest(LocalizationMessages.WSP_0055_NO_ALTERNATIVE_COMBINATIONS_CREATED());
        } else if (alternatives.size() == 1 && alternatives.iterator().next().isEmpty()) {
            policy = Policy.createEmptyPolicy(model.getNamespaceVersion(), policyName, policyId);
            LOGGER.finest(LocalizationMessages.WSP_0026_SINGLE_EMPTY_ALTERNATIVE_COMBINATION_CREATED());
        } else {
            policy = Policy.createPolicy(model.getNamespaceVersion(), policyName, policyId, alternatives);
            LOGGER.finest(LocalizationMessages.WSP_0057_N_ALTERNATIVE_COMBINATIONS_M_POLICY_ALTERNATIVES_CREATED(alternatives.size(), policy.getNumberOfAssertionSets()));
        }
        LOGGER.exiting(policy);
        return policy;
    }

    private Collection<AssertionSet> createPolicyAlternatives(PolicySourceModel model) throws PolicyException {
        RawPolicy rootPolicy;
        ContentDecomposition decomposition = new ContentDecomposition();
        LinkedList<RawPolicy> policyQueue = new LinkedList<RawPolicy>();
        LinkedList contentQueue = new LinkedList();
        RawPolicy processedPolicy = rootPolicy = new RawPolicy(model.getRootNode(), new LinkedList<RawAlternative>());
        do {
            Collection processedContent = processedPolicy.originalContent;
            do {
                this.decompose(processedContent, decomposition);
                if (decomposition.exactlyOneContents.isEmpty()) {
                    RawAlternative alternative = new RawAlternative(decomposition.assertions);
                    processedPolicy.alternatives.add(alternative);
                    if (alternative.allNestedPolicies.isEmpty()) continue;
                    policyQueue.addAll(alternative.allNestedPolicies);
                    continue;
                }
                Collection combinations = PolicyUtils.Collections.combine(decomposition.assertions, decomposition.exactlyOneContents, false);
                if (combinations == null || combinations.isEmpty()) continue;
                contentQueue.addAll(combinations);
            } while ((processedContent = (Collection)contentQueue.poll()) != null);
        } while ((processedPolicy = (RawPolicy)policyQueue.poll()) != null);
        LinkedList<AssertionSet> assertionSets = new LinkedList<AssertionSet>();
        for (RawAlternative rootAlternative : rootPolicy.alternatives) {
            List<AssertionSet> normalizedAlternatives = this.normalizeRawAlternative(rootAlternative);
            assertionSets.addAll(normalizedAlternatives);
        }
        return assertionSets;
    }

    private void decompose(Collection<ModelNode> content, ContentDecomposition decomposition) throws PolicyException {
        ModelNode node;
        decomposition.reset();
        LinkedList<ModelNode> allContentQueue = new LinkedList<ModelNode>(content);
        block6: while ((node = (ModelNode)allContentQueue.poll()) != null) {
            switch (node.getType()) {
                case POLICY: 
                case ALL: {
                    allContentQueue.addAll(node.getChildren());
                    continue block6;
                }
                case POLICY_REFERENCE: {
                    allContentQueue.addAll(PolicyModelTranslator.getReferencedModelRootNode(node).getChildren());
                    continue block6;
                }
                case EXACTLY_ONE: {
                    decomposition.exactlyOneContents.add(this.expandsExactlyOneContent(node.getChildren()));
                    continue block6;
                }
                case ASSERTION: {
                    decomposition.assertions.add(node);
                    continue block6;
                }
            }
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0007_UNEXPECTED_MODEL_NODE_TYPE_FOUND((Object)node.getType())));
        }
    }

    private static ModelNode getReferencedModelRootNode(ModelNode policyReferenceNode) throws PolicyException {
        PolicySourceModel referencedModel = policyReferenceNode.getReferencedModel();
        if (referencedModel == null) {
            PolicyReferenceData refData = policyReferenceNode.getPolicyReferenceData();
            if (refData == null) {
                throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0041_POLICY_REFERENCE_NODE_FOUND_WITH_NO_POLICY_REFERENCE_IN_IT()));
            }
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0010_UNEXPANDED_POLICY_REFERENCE_NODE_FOUND_REFERENCING(refData.getReferencedModelUri())));
        }
        return referencedModel.getRootNode();
    }

    private Collection<ModelNode> expandsExactlyOneContent(Collection<ModelNode> content) throws PolicyException {
        ModelNode node;
        LinkedList<ModelNode> result = new LinkedList<ModelNode>();
        LinkedList<ModelNode> eoContentQueue = new LinkedList<ModelNode>(content);
        block5: while ((node = (ModelNode)eoContentQueue.poll()) != null) {
            switch (node.getType()) {
                case POLICY: 
                case ALL: 
                case ASSERTION: {
                    result.add(node);
                    continue block5;
                }
                case POLICY_REFERENCE: {
                    result.add(PolicyModelTranslator.getReferencedModelRootNode(node));
                    continue block5;
                }
                case EXACTLY_ONE: {
                    eoContentQueue.addAll(node.getChildren());
                    continue block5;
                }
            }
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0001_UNSUPPORTED_MODEL_NODE_TYPE((Object)node.getType())));
        }
        return result;
    }

    private List<AssertionSet> normalizeRawAlternative(RawAlternative alternative) throws AssertionCreationException, PolicyException {
        LinkedList<PolicyAssertion> normalizedContentBase = new LinkedList<PolicyAssertion>();
        LinkedList<List<PolicyAssertion>> normalizedContentOptions = new LinkedList<List<PolicyAssertion>>();
        if (!alternative.nestedAssertions.isEmpty()) {
            RawAssertion rawAssertion;
            LinkedList<RawAssertion> nestedAssertionsQueue = new LinkedList<RawAssertion>(alternative.nestedAssertions);
            while ((rawAssertion = (RawAssertion)nestedAssertionsQueue.poll()) != null) {
                List<PolicyAssertion> normalized = this.normalizeRawAssertion(rawAssertion);
                if (normalized.size() == 1) {
                    normalizedContentBase.addAll(normalized);
                    continue;
                }
                normalizedContentOptions.add(normalized);
            }
        }
        LinkedList<AssertionSet> options = new LinkedList<AssertionSet>();
        if (normalizedContentOptions.isEmpty()) {
            options.add(AssertionSet.createAssertionSet(normalizedContentBase));
        } else {
            Collection contentCombinations = PolicyUtils.Collections.combine(normalizedContentBase, normalizedContentOptions, true);
            for (Collection contentOption : contentCombinations) {
                options.add(AssertionSet.createAssertionSet(contentOption));
            }
        }
        return options;
    }

    private List<PolicyAssertion> normalizeRawAssertion(RawAssertion assertion) throws AssertionCreationException, PolicyException {
        boolean nestedAlternativesAvailable;
        ArrayList<PolicyAssertion> parameters;
        if (assertion.parameters.isEmpty()) {
            parameters = null;
        } else {
            parameters = new ArrayList<PolicyAssertion>(assertion.parameters.size());
            for (ModelNode parameterNode : assertion.parameters) {
                parameters.add(this.createPolicyAssertionParameter(parameterNode));
            }
        }
        LinkedList<AssertionSet> nestedAlternatives = new LinkedList<AssertionSet>();
        if (assertion.nestedAlternatives != null && !assertion.nestedAlternatives.isEmpty()) {
            RawAlternative rawAlternative;
            LinkedList<RawAlternative> nestedAlternativeQueue = new LinkedList<RawAlternative>(assertion.nestedAlternatives);
            while ((rawAlternative = (RawAlternative)nestedAlternativeQueue.poll()) != null) {
                nestedAlternatives.addAll(this.normalizeRawAlternative(rawAlternative));
            }
        }
        LinkedList<PolicyAssertion> assertionOptions = new LinkedList<PolicyAssertion>();
        boolean bl = nestedAlternativesAvailable = !nestedAlternatives.isEmpty();
        if (nestedAlternativesAvailable) {
            for (AssertionSet nestedAlternative : nestedAlternatives) {
                assertionOptions.add(this.createPolicyAssertion(assertion.originalNode.getNodeData(), parameters, nestedAlternative));
            }
        } else {
            assertionOptions.add(this.createPolicyAssertion(assertion.originalNode.getNodeData(), parameters, null));
        }
        return assertionOptions;
    }

    private PolicyAssertion createPolicyAssertionParameter(ModelNode parameterNode) throws AssertionCreationException, PolicyException {
        if (parameterNode.getType() != ModelNode.Type.ASSERTION_PARAMETER_NODE) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0065_INCONSISTENCY_IN_POLICY_SOURCE_MODEL((Object)parameterNode.getType())));
        }
        ArrayList<PolicyAssertion> childParameters = null;
        if (parameterNode.hasChildren()) {
            childParameters = new ArrayList<PolicyAssertion>(parameterNode.childrenSize());
            for (ModelNode childParameterNode : parameterNode) {
                childParameters.add(this.createPolicyAssertionParameter(childParameterNode));
            }
        }
        return this.createPolicyAssertion(parameterNode.getNodeData(), childParameters, null);
    }

    private PolicyAssertion createPolicyAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) throws AssertionCreationException {
        String assertionNamespace = data.getName().getNamespaceURI();
        PolicyAssertionCreator domainSpecificPAC = this.assertionCreators.get(assertionNamespace);
        if (domainSpecificPAC == null) {
            return defaultCreator.createAssertion(data, assertionParameters, nestedAlternative, null);
        }
        return domainSpecificPAC.createAssertion(data, assertionParameters, nestedAlternative, defaultCreator);
    }

    private static final class RawPolicy {
        final Collection<ModelNode> originalContent;
        final Collection<RawAlternative> alternatives;

        RawPolicy(ModelNode policyNode, Collection<RawAlternative> alternatives) {
            this.originalContent = policyNode.getChildren();
            this.alternatives = alternatives;
        }
    }

    private static final class RawAlternative {
        private static final PolicyLogger LOGGER = PolicyLogger.getLogger(RawAlternative.class);
        final List<RawPolicy> allNestedPolicies = new LinkedList<RawPolicy>();
        final Collection<RawAssertion> nestedAssertions = new LinkedList<RawAssertion>();

        RawAlternative(Collection<ModelNode> assertionNodes) throws PolicyException {
            for (ModelNode node : assertionNodes) {
                RawAssertion assertion = new RawAssertion(node, new LinkedList<ModelNode>());
                this.nestedAssertions.add(assertion);
                block5: for (ModelNode assertionNodeChild : assertion.originalNode.getChildren()) {
                    switch (assertionNodeChild.getType()) {
                        case ASSERTION_PARAMETER_NODE: {
                            assertion.parameters.add(assertionNodeChild);
                            continue block5;
                        }
                        case POLICY: 
                        case POLICY_REFERENCE: {
                            if (assertion.nestedAlternatives == null) {
                                assertion.nestedAlternatives = new LinkedList<RawAlternative>();
                                RawPolicy nestedPolicy = assertionNodeChild.getType() == ModelNode.Type.POLICY ? new RawPolicy(assertionNodeChild, assertion.nestedAlternatives) : new RawPolicy(PolicyModelTranslator.getReferencedModelRootNode(assertionNodeChild), assertion.nestedAlternatives);
                                this.allNestedPolicies.add(nestedPolicy);
                                continue block5;
                            }
                            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0006_UNEXPECTED_MULTIPLE_POLICY_NODES()));
                        }
                    }
                    throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0008_UNEXPECTED_CHILD_MODEL_TYPE((Object)assertionNodeChild.getType())));
                }
            }
        }
    }

    private static final class RawAssertion {
        ModelNode originalNode;
        Collection<RawAlternative> nestedAlternatives = null;
        final Collection<ModelNode> parameters;

        RawAssertion(ModelNode originalNode, Collection<ModelNode> parameters) {
            this.parameters = parameters;
            this.originalNode = originalNode;
        }
    }

    private static final class ContentDecomposition {
        final List<Collection<ModelNode>> exactlyOneContents = new LinkedList<Collection<ModelNode>>();
        final List<ModelNode> assertions = new LinkedList<ModelNode>();

        private ContentDecomposition() {
        }

        void reset() {
            this.exactlyOneContents.clear();
            this.assertions.clear();
        }
    }
}

