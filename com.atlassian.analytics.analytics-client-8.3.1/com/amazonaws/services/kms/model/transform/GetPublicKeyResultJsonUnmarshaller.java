/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GetPublicKeyResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.ListUnmarshaller;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class GetPublicKeyResultJsonUnmarshaller
implements Unmarshaller<GetPublicKeyResult, JsonUnmarshallerContext> {
    private static GetPublicKeyResultJsonUnmarshaller instance;

    @Override
    public GetPublicKeyResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GetPublicKeyResult getPublicKeyResult = new GetPublicKeyResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return getPublicKeyResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    getPublicKeyResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("PublicKey", targetDepth)) {
                    context.nextToken();
                    getPublicKeyResult.setPublicKey(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("CustomerMasterKeySpec", targetDepth)) {
                    context.nextToken();
                    getPublicKeyResult.setCustomerMasterKeySpec(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeySpec", targetDepth)) {
                    context.nextToken();
                    getPublicKeyResult.setKeySpec(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeyUsage", targetDepth)) {
                    context.nextToken();
                    getPublicKeyResult.setKeyUsage(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("EncryptionAlgorithms", targetDepth)) {
                    context.nextToken();
                    getPublicKeyResult.setEncryptionAlgorithms(new ListUnmarshaller<String>(context.getUnmarshaller(String.class)).unmarshall(context));
                }
                if (context.testExpression("SigningAlgorithms", targetDepth)) {
                    context.nextToken();
                    getPublicKeyResult.setSigningAlgorithms(new ListUnmarshaller<String>(context.getUnmarshaller(String.class)).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return getPublicKeyResult;
    }

    public static GetPublicKeyResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GetPublicKeyResultJsonUnmarshaller();
        }
        return instance;
    }
}

