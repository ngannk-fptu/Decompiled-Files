/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GenerateDataKeyPairResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class GenerateDataKeyPairResultJsonUnmarshaller
implements Unmarshaller<GenerateDataKeyPairResult, JsonUnmarshallerContext> {
    private static GenerateDataKeyPairResultJsonUnmarshaller instance;

    @Override
    public GenerateDataKeyPairResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GenerateDataKeyPairResult generateDataKeyPairResult = new GenerateDataKeyPairResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return generateDataKeyPairResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("PrivateKeyCiphertextBlob", targetDepth)) {
                    context.nextToken();
                    generateDataKeyPairResult.setPrivateKeyCiphertextBlob(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("PrivateKeyPlaintext", targetDepth)) {
                    context.nextToken();
                    generateDataKeyPairResult.setPrivateKeyPlaintext(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("PublicKey", targetDepth)) {
                    context.nextToken();
                    generateDataKeyPairResult.setPublicKey(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    generateDataKeyPairResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeyPairSpec", targetDepth)) {
                    context.nextToken();
                    generateDataKeyPairResult.setKeyPairSpec(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return generateDataKeyPairResult;
    }

    public static GenerateDataKeyPairResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GenerateDataKeyPairResultJsonUnmarshaller();
        }
        return instance;
    }
}

