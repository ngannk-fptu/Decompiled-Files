/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.federation.policy.MetadataPolicyEntry;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.PolicyOperationCombinationValidator;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.PolicyOperationFactory;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

public class MetadataPolicy
implements JSONAware {
    private final Map<String, List<PolicyOperation>> entries = new LinkedHashMap<String, List<PolicyOperation>>();

    public JSONObject apply(JSONObject metadata) throws PolicyViolationException {
        if (metadata == null) {
            return null;
        }
        JSONObject out = new JSONObject();
        for (String key : metadata.keySet()) {
            if (this.entries.containsKey(key)) continue;
            out.put(key, metadata.get(key));
        }
        for (String key : this.entries.keySet()) {
            Object metadataValue = metadata.get(key);
            MetadataPolicyEntry en = this.getEntry(key);
            Object outputValue = en.apply(metadataValue);
            if (outputValue == null) continue;
            out.put(key, outputValue);
        }
        return out;
    }

    public void put(String parameterName, PolicyOperation policyOperation) {
        this.put(new MetadataPolicyEntry(parameterName, Collections.singletonList(policyOperation)));
    }

    public void put(String parameterName, List<PolicyOperation> policyOperations) {
        this.put(new MetadataPolicyEntry(parameterName, policyOperations));
    }

    public void put(MetadataPolicyEntry entry) {
        this.entries.put(entry.getKey(), (List<PolicyOperation>)entry.getValue());
    }

    public List<PolicyOperation> get(String parameterName) {
        return this.entries.get(parameterName);
    }

    public MetadataPolicyEntry getEntry(String parameterName) {
        List<PolicyOperation> policyOperations = this.entries.get(parameterName);
        if (policyOperations == null) {
            return null;
        }
        return new MetadataPolicyEntry(parameterName, policyOperations);
    }

    public Set<MetadataPolicyEntry> entrySet() {
        LinkedHashSet<MetadataPolicyEntry> set = new LinkedHashSet<MetadataPolicyEntry>();
        for (Map.Entry<String, List<PolicyOperation>> en : this.entries.entrySet()) {
            set.add(new MetadataPolicyEntry(en.getKey(), en.getValue()));
        }
        return set;
    }

    public List<PolicyOperation> remove(String parameterName) {
        return this.entries.remove(parameterName);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        for (MetadataPolicyEntry en : this.entrySet()) {
            JSONObject policyEntryJSONObject = en.toJSONObject();
            if (policyEntryJSONObject == null) continue;
            jsonObject.put(en.getKey(), en.toJSONObject());
        }
        return jsonObject;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public String toString() {
        return this.toJSONString();
    }

    public static MetadataPolicy combine(List<MetadataPolicy> policies) throws PolicyViolationException {
        return MetadataPolicy.combine(policies, MetadataPolicyEntry.DEFAULT_POLICY_COMBINATION_VALIDATOR);
    }

    public static MetadataPolicy combine(List<MetadataPolicy> policies, PolicyOperationCombinationValidator combinationValidator) throws PolicyViolationException {
        MetadataPolicy out = new MetadataPolicy();
        for (MetadataPolicy p : policies) {
            for (MetadataPolicyEntry entry : p.entrySet()) {
                MetadataPolicyEntry existingEntry = out.getEntry(entry.getParameterName());
                if (existingEntry == null) {
                    out.put(entry);
                    continue;
                }
                out.put(existingEntry.combine(entry, combinationValidator));
            }
        }
        return out;
    }

    public static MetadataPolicy parse(JSONObject policySpec) throws ParseException, PolicyViolationException {
        return MetadataPolicy.parse(policySpec, MetadataPolicyEntry.DEFAULT_POLICY_OPERATION_FACTORY, MetadataPolicyEntry.DEFAULT_POLICY_COMBINATION_VALIDATOR);
    }

    public static MetadataPolicy parse(JSONObject policySpec, PolicyOperationFactory factory, PolicyOperationCombinationValidator combinationValidator) throws ParseException, PolicyViolationException {
        MetadataPolicy metadataPolicy = new MetadataPolicy();
        for (String parameterName : policySpec.keySet()) {
            JSONObject entrySpec = JSONObjectUtils.getJSONObject(policySpec, parameterName);
            metadataPolicy.put(MetadataPolicyEntry.parse(parameterName, entrySpec, factory, combinationValidator));
        }
        return metadataPolicy;
    }

    public static MetadataPolicy parse(String policySpec) throws ParseException, PolicyViolationException {
        return MetadataPolicy.parse(policySpec, MetadataPolicyEntry.DEFAULT_POLICY_OPERATION_FACTORY, MetadataPolicyEntry.DEFAULT_POLICY_COMBINATION_VALIDATOR);
    }

    public static MetadataPolicy parse(String policySpec, PolicyOperationFactory factory, PolicyOperationCombinationValidator combinationValidator) throws ParseException, PolicyViolationException {
        return MetadataPolicy.parse(JSONObjectUtils.parse(policySpec), factory, combinationValidator);
    }
}

