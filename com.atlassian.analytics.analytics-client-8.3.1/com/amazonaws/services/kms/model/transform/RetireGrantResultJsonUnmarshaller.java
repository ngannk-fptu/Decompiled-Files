/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.RetireGrantResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class RetireGrantResultJsonUnmarshaller
implements Unmarshaller<RetireGrantResult, JsonUnmarshallerContext> {
    private static RetireGrantResultJsonUnmarshaller instance;

    @Override
    public RetireGrantResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        RetireGrantResult retireGrantResult = new RetireGrantResult();
        return retireGrantResult;
    }

    public static RetireGrantResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new RetireGrantResultJsonUnmarshaller();
        }
        return instance;
    }
}

