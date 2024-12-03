/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.ReEncryptResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class ReEncryptResultJsonUnmarshaller
implements Unmarshaller<ReEncryptResult, JsonUnmarshallerContext> {
    private static ReEncryptResultJsonUnmarshaller instance;

    @Override
    public ReEncryptResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        ReEncryptResult reEncryptResult = new ReEncryptResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return reEncryptResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("CiphertextBlob", targetDepth)) {
                    context.nextToken();
                    reEncryptResult.setCiphertextBlob(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("SourceKeyId", targetDepth)) {
                    context.nextToken();
                    reEncryptResult.setSourceKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    reEncryptResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("SourceEncryptionAlgorithm", targetDepth)) {
                    context.nextToken();
                    reEncryptResult.setSourceEncryptionAlgorithm(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("DestinationEncryptionAlgorithm", targetDepth)) {
                    context.nextToken();
                    reEncryptResult.setDestinationEncryptionAlgorithm(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return reEncryptResult;
    }

    public static ReEncryptResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new ReEncryptResultJsonUnmarshaller();
        }
        return instance;
    }
}

