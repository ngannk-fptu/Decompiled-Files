/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GenerateRandomResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class GenerateRandomResultJsonUnmarshaller
implements Unmarshaller<GenerateRandomResult, JsonUnmarshallerContext> {
    private static GenerateRandomResultJsonUnmarshaller instance;

    @Override
    public GenerateRandomResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GenerateRandomResult generateRandomResult = new GenerateRandomResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return generateRandomResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("Plaintext", targetDepth)) {
                    context.nextToken();
                    generateRandomResult.setPlaintext(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return generateRandomResult;
    }

    public static GenerateRandomResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GenerateRandomResultJsonUnmarshaller();
        }
        return instance;
    }
}

