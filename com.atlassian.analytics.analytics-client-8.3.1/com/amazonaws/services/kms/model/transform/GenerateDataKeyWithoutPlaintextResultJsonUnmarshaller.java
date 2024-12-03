/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GenerateDataKeyWithoutPlaintextResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class GenerateDataKeyWithoutPlaintextResultJsonUnmarshaller
implements Unmarshaller<GenerateDataKeyWithoutPlaintextResult, JsonUnmarshallerContext> {
    private static GenerateDataKeyWithoutPlaintextResultJsonUnmarshaller instance;

    @Override
    public GenerateDataKeyWithoutPlaintextResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GenerateDataKeyWithoutPlaintextResult generateDataKeyWithoutPlaintextResult = new GenerateDataKeyWithoutPlaintextResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return generateDataKeyWithoutPlaintextResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("CiphertextBlob", targetDepth)) {
                    context.nextToken();
                    generateDataKeyWithoutPlaintextResult.setCiphertextBlob(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    generateDataKeyWithoutPlaintextResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return generateDataKeyWithoutPlaintextResult;
    }

    public static GenerateDataKeyWithoutPlaintextResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GenerateDataKeyWithoutPlaintextResultJsonUnmarshaller();
        }
        return instance;
    }
}

