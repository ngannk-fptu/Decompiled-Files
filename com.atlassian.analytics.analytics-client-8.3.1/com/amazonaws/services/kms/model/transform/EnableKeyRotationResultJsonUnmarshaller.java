/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.EnableKeyRotationResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class EnableKeyRotationResultJsonUnmarshaller
implements Unmarshaller<EnableKeyRotationResult, JsonUnmarshallerContext> {
    private static EnableKeyRotationResultJsonUnmarshaller instance;

    @Override
    public EnableKeyRotationResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        EnableKeyRotationResult enableKeyRotationResult = new EnableKeyRotationResult();
        return enableKeyRotationResult;
    }

    public static EnableKeyRotationResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new EnableKeyRotationResultJsonUnmarshaller();
        }
        return instance;
    }
}

