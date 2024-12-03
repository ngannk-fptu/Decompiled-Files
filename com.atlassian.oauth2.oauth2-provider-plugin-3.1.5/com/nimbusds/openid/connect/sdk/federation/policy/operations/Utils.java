/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.operations;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import java.util.List;

class Utils {
    static <T> T castForMerge(PolicyOperation op, Class<T> clazz) throws PolicyViolationException {
        try {
            return (T)op;
        }
        catch (ClassCastException e) {
            throw new PolicyViolationException("The policy must be " + clazz.getName());
        }
    }

    static <T extends PolicyOperation> T getPolicyOperationByType(List<PolicyOperation> opList, Class<T> clazz) {
        if (CollectionUtils.isEmpty(opList)) {
            return null;
        }
        for (PolicyOperation op : opList) {
            if (!clazz.isAssignableFrom(op.getClass())) continue;
            return (T)op;
        }
        return null;
    }

    private Utils() {
    }
}

