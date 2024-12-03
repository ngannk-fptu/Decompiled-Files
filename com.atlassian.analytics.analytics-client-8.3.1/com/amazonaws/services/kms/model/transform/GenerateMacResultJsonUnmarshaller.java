/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GenerateMacResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class GenerateMacResultJsonUnmarshaller
implements Unmarshaller<GenerateMacResult, JsonUnmarshallerContext> {
    private static GenerateMacResultJsonUnmarshaller instance;

    @Override
    public GenerateMacResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GenerateMacResult generateMacResult = new GenerateMacResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return generateMacResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("Mac", targetDepth)) {
                    context.nextToken();
                    generateMacResult.setMac(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("MacAlgorithm", targetDepth)) {
                    context.nextToken();
                    generateMacResult.setMacAlgorithm(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    generateMacResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return generateMacResult;
    }

    public static GenerateMacResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GenerateMacResultJsonUnmarshaller();
        }
        return instance;
    }
}

