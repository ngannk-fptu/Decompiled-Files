/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.KeyListEntry;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class KeyListEntryJsonUnmarshaller
implements Unmarshaller<KeyListEntry, JsonUnmarshallerContext> {
    private static KeyListEntryJsonUnmarshaller instance;

    @Override
    public KeyListEntry unmarshall(JsonUnmarshallerContext context) throws Exception {
        KeyListEntry keyListEntry = new KeyListEntry();
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
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    keyListEntry.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeyArn", targetDepth)) {
                    context.nextToken();
                    keyListEntry.setKeyArn(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return keyListEntry;
    }

    public static KeyListEntryJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new KeyListEntryJsonUnmarshaller();
        }
        return instance;
    }
}

