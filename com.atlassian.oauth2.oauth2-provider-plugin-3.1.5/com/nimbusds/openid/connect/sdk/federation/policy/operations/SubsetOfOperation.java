/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.openid.connect.sdk.federation.policy.language.OperationName;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringListOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.AbstractSetBasedOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.Utils;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SubsetOfOperation
extends AbstractSetBasedOperation
implements StringListOperation {
    public static final OperationName NAME = new OperationName("subset_of");

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
        SubsetOfOperation otherTyped = Utils.castForMerge(other, SubsetOfOperation.class);
        LinkedHashSet combinedConfig = new LinkedHashSet(this.setConfig);
        combinedConfig.retainAll(otherTyped.getStringListConfiguration());
        SubsetOfOperation mergedPolicy = new SubsetOfOperation();
        mergedPolicy.configure((List)new LinkedList(combinedConfig));
        return mergedPolicy;
    }

    @Override
    public List<String> apply(List<String> stringList) {
        if (this.setConfig == null) {
            throw new IllegalStateException("The policy is not initialized");
        }
        if (stringList == null) {
            return Collections.emptyList();
        }
        LinkedHashSet<String> setValue = new LinkedHashSet<String>(stringList);
        setValue.retainAll(this.setConfig);
        return Collections.unmodifiableList(new LinkedList<String>(setValue));
    }
}

