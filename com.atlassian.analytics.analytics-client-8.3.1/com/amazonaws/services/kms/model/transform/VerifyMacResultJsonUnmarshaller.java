/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.VerifyMacResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class VerifyMacResultJsonUnmarshaller
implements Unmarshaller<VerifyMacResult, JsonUnmarshallerContext> {
    private static VerifyMacResultJsonUnmarshaller instance;

    @Override
    public VerifyMacResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        VerifyMacResult verifyMacResult = new VerifyMacResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return verifyMacResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    verifyMacResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("MacValid", targetDepth)) {
                    context.nextToken();
                    verifyMacResult.setMacValid(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
                if (context.testExpression("MacAlgorithm", targetDepth)) {
                    context.nextToken();
                    verifyMacResult.setMacAlgorithm(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return verifyMacResult;
    }

    public static VerifyMacResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new VerifyMacResultJsonUnmarshaller();
        }
        return instance;
    }
}

