/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.openid.connect.sdk.federation.policy.language.OperationName;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringListConfiguration;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringListOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.AbstractSetBasedOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.Utils;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SupersetOfOperation
extends AbstractSetBasedOperation
implements PolicyOperation,
StringListConfiguration,
StringListOperation {
    public static final OperationName NAME = new OperationName("superset_of");

    @Override
    public OperationName getOperationName() {
        return NAME;
    }

    @Override
    public Map.Entry<String, Object> toJSONObjectEntry() {
        if (this.configType == null) {
            throw new IllegalStateException("The policy is not initialized");
        }
        return new AbstractMap.SimpleImmutableEntry<String, Object>(this.getOperationName().getValue(), this.getStringListConfiguration());
    }

    @Override
    public PolicyOperation merge(PolicyOperation other) throws PolicyViolationException {
        SupersetOfOperation otherTyped = Utils.castForMerge(other, SupersetOfOperation.class);
        LinkedHashSet combinedConfig = new LinkedHashSet(this.setConfig);
        combinedConfig.retainAll(otherTyped.getStringListConfiguration());
        SupersetOfOperation mergedPolicy = new SupersetOfOperation();
        mergedPolicy.configure((List)new LinkedList(combinedConfig));
        return mergedPolicy;
    }

    @Override
    public List<String> apply(List<String> stringList) throws PolicyViolationException {
        if (this.setConfig == null) {
            throw new IllegalStateException("The policy is not initialized");
        }
        if (stringList == null) {
            throw new PolicyViolationException("Value not specified");
        }
        LinkedList<String> missingValues = new LinkedList<String>();
        for (String requiredValue : this.setConfig) {
            if (stringList.contains(requiredValue)) continue;
            missingValues.add(requiredValue);
        }
        if (!missingValues.isEmpty()) {
            throw new PolicyViolationException("Missing values: " + missingValues);
        }
        return Collections.unmodifiableList(stringList);
    }
}

