/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONUtils;
import com.nimbusds.openid.connect.sdk.federation.policy.language.OperationName;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringConfiguration;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringListOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.AbstractSetBasedOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.ConfigurationType;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.Utils;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AddOperation
extends AbstractSetBasedOperation
implements StringConfiguration,
StringListOperation {
    public static final OperationName NAME = new OperationName("add");

    @Override
    public OperationName getOperationName() {
        return NAME;
    }

    @Override
    public void configure(String parameter) {
        this.configType = ConfigurationType.STRING;
        this.configure((List)Collections.singletonList(parameter));
    }

    @Override
    public void parseConfiguration(Object jsonEntity) throws ParseException {
        if (jsonEntity instanceof String) {
            this.configure(JSONUtils.toString(jsonEntity));
        } else {
            super.parseConfiguration(jsonEntity);
        }
    }

    @Override
    public Map.Entry<String, Object> toJSONObjectEntry() {
        List value;
        if (this.configType == null) {
            throw new IllegalStateException("The policy is not initialized");
        }
        if (this.configType.equals((Object)ConfigurationType.STRING_LIST)) {
            value = this.getStringListConfiguration().size() > 1 ? this.getStringListConfiguration() : this.getStringListConfiguration().get(0);
        } else if (this.configType.equals((Object)ConfigurationType.STRING)) {
            value = this.getStringConfiguration();
        } else {
            throw new IllegalStateException("Unsupported configuration type: " + (Object)((Object)this.configType));
        }
        return new AbstractMap.SimpleImmutableEntry<String, Object>(this.getOperationName().getValue(), value);
    }

    @Override
    public String getStringConfiguration() {
        return (String)this.getStringListConfiguration().get(0);
    }

    @Override
    public PolicyOperation merge(PolicyOperation other) throws PolicyViolationException {
        AddOperation otherTyped = Utils.castForMerge(other, AddOperation.class);
        LinkedList combined = new LinkedList();
        combined.addAll(this.getStringListConfiguration());
        combined.addAll(otherTyped.getStringListConfiguration());
        AddOperation mergedPolicy = new AddOperation();
        mergedPolicy.configure((List)combined);
        return mergedPolicy;
    }

    @Override
    public List<String> apply(List<String> value) {
        if (this.setConfig == null) {
            throw new IllegalStateException("The policy is not initialized");
        }
        if (value == null) {
            return Collections.unmodifiableList(new LinkedList(this.setConfig));
        }
        LinkedList<String> result = new LinkedList<String>(value);
        result.addAll(this.setConfig);
        return result;
    }
}

