/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GetKeyRotationStatusResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class GetKeyRotationStatusResultJsonUnmarshaller
implements Unmarshaller<GetKeyRotationStatusResult, JsonUnmarshallerContext> {
    private static GetKeyRotationStatusResultJsonUnmarshaller instance;

    @Override
    public GetKeyRotationStatusResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GetKeyRotationStatusResult getKeyRotationStatusResult = new GetKeyRotationStatusResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return getKeyRotationStatusResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyRotationEnabled", targetDepth)) {
                    context.nextToken();
                    getKeyRotationStatusResult.setKeyRotationEnabled(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return getKeyRotationStatusResult;
    }

    public static GetKeyRotationStatusResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GetKeyRotationStatusResultJsonUnmarshaller();
        }
        return instance;
    }
}

