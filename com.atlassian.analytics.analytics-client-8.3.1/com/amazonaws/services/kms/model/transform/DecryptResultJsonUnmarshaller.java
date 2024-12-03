/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class DecryptResultJsonUnmarshaller
implements Unmarshaller<DecryptResult, JsonUnmarshallerContext> {
    private static DecryptResultJsonUnmarshaller instance;

    @Override
    public DecryptResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        DecryptResult decryptResult = new DecryptResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return decryptResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    decryptResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Plaintext", targetDepth)) {
                    context.nextToken();
                    decryptResult.setPlaintext(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("EncryptionAlgorithm", targetDepth)) {
                    context.nextToken();
                    decryptResult.setEncryptionAlgorithm(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return decryptResult;
    }

    public static DecryptResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DecryptResultJsonUnmarshaller();
        }
        return instance;
    }
}

