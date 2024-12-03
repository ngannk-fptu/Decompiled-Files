/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GetKeyPolicyResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class GetKeyPolicyResultJsonUnmarshaller
implements Unmarshaller<GetKeyPolicyResult, JsonUnmarshallerContext> {
    private static GetKeyPolicyResultJsonUnmarshaller instance;

    @Override
    public GetKeyPolicyResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GetKeyPolicyResult getKeyPolicyResult = new GetKeyPolicyResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return getKeyPolicyResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("Policy", targetDepth)) {
                    context.nextToken();
                    getKeyPolicyResult.setPolicy(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return getKeyPolicyResult;
    }

    public static GetKeyPolicyResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GetKeyPolicyResultJsonUnmarshaller();
        }
        return instance;
    }
}

