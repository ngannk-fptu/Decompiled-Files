/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.UpdateCustomKeyStoreResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class UpdateCustomKeyStoreResultJsonUnmarshaller
implements Unmarshaller<UpdateCustomKeyStoreResult, JsonUnmarshallerContext> {
    private static UpdateCustomKeyStoreResultJsonUnmarshaller instance;

    @Override
    public UpdateCustomKeyStoreResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        UpdateCustomKeyStoreResult updateCustomKeyStoreResult = new UpdateCustomKeyStoreResult();
        return updateCustomKeyStoreResult;
    }

    public static UpdateCustomKeyStoreResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new UpdateCustomKeyStoreResultJsonUnmarshaller();
        }
        return instance;
    }
}

