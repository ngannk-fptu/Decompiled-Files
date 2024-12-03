/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.InvalidImportTokenException;
import com.amazonaws.transform.EnhancedJsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.fasterxml.jackson.core.JsonToken;

public class InvalidImportTokenExceptionUnmarshaller
extends EnhancedJsonErrorUnmarshaller {
    private static InvalidImportTokenExceptionUnmarshaller instance;

    private InvalidImportTokenExceptionUnmarshaller() {
        super(InvalidImportTokenException.class, "InvalidImportTokenException");
    }

    @Override
    public InvalidImportTokenException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        InvalidImportTokenException invalidImportTokenException = new InvalidImportTokenException(null);
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
        return invalidImportTokenException;
    }

    public static InvalidImportTokenExceptionUnmarshaller getInstance() {
        if (instance == null) {
            instance = new InvalidImportTokenExceptionUnmarshaller();
        }
        return instance;
    }
}

