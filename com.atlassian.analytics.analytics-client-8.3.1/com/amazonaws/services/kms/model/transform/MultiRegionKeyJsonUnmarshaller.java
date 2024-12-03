/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.MultiRegionKey;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class MultiRegionKeyJsonUnmarshaller
implements Unmarshaller<MultiRegionKey, JsonUnmarshallerContext> {
    private static MultiRegionKeyJsonUnmarshaller instance;

    @Override
    public MultiRegionKey unmarshall(JsonUnmarshallerContext context) throws Exception {
        MultiRegionKey multiRegionKey = new MultiRegionKey();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("Arn", targetDepth)) {
                    context.nextToken();
                    multiRegionKey.setArn(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Region", targetDepth)) {
                    context.nextToken();
                    multiRegionKey.setRegion(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return multiRegionKey;
    }

    public static MultiRegionKeyJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new MultiRegionKeyJsonUnmarshaller();
        }
        return instance;
    }
}

