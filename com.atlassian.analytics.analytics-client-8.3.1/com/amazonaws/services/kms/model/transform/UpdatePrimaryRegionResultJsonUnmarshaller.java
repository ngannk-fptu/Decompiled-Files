/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.UpdatePrimaryRegionResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class UpdatePrimaryRegionResultJsonUnmarshaller
implements Unmarshaller<UpdatePrimaryRegionResult, JsonUnmarshallerContext> {
    private static UpdatePrimaryRegionResultJsonUnmarshaller instance;

    @Override
    public UpdatePrimaryRegionResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        UpdatePrimaryRegionResult updatePrimaryRegionResult = new UpdatePrimaryRegionResult();
        return updatePrimaryRegionResult;
    }

    public static UpdatePrimaryRegionResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new UpdatePrimaryRegionResultJsonUnmarshaller();
        }
        return instance;
    }
}

