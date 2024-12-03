/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.DeleteCustomKeyStoreResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class DeleteCustomKeyStoreResultJsonUnmarshaller
implements Unmarshaller<DeleteCustomKeyStoreResult, JsonUnmarshallerContext> {
    private static DeleteCustomKeyStoreResultJsonUnmarshaller instance;

    @Override
    public DeleteCustomKeyStoreResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        DeleteCustomKeyStoreResult deleteCustomKeyStoreResult = new DeleteCustomKeyStoreResult();
        return deleteCustomKeyStoreResult;
    }

    public static DeleteCustomKeyStoreResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DeleteCustomKeyStoreResultJsonUnmarshaller();
        }
        return instance;
    }
}

