/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.VerifyResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class VerifyResultJsonUnmarshaller
implements Unmarshaller<VerifyResult, JsonUnmarshallerContext> {
    private static VerifyResultJsonUnmarshaller instance;

    @Override
    public VerifyResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        VerifyResult verifyResult = new VerifyResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return verifyResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    verifyResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("SignatureValid", targetDepth)) {
                    context.nextToken();
                    verifyResult.setSignatureValid(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
                if (context.testExpression("SigningAlgorithm", targetDepth)) {
                    context.nextToken();
                    verifyResult.setSigningAlgorithm(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return verifyResult;
    }

    public static VerifyResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new VerifyResultJsonUnmarshaller();
        }
        return instance;
    }
}

