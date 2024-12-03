/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.UpdateKeyDescriptionResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class UpdateKeyDescriptionResultJsonUnmarshaller
implements Unmarshaller<UpdateKeyDescriptionResult, JsonUnmarshallerContext> {
    private static UpdateKeyDescriptionResultJsonUnmarshaller instance;

    @Override
    public UpdateKeyDescriptionResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        UpdateKeyDescriptionResult updateKeyDescriptionResult = new UpdateKeyDescriptionResult();
        return updateKeyDescriptionResult;
    }

    public static UpdateKeyDescriptionResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new UpdateKeyDescriptionResultJsonUnmarshaller();
        }
        return instance;
    }
}

