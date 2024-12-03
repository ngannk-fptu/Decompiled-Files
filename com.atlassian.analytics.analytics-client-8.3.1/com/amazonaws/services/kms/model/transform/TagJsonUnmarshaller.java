/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.Tag;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class TagJsonUnmarshaller
implements Unmarshaller<Tag, JsonUnmarshallerContext> {
    private static TagJsonUnmarshaller instance;

    @Override
    public Tag unmarshall(JsonUnmarshallerContext context) throws Exception {
        Tag tag = new Tag();
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
                if (context.testExpression("TagKey", targetDepth)) {
                    context.nextToken();
                    tag.setTagKey(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("TagValue", targetDepth)) {
                    context.nextToken();
                    tag.setTagValue(context.getUnmarshaller(String.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return tag;
    }

    public static TagJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new TagJsonUnmarshaller();
        }
        return instance;
    }
}

