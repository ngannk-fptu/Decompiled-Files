/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GrantListEntry;
import com.amazonaws.services.kms.model.ListGrantsResult;
import com.amazonaws.services.kms.model.transform.GrantListEntryJsonUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.ListUnmarshaller;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class ListGrantsResultJsonUnmarshaller
implements Unmarshaller<ListGrantsResult, JsonUnmarshallerContext> {
    private static ListGrantsResultJsonUnmarshaller instance;

    @Override
    public ListGrantsResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        ListGrantsResult listGrantsResult = new ListGrantsResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return listGrantsResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("Grants", targetDepth)) {
                    context.nextToken();
                    listGrantsResult.setGrants(new ListUnmarshaller<GrantListEntry>(GrantListEntryJsonUnmarshaller.getInstance()).unmarshall(context));
                }
                if (context.testExpression("NextMarker", targetDepth)) {
                    context.nextToken();
                    listGrantsResult.setNextMarker(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Truncated", targetDepth)) {
                    context.nextToken();
                    listGrantsResult.setTruncated(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return listGrantsResult;
    }

    public static ListGrantsResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new ListGrantsResultJsonUnmarshaller();
        }
        return instance;
    }
}

