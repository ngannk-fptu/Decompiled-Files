/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.CustomKeyStoreInvalidStateException;
import com.amazonaws.transform.EnhancedJsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.fasterxml.jackson.core.JsonToken;

public class CustomKeyStoreInvalidStateExceptionUnmarshaller
extends EnhancedJsonErrorUnmarshaller {
    private static CustomKeyStoreInvalidStateExceptionUnmarshaller instance;

    private CustomKeyStoreInvalidStateExceptionUnmarshaller() {
        super(CustomKeyStoreInvalidStateException.class, "CustomKeyStoreInvalidStateException");
    }

    @Override
    public CustomKeyStoreInvalidStateException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        CustomKeyStoreInvalidStateException customKeyStoreInvalidStateException = new CustomKeyStoreInvalidStateException(null);
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
        return customKeyStoreInvalidStateException;
    }

    public static CustomKeyStoreInvalidStateExceptionUnmarshaller getInstance() {
        if (instance == null) {
            instance = new CustomKeyStoreInvalidStateExceptionUnmarshaller();
        }
        return instance;
    }
}

