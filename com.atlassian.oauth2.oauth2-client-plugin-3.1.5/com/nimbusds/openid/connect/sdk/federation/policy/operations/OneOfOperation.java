/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.openid.connect.sdk.federation.policy.language.OperationName;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.AbstractSetBasedOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.Utils;
import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OneOfOperation
extends AbstractSetBasedOperation
implements StringOperation {
    public static final OperationName NAME = new OperationName("one_of");

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
        OneOfOperation otherTyped = Utils.castForMerge(other, OneOfOperation.class);
        LinkedHashSet combinedConfig = new LinkedHashSet(this.setConfig);
        combinedConfig.retainAll(otherTyped.getStringListConfiguration());
        OneOfOperation mergedPolicy = new OneOfOperation();
        mergedPolicy.configure((List)new LinkedList(combinedConfig));
        return mergedPolicy;
    }

    @Override
    public String apply(String value) throws PolicyViolationException {
        if (this.setConfig == null) {
            throw new IllegalStateException("The policy is not initialized");
        }
        if (value == null) {
            throw new PolicyViolationException("Value not set");
        }
        if (!this.setConfig.contains(value)) {
            throw new PolicyViolationException("Value " + value + " not in policy list: " + this.setConfig);
        }
        return value;
    }
}

