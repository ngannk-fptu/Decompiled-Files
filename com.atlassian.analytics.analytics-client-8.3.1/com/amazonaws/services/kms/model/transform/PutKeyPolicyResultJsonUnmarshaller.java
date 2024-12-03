/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.PutKeyPolicyResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class PutKeyPolicyResultJsonUnmarshaller
implements Unmarshaller<PutKeyPolicyResult, JsonUnmarshallerContext> {
    private static PutKeyPolicyResultJsonUnmarshaller instance;

    @Override
    public PutKeyPolicyResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        PutKeyPolicyResult putKeyPolicyResult = new PutKeyPolicyResult();
        return putKeyPolicyResult;
    }

    public static PutKeyPolicyResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new PutKeyPolicyResultJsonUnmarshaller();
        }
        return instance;
    }
}

