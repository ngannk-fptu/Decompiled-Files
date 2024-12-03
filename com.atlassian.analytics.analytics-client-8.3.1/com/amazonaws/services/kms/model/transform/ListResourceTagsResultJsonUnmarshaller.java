/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.ListResourceTagsResult;
import com.amazonaws.services.kms.model.Tag;
import com.amazonaws.services.kms.model.transform.TagJsonUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.ListUnmarshaller;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class ListResourceTagsResultJsonUnmarshaller
implements Unmarshaller<ListResourceTagsResult, JsonUnmarshallerContext> {
    private static ListResourceTagsResultJsonUnmarshaller instance;

    @Override
    public ListResourceTagsResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        ListResourceTagsResult listResourceTagsResult = new ListResourceTagsResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return listResourceTagsResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("Tags", targetDepth)) {
                    context.nextToken();
                    listResourceTagsResult.setTags(new ListUnmarshaller<Tag>(TagJsonUnmarshaller.getInstance()).unmarshall(context));
                }
                if (context.testExpression("NextMarker", targetDepth)) {
                    context.nextToken();
                    listResourceTagsResult.setNextMarker(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Truncated", targetDepth)) {
                    context.nextToken();
                    listResourceTagsResult.setTruncated(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return listResourceTagsResult;
    }

    public static ListResourceTagsResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new ListResourceTagsResultJsonUnmarshaller();
        }
        return instance;
    }
}

