/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GetParametersForImportResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.SimpleTypeJsonUnmarshallers;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;
import java.nio.ByteBuffer;

public class GetParametersForImportResultJsonUnmarshaller
implements Unmarshaller<GetParametersForImportResult, JsonUnmarshallerContext> {
    private static GetParametersForImportResultJsonUnmarshaller instance;

    @Override
    public GetParametersForImportResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        GetParametersForImportResult getParametersForImportResult = new GetParametersForImportResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return getParametersForImportResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    getParametersForImportResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("ImportToken", targetDepth)) {
                    context.nextToken();
                    getParametersForImportResult.setImportToken(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("PublicKey", targetDepth)) {
                    context.nextToken();
                    getParametersForImportResult.setPublicKey(context.getUnmarshaller(ByteBuffer.class).unmarshall(context));
                }
                if (context.testExpression("ParametersValidTo", targetDepth)) {
                    context.nextToken();
                    getParametersForImportResult.setParametersValidTo(SimpleTypeJsonUnmarshallers.DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return getParametersForImportResult;
    }

    public static GetParametersForImportResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GetParametersForImportResultJsonUnmarshaller();
        }
        return instance;
    }
}

