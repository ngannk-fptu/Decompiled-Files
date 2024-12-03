/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.DeleteAliasResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class DeleteAliasResultJsonUnmarshaller
implements Unmarshaller<DeleteAliasResult, JsonUnmarshallerContext> {
    private static DeleteAliasResultJsonUnmarshaller instance;

    @Override
    public DeleteAliasResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        DeleteAliasResult deleteAliasResult = new DeleteAliasResult();
        return deleteAliasResult;
    }

    public static DeleteAliasResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DeleteAliasResultJsonUnmarshaller();
        }
        return instance;
    }
}

