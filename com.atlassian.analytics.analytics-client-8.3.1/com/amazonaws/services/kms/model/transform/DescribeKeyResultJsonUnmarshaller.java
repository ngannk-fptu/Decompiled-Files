/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.DescribeKeyResult;
import com.amazonaws.services.kms.model.transform.KeyMetadataJsonUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class DescribeKeyResultJsonUnmarshaller
implements Unmarshaller<DescribeKeyResult, JsonUnmarshallerContext> {
    private static DescribeKeyResultJsonUnmarshaller instance;

    @Override
    public DescribeKeyResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        DescribeKeyResult describeKeyResult = new DescribeKeyResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return describeKeyResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyMetadata", targetDepth)) {
                    context.nextToken();
                    describeKeyResult.setKeyMetadata(KeyMetadataJsonUnmarshaller.getInstance().unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return describeKeyResult;
    }

    public static DescribeKeyResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new DescribeKeyResultJsonUnmarshaller();
        }
        return instance;
    }
}

