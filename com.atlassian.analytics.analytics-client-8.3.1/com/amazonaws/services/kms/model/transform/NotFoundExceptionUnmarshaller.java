/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.transform.EnhancedJsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.fasterxml.jackson.core.JsonToken;

public class NotFoundExceptionUnmarshaller
extends EnhancedJsonErrorUnmarshaller {
    private static NotFoundExceptionUnmarshaller instance;

    private NotFoundExceptionUnmarshaller() {
        super(NotFoundException.class, "NotFoundException");
    }

    @Override
    public NotFoundException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        NotFoundException notFoundException = new NotFoundException(null);
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
        return notFoundException;
    }

    public static NotFoundExceptionUnmarshaller getInstance() {
        if (instance == null) {
            instance = new NotFoundExceptionUnmarshaller();
        }
        return instance;
    }
}

