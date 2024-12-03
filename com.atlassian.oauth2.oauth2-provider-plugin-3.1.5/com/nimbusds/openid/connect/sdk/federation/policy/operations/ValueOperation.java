/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONUtils;
import com.nimbusds.openid.connect.sdk.federation.policy.language.BooleanConfiguration;
import com.nimbusds.openid.connect.sdk.federation.policy.language.NumberConfiguration;
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

public class ValueOperation
implements PolicyOperation,
BooleanConfiguration,
NumberConfiguration,
StringConfiguration,
StringListConfiguration,
UntypedOperation {
    public static final OperationName NAME = new OperationName("value");
    private ConfigurationType configType;
    private boolean booleanValue;
    private Number numberValue = null;
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
    public void configure(Number parameter) {
        this.configType = ConfigurationType.NUMBER;
        this.numberValue = parameter;
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
        } else if (jsonEntity instanceof Number) {
            this.configure(JSONUtils.toNumber(jsonEntity));
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
        } else if (this.configType.equals((Object)ConfigurationType.NUMBER)) {
            value = this.getNumberConfiguration();
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
    public Number getNumberConfiguration() {
        return this.numberValue;
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
        ValueOperation otherTyped = Utils.castForMerge(other, ValueOperation.class);
        if (this.configType == null || otherTyped.configType == null) {
            throw new PolicyViolationException("The value operation is not initialized");
        }
        if (this.configType.equals((Object)ConfigurationType.STRING_LIST) && this.getStringListConfiguration() != null && this.getStringListConfiguration().equals(otherTyped.getStringListConfiguration())) {
            ValueOperation copy = new ValueOperation();
            copy.configure(this.getStringListConfiguration());
            return copy;
        }
        if (this.configType.equals((Object)ConfigurationType.STRING) && this.getStringConfiguration() != null && this.getStringConfiguration().equals(otherTyped.getStringConfiguration())) {
            ValueOperation copy = new ValueOperation();
            copy.configure(this.getStringConfiguration());
            return copy;
        }
        if (this.configType.equals((Object)ConfigurationType.BOOLEAN) && this.getBooleanConfiguration() == otherTyped.getBooleanConfiguration()) {
            ValueOperation copy = new ValueOperation();
            copy.configure(this.getBooleanConfiguration());
            return copy;
        }
        if (this.configType.equals((Object)ConfigurationType.NUMBER) && this.getNumberConfiguration() != null && this.getNumberConfiguration().equals(otherTyped.getNumberConfiguration())) {
            ValueOperation copy = new ValueOperation();
            copy.configure(this.getNumberConfiguration());
            return copy;
        }
        throw new PolicyViolationException("Value mismatch");
    }

    @Override
    public Object apply(Object value) {
        if (this.configType == null) {
            throw new IllegalStateException("The policy is not initialized");
        }
        if (this.configType.equals((Object)ConfigurationType.BOOLEAN)) {
            return this.booleanValue;
        }
        if (this.configType.equals((Object)ConfigurationType.NUMBER)) {
            return this.numberValue;
        }
        if (this.configType.equals((Object)ConfigurationType.STRING)) {
            return this.stringValue;
        }
        return this.stringListValue;
    }
}

