/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.DisableKeyResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class DisableKeyResultJsonUnmarshaller
implements Unmarshaller<DisableKeyResult, JsonUnmarshallerContext> {
    private static DisableKeyResultJsonUnmarshaller instance;

    @Override
    public DisableKeyResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        DisableKeyResult disableKeyResult = new DisableKeyResult();
        return disableKeyResult;
    }

    public static DisableKeyResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DisableKeyResultJsonUnmarshaller();
        }
        return instance;
    }
}

