/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.language;

import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import java.util.List;

public interface StringListOperation
extends PolicyOperation {
    public List<String> apply(List<String> var1) throws PolicyViolationException;
}

