/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.KMSInternalException;
import com.amazonaws.transform.EnhancedJsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.fasterxml.jackson.core.JsonToken;

public class KMSInternalExceptionUnmarshaller
extends EnhancedJsonErrorUnmarshaller {
    private static KMSInternalExceptionUnmarshaller instance;

    private KMSInternalExceptionUnmarshaller() {
        super(KMSInternalException.class, "KMSInternalException");
    }

    @Override
    public KMSInternalException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        KMSInternalException kMSInternalException = new KMSInternalException(null);
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
        return kMSInternalException;
    }

    public static KMSInternalExceptionUnmarshaller getInstance() {
        if (instance == null) {
            instance = new KMSInternalExceptionUnmarshaller();
        }
        return instance;
    }
}

