/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.SignResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class SignResultJsonUnmarshaller
implements Unmarshaller<SignResult, JsonUnmarshallerContext> {
    private static SignResultJsonUnmarshaller instance;

    @Override
    public SignResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        SignResult signResult = new SignResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return signResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    signResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Signature", targetDepth)) {
                    context.nextToken();
                    signResult.setSignature(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("SigningAlgorithm", targetDepth)) {
                    context.nextToken();
                    signResult.setSigningAlgorithm(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return signResult;
    }

    public static SignResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new SignResultJsonUnmarshaller();
        }
        return instance;
    }
}

