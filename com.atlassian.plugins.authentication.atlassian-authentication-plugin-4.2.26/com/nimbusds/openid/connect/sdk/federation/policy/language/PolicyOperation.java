/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.language;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.federation.policy.language.OperationName;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import java.util.Map;

public interface PolicyOperation {
    public OperationName getOperationName();

    public void parseConfiguration(Object var1) throws ParseException;

    public Map.Entry<String, Object> toJSONObjectEntry();

    public PolicyOperation merge(PolicyOperation var1) throws PolicyViolationException;
}

