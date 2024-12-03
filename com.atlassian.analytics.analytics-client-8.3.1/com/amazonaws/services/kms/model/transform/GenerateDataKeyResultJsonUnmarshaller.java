/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GenerateDataKeyResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class GenerateDataKeyResultJsonUnmarshaller
implements Unmarshaller<GenerateDataKeyResult, JsonUnmarshallerContext> {
    private static GenerateDataKeyResultJsonUnmarshaller instance;

    @Override
    public GenerateDataKeyResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GenerateDataKeyResult generateDataKeyResult = new GenerateDataKeyResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return generateDataKeyResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("CiphertextBlob", targetDepth)) {
                    context.nextToken();
                    generateDataKeyResult.setCiphertextBlob(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("Plaintext", targetDepth)) {
                    context.nextToken();
                    generateDataKeyResult.setPlaintext(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    generateDataKeyResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return generateDataKeyResult;
    }

    public static GenerateDataKeyResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GenerateDataKeyResultJsonUnmarshaller();
        }
        return instance;
    }
}

