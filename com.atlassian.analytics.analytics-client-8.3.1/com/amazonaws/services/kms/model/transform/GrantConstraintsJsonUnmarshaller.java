/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GrantConstraints;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.MapUnmarshaller;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class GrantConstraintsJsonUnmarshaller
implements Unmarshaller<GrantConstraints, JsonUnmarshallerContext> {
    private static GrantConstraintsJsonUnmarshaller instance;

    @Override
    public GrantConstraints unmarshall(JsonUnmarshallerContext context) throws Exception {
        GrantConstraints grantConstraints = new GrantConstraints();
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
                if (context.testExpression("EncryptionContextSubset", targetDepth)) {
                    context.nextToken();
                    grantConstraints.setEncryptionContextSubset(new MapUnmarshaller<String, String>(context.getUnmarshaller(String.class), context.getUnmarshaller(String.class)).unmarshall(context));
                }
                if (context.testExpression("EncryptionContextEquals", targetDepth)) {
                    context.nextToken();
                    grantConstraints.setEncryptionContextEquals(new MapUnmarshaller<String, String>(context.getUnmarshaller(String.class), context.getUnmarshaller(String.class)).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return grantConstraints;
    }

    public static GrantConstraintsJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GrantConstraintsJsonUnmarshaller();
        }
        return instance;
    }
}

