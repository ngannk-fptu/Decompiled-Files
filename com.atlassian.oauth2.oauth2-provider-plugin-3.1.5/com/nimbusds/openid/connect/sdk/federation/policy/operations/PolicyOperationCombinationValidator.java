/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import java.util.List;

public interface PolicyOperationCombinationValidator {
    public List<PolicyOperation> validate(List<PolicyOperation> var1) throws PolicyViolationException;
}

