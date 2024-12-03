/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.EncryptResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class EncryptResultJsonUnmarshaller
implements Unmarshaller<EncryptResult, JsonUnmarshallerContext> {
    private static EncryptResultJsonUnmarshaller instance;

    @Override
    public EncryptResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        EncryptResult encryptResult = new EncryptResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return encryptResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("CiphertextBlob", targetDepth)) {
                    context.nextToken();
                    encryptResult.setCiphertextBlob(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    encryptResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("EncryptionAlgorithm", targetDepth)) {
                    context.nextToken();
                    encryptResult.setEncryptionAlgorithm(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return encryptResult;
    }

    public static EncryptResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new EncryptResultJsonUnmarshaller();
        }
        return instance;
    }
}

