/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.DisableKeyRotationResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class DisableKeyRotationResultJsonUnmarshaller
implements Unmarshaller<DisableKeyRotationResult, JsonUnmarshallerContext> {
    private static DisableKeyRotationResultJsonUnmarshaller instance;

    @Override
    public DisableKeyRotationResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        DisableKeyRotationResult disableKeyRotationResult = new DisableKeyRotationResult();
        return disableKeyRotationResult;
    }

    public static DisableKeyRotationResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DisableKeyRotationResultJsonUnmarshaller();
        }
        return instance;
    }
}

