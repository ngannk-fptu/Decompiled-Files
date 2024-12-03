/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.CreateAliasResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class CreateAliasResultJsonUnmarshaller
implements Unmarshaller<CreateAliasResult, JsonUnmarshallerContext> {
    private static CreateAliasResultJsonUnmarshaller instance;

    @Override
    public CreateAliasResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        CreateAliasResult createAliasResult = new CreateAliasResult();
        return createAliasResult;
    }

    public static CreateAliasResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new CreateAliasResultJsonUnmarshaller();
        }
        return instance;
    }
}

