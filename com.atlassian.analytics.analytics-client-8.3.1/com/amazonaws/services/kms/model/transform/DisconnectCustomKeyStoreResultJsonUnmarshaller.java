/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.DisconnectCustomKeyStoreResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class DisconnectCustomKeyStoreResultJsonUnmarshaller
implements Unmarshaller<DisconnectCustomKeyStoreResult, JsonUnmarshallerContext> {
    private static DisconnectCustomKeyStoreResultJsonUnmarshaller instance;

    @Override
    public DisconnectCustomKeyStoreResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        DisconnectCustomKeyStoreResult disconnectCustomKeyStoreResult = new DisconnectCustomKeyStoreResult();
        return disconnectCustomKeyStoreResult;
    }

    public static DisconnectCustomKeyStoreResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DisconnectCustomKeyStoreResultJsonUnmarshaller();
        }
        return instance;
    }
}

