/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.KMSInvalidSignatureException;
import com.amazonaws.transform.EnhancedJsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.fasterxml.jackson.core.JsonToken;

public class KMSInvalidSignatureExceptionUnmarshaller
extends EnhancedJsonErrorUnmarshaller {
    private static KMSInvalidSignatureExceptionUnmarshaller instance;

    private KMSInvalidSignatureExceptionUnmarshaller() {
        super(KMSInvalidSignatureException.class, "KMSInvalidSignatureException");
    }

    @Override
    public KMSInvalidSignatureException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        KMSInvalidSignatureException kMSInvalidSignatureException = new KMSInvalidSignatureException(null);
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
        return kMSInvalidSignatureException;
    }

    public static KMSInvalidSignatureExceptionUnmarshaller getInstance() {
        if (instance == null) {
            instance = new KMSInvalidSignatureExceptionUnmarshaller();
        }
        return instance;
    }
}

