/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.federation.policy.language.OperationName;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperationApplication;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.DefaultPolicyOperationCombinationValidator;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.DefaultPolicyOperationFactory;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.PolicyOperationCombinationValidator;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.PolicyOperationFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONObject;

public class MetadataPolicyEntry
implements Map.Entry<String, List<PolicyOperation>> {
    public static final PolicyOperationFactory DEFAULT_POLICY_OPERATION_FACTORY = new DefaultPolicyOperationFactory();
    public static final PolicyOperationCombinationValidator DEFAULT_POLICY_COMBINATION_VALIDATOR = new DefaultPolicyOperationCombinationValidator();
    private final String parameterName;
    private final List<PolicyOperation> policyOperations;

    public MetadataPolicyEntry(String parameterName, List<PolicyOperation> policyOperations) {
        if (StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("The parameter name must not be null or empty");
        }
        this.parameterName = parameterName;
        this.policyOperations = policyOperations;
    }

    public String getParameterName() {
        return this.getKey();
    }

    @Override
    public String getKey() {
        return this.parameterName;
    }

    public List<PolicyOperation> getPolicyOperations() {
        return this.getValue();
    }

    @Override
    public List<PolicyOperation> getValue() {
        return this.policyOperations;
    }

    @Override
    public List<PolicyOperation> setValue(List<PolicyOperation> policyOperations) {
        throw new UnsupportedOperationException();
    }

    public Map<OperationName, PolicyOperation> getOperationsMap() {
        HashMap<OperationName, PolicyOperation> map = new HashMap<OperationName, PolicyOperation>();
        if (this.getPolicyOperations() == null) {
            return map;
        }
        for (PolicyOperation op : this.getPolicyOperations()) {
            map.put(op.getOperationName(), op);
        }
        return map;
    }

    public MetadataPolicyEntry combine(MetadataPolicyEntry other) throws PolicyViolationException {
        return this.combine(other, DEFAULT_POLICY_COMBINATION_VALIDATOR);
    }

    public MetadataPolicyEntry combine(MetadataPolicyEntry other, PolicyOperationCombinationValidator combinationValidator) throws PolicyViolationException {
        if (!this.getParameterName().equals(other.getParameterName())) {
            throw new PolicyViolationException("The parameter name of the other policy doesn't match: " + other.getParameterName());
        }
        LinkedList<PolicyOperation> combinedOperations = new LinkedList<PolicyOperation>();
        Map<OperationName, PolicyOperation> en1Map = this.getOperationsMap();
        Map<OperationName, PolicyOperation> en2Map = other.getOperationsMap();
        for (OperationName name : en1Map.keySet()) {
            if (en2Map.containsKey(name)) continue;
            combinedOperations.add(en1Map.get(name));
        }
        for (OperationName name : en2Map.keySet()) {
            if (en1Map.containsKey(name)) continue;
            combinedOperations.add(en2Map.get(name));
        }
        for (OperationName opName : en1Map.keySet()) {
            if (!en2Map.containsKey(opName)) continue;
            PolicyOperation op1 = en1Map.get(opName);
            combinedOperations.add(op1.merge(en2Map.get(opName)));
        }
        List<PolicyOperation> validatedOperations = combinationValidator.validate(combinedOperations);
        return new MetadataPolicyEntry(this.getParameterName(), validatedOperations);
    }

    public Object apply(Object value) throws PolicyViolationException {
        if (CollectionUtils.isEmpty(this.getValue())) {
            return value;
        }
        Object updatedValue = value;
        Iterator iterator = this.getValue().iterator();
        while (iterator.hasNext()) {
            PolicyOperation op = (PolicyOperation)iterator.next();
            updatedValue = PolicyOperationApplication.apply(op, updatedValue);
        }
        return updatedValue;
    }

    public JSONObject toJSONObject() {
        if (CollectionUtils.isEmpty(this.getValue())) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        Iterator iterator = this.getValue().iterator();
        while (iterator.hasNext()) {
            PolicyOperation operation = (PolicyOperation)iterator.next();
            Map.Entry<String, Object> en = operation.toJSONObjectEntry();
            jsonObject.put(en.getKey(), en.getValue());
        }
        return jsonObject;
    }

    public static MetadataPolicyEntry parse(String parameterName, JSONObject entrySpec) throws ParseException, PolicyViolationException {
        return MetadataPolicyEntry.parse(parameterName, entrySpec, DEFAULT_POLICY_OPERATION_FACTORY, DEFAULT_POLICY_COMBINATION_VALIDATOR);
    }

    public static MetadataPolicyEntry parse(String parameterName, JSONObject entrySpec, PolicyOperationFactory factory, PolicyOperationCombinationValidator combinationValidator) throws ParseException, PolicyViolationException {
        if (entrySpec == null) {
            throw new IllegalArgumentException("The entry spec must not be null");
        }
        LinkedList<PolicyOperation> policyOperations = new LinkedList<PolicyOperation>();
        for (String opName : entrySpec.keySet()) {
            PolicyOperation op = factory.createForName(new OperationName(opName));
            if (op == null) {
                throw new PolicyViolationException("Unsupported policy operation: " + opName);
            }
            op.parseConfiguration(entrySpec.get(opName));
            policyOperations.add(op);
        }
        List<PolicyOperation> validatedPolicyOperations = combinationValidator.validate(policyOperations);
        return new MetadataPolicyEntry(parameterName, validatedPolicyOperations);
    }
}

