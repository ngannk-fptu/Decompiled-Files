/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.ConnectCustomKeyStoreResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class ConnectCustomKeyStoreResultJsonUnmarshaller
implements Unmarshaller<ConnectCustomKeyStoreResult, JsonUnmarshallerContext> {
    private static ConnectCustomKeyStoreResultJsonUnmarshaller instance;

    @Override
    public ConnectCustomKeyStoreResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        ConnectCustomKeyStoreResult connectCustomKeyStoreResult = new ConnectCustomKeyStoreResult();
        return connectCustomKeyStoreResult;
    }

    public static ConnectCustomKeyStoreResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new ConnectCustomKeyStoreResultJsonUnmarshaller();
        }
        return instance;
    }
}

