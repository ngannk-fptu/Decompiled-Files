/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.CreateKeyResult;
import com.amazonaws.services.kms.model.transform.KeyMetadataJsonUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class CreateKeyResultJsonUnmarshaller
implements Unmarshaller<CreateKeyResult, JsonUnmarshallerContext> {
    private static CreateKeyResultJsonUnmarshaller instance;

    @Override
    public CreateKeyResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        CreateKeyResult createKeyResult = new CreateKeyResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return createKeyResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyMetadata", targetDepth)) {
                    context.nextToken();
                    createKeyResult.setKeyMetadata(KeyMetadataJsonUnmarshaller.getInstance().unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return createKeyResult;
    }

    public static CreateKeyResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new CreateKeyResultJsonUnmarshaller();
        }
        return instance;
    }
}

