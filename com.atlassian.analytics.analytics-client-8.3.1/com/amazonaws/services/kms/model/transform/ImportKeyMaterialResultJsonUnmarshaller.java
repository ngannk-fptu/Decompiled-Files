/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.ImportKeyMaterialResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class ImportKeyMaterialResultJsonUnmarshaller
implements Unmarshaller<ImportKeyMaterialResult, JsonUnmarshallerContext> {
    private static ImportKeyMaterialResultJsonUnmarshaller instance;

    @Override
    public ImportKeyMaterialResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        ImportKeyMaterialResult importKeyMaterialResult = new ImportKeyMaterialResult();
        return importKeyMaterialResult;
    }

    public static ImportKeyMaterialResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new ImportKeyMaterialResultJsonUnmarshaller();
        }
        return instance;
    }
}

