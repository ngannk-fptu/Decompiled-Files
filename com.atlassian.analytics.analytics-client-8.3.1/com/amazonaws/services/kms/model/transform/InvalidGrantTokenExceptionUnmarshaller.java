/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.InvalidGrantTokenException;
import com.amazonaws.transform.EnhancedJsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.fasterxml.jackson.core.JsonToken;

public class InvalidGrantTokenExceptionUnmarshaller
extends EnhancedJsonErrorUnmarshaller {
    private static InvalidGrantTokenExceptionUnmarshaller instance;

    private InvalidGrantTokenExceptionUnmarshaller() {
        super(InvalidGrantTokenException.class, "InvalidGrantTokenException");
    }

    @Override
    public InvalidGrantTokenException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        InvalidGrantTokenException invalidGrantTokenException = new InvalidGrantTokenException(null);
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
        while (token != null && (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT || token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) {
            token = context.nextToken();
        }
        return invalidGrantTokenException;
    }

    public static InvalidGrantTokenExceptionUnmarshaller getInstance() {
        if (instance == null) {
            instance = new InvalidGrantTokenExceptionUnmarshaller();
        }
        return instance;
    }
}

