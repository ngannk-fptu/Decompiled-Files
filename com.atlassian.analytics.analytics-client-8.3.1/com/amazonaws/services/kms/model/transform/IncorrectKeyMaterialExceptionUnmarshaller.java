/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.IncorrectKeyMaterialException;
import com.amazonaws.transform.EnhancedJsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.fasterxml.jackson.core.JsonToken;

public class IncorrectKeyMaterialExceptionUnmarshaller
extends EnhancedJsonErrorUnmarshaller {
    private static IncorrectKeyMaterialExceptionUnmarshaller instance;

    private IncorrectKeyMaterialExceptionUnmarshaller() {
        super(IncorrectKeyMaterialException.class, "IncorrectKeyMaterialException");
    }

    @Override
    public IncorrectKeyMaterialException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        IncorrectKeyMaterialException incorrectKeyMaterialException = new IncorrectKeyMaterialException(null);
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
        return incorrectKeyMaterialException;
    }

    public static IncorrectKeyMaterialExceptionUnmarshaller getInstance() {
        if (instance == null) {
            instance = new IncorrectKeyMaterialExceptionUnmarshaller();
        }
        return instance;
    }
}

