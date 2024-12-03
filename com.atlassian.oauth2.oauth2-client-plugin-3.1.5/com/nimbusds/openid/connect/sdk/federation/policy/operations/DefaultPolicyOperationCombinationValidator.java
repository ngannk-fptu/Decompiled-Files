/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.openid.connect.sdk.federation.policy.language.OperationName;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.DefaultOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.EssentialOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.OneOfOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.PolicyOperationCombinationValidator;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.SubsetOfOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.SupersetOfOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.Utils;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.ValueOperation;
import java.util.LinkedList;
import java.util.List;

public class DefaultPolicyOperationCombinationValidator
implements PolicyOperationCombinationValidator {
    @Override
    public List<PolicyOperation> validate(List<PolicyOperation> policyOperations) throws PolicyViolationException {
        if (CollectionUtils.isEmpty(policyOperations) || policyOperations.size() == 1) {
            return policyOperations;
        }
        List<PolicyOperation> currentOpList = new LinkedList<PolicyOperation>(policyOperations);
        currentOpList = DefaultPolicyOperationCombinationValidator.validateCombinationsOfEssential(currentOpList);
        currentOpList = DefaultPolicyOperationCombinationValidator.validateCombinationsOfAdd(currentOpList);
        currentOpList = DefaultPolicyOperationCombinationValidator.validateCombinationsOfDefault(currentOpList);
        currentOpList = DefaultPolicyOperationCombinationValidator.validateCombinationsOfSupersetOf(currentOpList);
        currentOpList = DefaultPolicyOperationCombinationValidator.validateCombinationsOfSubsetOf(currentOpList);
        currentOpList = DefaultPolicyOperationCombinationValidator.validateCombinationsOfValue(currentOpList);
        return currentOpList;
    }

    private static List<PolicyOperation> validateCombinationsOfEssential(List<PolicyOperation> ops) {
        return ops;
    }

    private static List<PolicyOperation> validateCombinationsOfAdd(List<PolicyOperation> ops) {
        return ops;
    }

    private static List<PolicyOperation> validateCombinationsOfDefault(List<PolicyOperation> ops) throws PolicyViolationException {
        DefaultOperation o = Utils.getPolicyOperationByType(ops, DefaultOperation.class);
        if (o == null) {
            return ops;
        }
        if (o.getStringListConfiguration() != null) {
            DefaultPolicyOperationCombinationValidator.ensureSatisfiedBySubsetOf(ops, o.getStringListConfiguration());
            DefaultPolicyOperationCombinationValidator.ensureSatisfiedBySupersetOf(ops, o.getStringListConfiguration());
        } else if (o.getStringConfiguration() != null) {
            DefaultPolicyOperationCombinationValidator.ensureSatisfiedByOneOf(ops, o.getStringConfiguration());
        }
        if (Utils.getPolicyOperationByType(ops, ValueOperation.class) != null) {
            throw new PolicyViolationException("Policies default and value cannot be combined");
        }
        return ops;
    }

    private static List<PolicyOperation> validateCombinationsOfSupersetOf(List<PolicyOperation> ops) throws PolicyViolationException {
        SupersetOfOperation o = Utils.getPolicyOperationByType(ops, SupersetOfOperation.class);
        if (o == null) {
            return ops;
        }
        SubsetOfOperation subsetOfOperation = Utils.getPolicyOperationByType(ops, SubsetOfOperation.class);
        if (subsetOfOperation != null) {
            DefaultPolicyOperationCombinationValidator.ensureSatisfied(o, (List<String>)subsetOfOperation.getStringListConfiguration());
        }
        return ops;
    }

    private static List<PolicyOperation> validateCombinationsOfSubsetOf(List<PolicyOperation> ops) throws PolicyViolationException {
        SubsetOfOperation o = Utils.getPolicyOperationByType(ops, SubsetOfOperation.class);
        if (o == null) {
            return ops;
        }
        SupersetOfOperation supersetOfOperation = Utils.getPolicyOperationByType(ops, SupersetOfOperation.class);
        if (supersetOfOperation != null) {
            DefaultPolicyOperationCombinationValidator.ensureSatisfied(supersetOfOperation, (List<String>)o.getStringListConfiguration());
        }
        return ops;
    }

    private static List<PolicyOperation> validateCombinationsOfValue(List<PolicyOperation> ops) throws PolicyViolationException {
        ValueOperation o = Utils.getPolicyOperationByType(ops, ValueOperation.class);
        if (o == null) {
            return ops;
        }
        LinkedList<OperationName> violating = new LinkedList<OperationName>();
        for (PolicyOperation op : ops) {
            if (op instanceof ValueOperation || op instanceof EssentialOperation) continue;
            violating.add(op.getOperationName());
        }
        if (!violating.isEmpty()) {
            throw new PolicyViolationException("Policy operation " + ValueOperation.NAME + " must not be combined with: " + violating);
        }
        return ops;
    }

    private static void ensureSatisfied(SubsetOfOperation op, List<String> values) throws PolicyViolationException {
        if (!op.getStringListConfiguration().containsAll(values)) {
            throw new PolicyViolationException("Not in " + SubsetOfOperation.NAME + " " + op.getStringListConfiguration() + ": " + values);
        }
    }

    private static void ensureSatisfiedBySubsetOf(List<PolicyOperation> policyOperations, List<String> values) throws PolicyViolationException {
        SubsetOfOperation op = Utils.getPolicyOperationByType(policyOperations, SubsetOfOperation.class);
        if (op != null) {
            DefaultPolicyOperationCombinationValidator.ensureSatisfied(op, values);
        }
    }

    private static void ensureSatisfied(SupersetOfOperation op, List<String> values) throws PolicyViolationException {
        if (!values.containsAll(op.getStringListConfiguration())) {
            throw new PolicyViolationException("Not in " + SupersetOfOperation.NAME + " " + op.getStringListConfiguration() + ": " + values);
        }
    }

    private static void ensureSatisfiedBySupersetOf(List<PolicyOperation> policyOperations, List<String> values) throws PolicyViolationException {
        SupersetOfOperation op = Utils.getPolicyOperationByType(policyOperations, SupersetOfOperation.class);
        if (op != null) {
            DefaultPolicyOperationCombinationValidator.ensureSatisfied(op, values);
        }
    }

    private static void ensureSatisfiedByOneOf(List<PolicyOperation> policyOperations, String value) throws PolicyViolationException {
        OneOfOperation op = Utils.getPolicyOperationByType(policyOperations, OneOfOperation.class);
        if (op == null) {
            return;
        }
        if (!op.getStringListConfiguration().contains(value)) {
            throw new PolicyViolationException("Not in " + OneOfOperation.NAME + " " + op.getStringListConfiguration() + ": " + value);
        }
    }
}

