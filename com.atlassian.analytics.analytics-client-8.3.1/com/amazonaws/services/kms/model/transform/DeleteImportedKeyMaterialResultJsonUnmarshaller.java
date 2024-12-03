/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.DeleteImportedKeyMaterialResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class DeleteImportedKeyMaterialResultJsonUnmarshaller
implements Unmarshaller<DeleteImportedKeyMaterialResult, JsonUnmarshallerContext> {
    private static DeleteImportedKeyMaterialResultJsonUnmarshaller instance;

    @Override
    public DeleteImportedKeyMaterialResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        DeleteImportedKeyMaterialResult deleteImportedKeyMaterialResult = new DeleteImportedKeyMaterialResult();
        return deleteImportedKeyMaterialResult;
    }

    public static DeleteImportedKeyMaterialResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DeleteImportedKeyMaterialResultJsonUnmarshaller();
        }
        return instance;
    }
}

