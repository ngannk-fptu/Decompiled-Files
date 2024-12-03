/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.KeyListEntry;
import com.amazonaws.services.kms.model.ListKeysResult;
import com.amazonaws.services.kms.model.transform.KeyListEntryJsonUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.ListUnmarshaller;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class ListKeysResultJsonUnmarshaller
implements Unmarshaller<ListKeysResult, JsonUnmarshallerContext> {
    private static ListKeysResultJsonUnmarshaller instance;

    @Override
    public ListKeysResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        ListKeysResult listKeysResult = new ListKeysResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return listKeysResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("Keys", targetDepth)) {
                    context.nextToken();
                    listKeysResult.setKeys(new ListUnmarshaller<KeyListEntry>(KeyListEntryJsonUnmarshaller.getInstance()).unmarshall(context));
                }
                if (context.testExpression("NextMarker", targetDepth)) {
                    context.nextToken();
                    listKeysResult.setNextMarker(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Truncated", targetDepth)) {
                    context.nextToken();
                    listKeysResult.setTruncated(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return listKeysResult;
    }

    public static ListKeysResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new ListKeysResultJsonUnmarshaller();
        }
        return instance;
    }
}

