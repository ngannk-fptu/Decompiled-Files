/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GenerateDataKeyPairWithoutPlaintextResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class GenerateDataKeyPairWithoutPlaintextResultJsonUnmarshaller
implements Unmarshaller<GenerateDataKeyPairWithoutPlaintextResult, JsonUnmarshallerContext> {
    private static GenerateDataKeyPairWithoutPlaintextResultJsonUnmarshaller instance;

    @Override
    public GenerateDataKeyPairWithoutPlaintextResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GenerateDataKeyPairWithoutPlaintextResult generateDataKeyPairWithoutPlaintextResult = new GenerateDataKeyPairWithoutPlaintextResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return generateDataKeyPairWithoutPlaintextResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("PrivateKeyCiphertextBlob", targetDepth)) {
                    context.nextToken();
                    generateDataKeyPairWithoutPlaintextResult.setPrivateKeyCiphertextBlob(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("PublicKey", targetDepth)) {
                    context.nextToken();
                    generateDataKeyPairWithoutPlaintextResult.setPublicKey(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    generateDataKeyPairWithoutPlaintextResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeyPairSpec", targetDepth)) {
                    context.nextToken();
                    generateDataKeyPairWithoutPlaintextResult.setKeyPairSpec(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return generateDataKeyPairWithoutPlaintextResult;
    }

    public static GenerateDataKeyPairWithoutPlaintextResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GenerateDataKeyPairWithoutPlaintextResultJsonUnmarshaller();
        }
        return instance;
    }
}

