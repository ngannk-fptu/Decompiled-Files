/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.CancelKeyDeletionResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class CancelKeyDeletionResultJsonUnmarshaller
implements Unmarshaller<CancelKeyDeletionResult, JsonUnmarshallerContext> {
    private static CancelKeyDeletionResultJsonUnmarshaller instance;

    @Override
    public CancelKeyDeletionResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        CancelKeyDeletionResult cancelKeyDeletionResult = new CancelKeyDeletionResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return cancelKeyDeletionResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    cancelKeyDeletionResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return cancelKeyDeletionResult;
    }

    public static CancelKeyDeletionResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new CancelKeyDeletionResultJsonUnmarshaller();
        }
        return instance;
    }
}

