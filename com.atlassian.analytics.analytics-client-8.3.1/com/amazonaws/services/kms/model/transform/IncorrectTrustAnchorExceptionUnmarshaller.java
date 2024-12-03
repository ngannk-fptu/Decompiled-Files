/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.IncorrectTrustAnchorException;
import com.amazonaws.transform.EnhancedJsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.fasterxml.jackson.core.JsonToken;

public class IncorrectTrustAnchorExceptionUnmarshaller
extends EnhancedJsonErrorUnmarshaller {
    private static IncorrectTrustAnchorExceptionUnmarshaller instance;

    private IncorrectTrustAnchorExceptionUnmarshaller() {
        super(IncorrectTrustAnchorException.class, "IncorrectTrustAnchorException");
    }

    @Override
    public IncorrectTrustAnchorException unmarshallFromContext(JsonUnmarshallerContext context) throws Exception {
        IncorrectTrustAnchorException incorrectTrustAnchorException = new IncorrectTrustAnchorException(null);
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
        return incorrectTrustAnchorException;
    }

    public static IncorrectTrustAnchorExceptionUnmarshaller getInstance() {
        if (instance == null) {
            instance = new IncorrectTrustAnchorExceptionUnmarshaller();
        }
        return instance;
    }
}

