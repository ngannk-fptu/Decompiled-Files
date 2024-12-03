/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.UpdateAliasResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class UpdateAliasResultJsonUnmarshaller
implements Unmarshaller<UpdateAliasResult, JsonUnmarshallerContext> {
    private static UpdateAliasResultJsonUnmarshaller instance;

    @Override
    public UpdateAliasResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        UpdateAliasResult updateAliasResult = new UpdateAliasResult();
        return updateAliasResult;
    }

    public static UpdateAliasResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new UpdateAliasResultJsonUnmarshaller();
        }
        return instance;
    }
}

