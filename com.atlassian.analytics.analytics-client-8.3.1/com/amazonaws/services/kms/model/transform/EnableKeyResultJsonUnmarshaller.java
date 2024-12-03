/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.EnableKeyResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class EnableKeyResultJsonUnmarshaller
implements Unmarshaller<EnableKeyResult, JsonUnmarshallerContext> {
    private static EnableKeyResultJsonUnmarshaller instance;

    @Override
    public EnableKeyResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        EnableKeyResult enableKeyResult = new EnableKeyResult();
        return enableKeyResult;
    }

    public static EnableKeyResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new EnableKeyResultJsonUnmarshaller();
        }
        return instance;
    }
}

