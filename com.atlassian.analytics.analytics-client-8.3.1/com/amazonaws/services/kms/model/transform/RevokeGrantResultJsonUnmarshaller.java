/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.RevokeGrantResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class RevokeGrantResultJsonUnmarshaller
implements Unmarshaller<RevokeGrantResult, JsonUnmarshallerContext> {
    private static RevokeGrantResultJsonUnmarshaller instance;

    @Override
    public RevokeGrantResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        RevokeGrantResult revokeGrantResult = new RevokeGrantResult();
        return revokeGrantResult;
    }

    public static RevokeGrantResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new RevokeGrantResultJsonUnmarshaller();
        }
        return instance;
    }
}

