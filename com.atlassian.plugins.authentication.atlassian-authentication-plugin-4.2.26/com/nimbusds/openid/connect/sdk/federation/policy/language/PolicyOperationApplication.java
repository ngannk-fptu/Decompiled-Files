/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.language;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONUtils;
import com.nimbusds.openid.connect.sdk.federation.policy.language.BooleanOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringListOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.StringOperation;
import com.nimbusds.openid.connect.sdk.federation.policy.language.UntypedOperation;
import java.util.List;

public class PolicyOperationApplication {
    public static Object apply(PolicyOperation op, Object value) throws PolicyViolationException {
        if (op instanceof UntypedOperation) {
            return ((UntypedOperation)op).apply(value);
        }
        if (op instanceof BooleanOperation) {
            if (!(value instanceof Boolean)) {
                throw new PolicyViolationException("The value must be a boolean");
            }
            return ((BooleanOperation)op).apply((Boolean)value);
        }
        if (op instanceof StringOperation) {
            StringOperation stringOperation = (StringOperation)op;
            if (value == null) {
                return stringOperation.apply(null);
            }
            if (value instanceof String) {
                return stringOperation.apply((String)value);
            }
            throw new PolicyViolationException("The value must be a string");
        }
        if (op instanceof StringListOperation) {
            StringListOperation stringListOperation = (StringListOperation)op;
            if (value == null) {
                return stringListOperation.apply(null);
            }
            if (value instanceof List) {
                try {
                    return stringListOperation.apply(JSONUtils.toStringList(value));
                }
                catch (ParseException e) {
                    throw new PolicyViolationException("The value must be a string list", e);
                }
            }
            throw new PolicyViolationException("The value must be a string list");
        }
        throw new PolicyViolationException("Unsupported policy operation: " + op.getClass().getName());
    }

    private PolicyOperationApplication() {
    }
}

