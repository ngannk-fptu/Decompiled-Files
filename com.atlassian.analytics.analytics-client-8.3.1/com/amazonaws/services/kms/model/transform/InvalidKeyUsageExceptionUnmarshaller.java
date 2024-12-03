/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.InvalidKeyUsageException;
import com.amazonaws.transform.EnhancedJsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.fasterxml.jackson.core.JsonToken;

public class InvalidKeyUsageExceptionUnmarshaller
extends EnhancedJsonErrorUnmarshaller {
    private static InvalidKeyUsageExceptionUnmarshaller instance;

    private InvalidKeyUsageExceptionUnmarshaller() {
        super(InvalidKeyUsageException.class, "InvalidKeyUsageException");
    }

    @Override
    public InvalidKeyUsageException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        InvalidKeyUsageException invalidKeyUsageException = new InvalidKeyUsageException(null);
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
        return invalidKeyUsageException;
    }

    public static InvalidKeyUsageExceptionUnmarshaller getInstance() {
        if (instance == null) {
            instance = new InvalidKeyUsageExceptionUnmarshaller();
        }
        return instance;
    }
}

