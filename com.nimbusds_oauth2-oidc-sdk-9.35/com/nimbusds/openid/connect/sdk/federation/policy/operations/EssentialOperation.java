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
import com.nimbusds.openid.connect.sdk.federation.policy.language.UntypedOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.operations.Utils;
import java.util.AbstractMap;
import java.util.Map;

public class EssentialOperation
implements PolicyOperation,
BooleanConfiguration,
UntypedOperation {
    public static final OperationName NAME = new OperationName("essential");
    private boolean enable = false;

    @Override
    public OperationName getOperationName() {
        return NAME;
    }

    @Override
    public void configure(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void parseConfiguration(Object jsonEntity) throws ParseException {
        this.configure(JSONUtils.toBoolean(jsonEntity));
    }

    @Override
    public Map.Entry<String, Object> toJSONObjectEntry() {
        return new AbstractMap.SimpleImmutableEntry<String, Object>(this.getOperationName().getValue(), this.getBooleanConfiguration());
    }

    @Override
    public boolean getBooleanConfiguration() {
        return this.enable;
    }

    @Override
    public PolicyOperation merge(PolicyOperation other) throws PolicyViolationException {
        EssentialOperation otherTyped = Utils.castForMerge(other, EssentialOperation.class);
        if (this.getBooleanConfiguration() == otherTyped.getBooleanConfiguration()) {
            EssentialOperation copy = new EssentialOperation();
            copy.configure(this.getBooleanConfiguration());
            return copy;
        }
        throw new PolicyViolationException("Essential value mismatch");
    }

    @Override
    public Object apply(Object value) throws PolicyViolationException {
        if (this.enable && value == null) {
            throw new PolicyViolationException("Essential parameter not present");
        }
        return value;
    }
}

