/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONUtils;
import com.nimbusds.openid.connect.sdk.federation.policy.language.BooleanConfiguration;
import com.nimbusds.openid.connect.sdk.federation.policy.language.OperationName;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringConfiguration;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringListConfiguration;
import com.nimbusds.openid.connect.sdk.federation.policy.language.UntypedOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.ConfigurationType;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.Utils;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class DefaultOperation
implements PolicyOperation,
BooleanConfiguration,
StringConfiguration,
StringListConfiguration,
UntypedOperation {
    public static final OperationName NAME = new OperationName("default");
    private ConfigurationType configType;
    private boolean booleanValue;
    private String stringValue;
    private List<String> stringListValue;

    @Override
    public OperationName getOperationName() {
        return NAME;
    }

    @Override
    public void configure(boolean parameter) {
        this.configType = ConfigurationType.BOOLEAN;
        this.booleanValue = parameter;
    }

    @Override
    public void configure(String parameter) {
        this.configType = ConfigurationType.STRING;
        this.stringValue = parameter;
    }

    @Override
    public void configure(List<String> parameter) {
        this.configType = ConfigurationType.STRING_LIST;
        this.stringListValue = parameter;
    }

    @Override
    public void parseConfiguration(Object jsonEntity) throws ParseException {
        if (jsonEntity instanceof Boolean) {
            this.configure(JSONUtils.toBoolean(jsonEntity));
        } else if (jsonEntity instanceof String) {
            this.configure(JSONUtils.toString(jsonEntity));
        } else {
            this.configure(JSONUtils.toStringList(jsonEntity));
        }
    }

    @Override
    public Map.Entry<String, Object> toJSONObjectEntry() {
        Object value;
        if (this.configType == null) {
            throw new IllegalStateException("The policy is not initialized");
        }
        if (this.configType.equals((Object)ConfigurationType.BOOLEAN)) {
            value = this.getBooleanConfiguration();
        } else if (this.configType.equals((Object)ConfigurationType.STRING_LIST)) {
            value = this.getStringListConfiguration();
        } else if (this.configType.equals((Object)ConfigurationType.STRING)) {
            value = this.getStringConfiguration();
        } else {
            throw new IllegalStateException("Unsupported configuration type: " + (Object)((Object)this.configType));
        }
        return new AbstractMap.SimpleImmutableEntry<String, Object>(this.getOperationName().getValue(), value);
    }

    @Override
    public boolean getBooleanConfiguration() {
        return this.booleanValue;
    }

    @Override
    public String getStringConfiguration() {
        return this.stringValue;
    }

    @Override
    public List<String> getStringListConfiguration() {
        return this.stringListValue;
    }

    @Override
    public PolicyOperation merge(PolicyOperation other) throws PolicyViolationException {
        DefaultOperation otherTyped = Utils.castForMerge(other, DefaultOperation.class);
        if (this.configType == null || otherTyped.configType == null) {
            throw new PolicyViolationException("The default operation is not initialized");
        }
        if (this.configType.equals((Object)ConfigurationType.STRING_LIST) && this.getStringListConfiguration() != null && this.getStringListConfiguration().equals(otherTyped.getStringListConfiguration())) {
            DefaultOperation copy = new DefaultOperation();
            copy.configure(this.getStringListConfiguration());
            return copy;
        }
        if (this.configType.equals((Object)ConfigurationType.STRING) && this.getStringConfiguration() != null && this.getStringConfiguration().equals(otherTyped.getStringConfiguration())) {
            DefaultOperation copy = new DefaultOperation();
            copy.configure(this.getStringConfiguration());
            return copy;
        }
        if (this.configType.equals((Object)ConfigurationType.BOOLEAN) && this.getBooleanConfiguration() == otherTyped.getBooleanConfiguration()) {
            DefaultOperation copy = new DefaultOperation();
            copy.configure(this.getBooleanConfiguration());
            return copy;
        }
        throw new PolicyViolationException("Default value mismatch");
    }

    @Override
    public Object apply(Object value) {
        if (this.configType == null) {
            throw new IllegalStateException("The policy is not initialized");
        }
        if (value != null) {
            return value;
        }
        if (this.stringListValue != null) {
            return this.stringListValue;
        }
        if (this.stringValue != null) {
            return this.stringValue;
        }
        return this.booleanValue;
    }
}

