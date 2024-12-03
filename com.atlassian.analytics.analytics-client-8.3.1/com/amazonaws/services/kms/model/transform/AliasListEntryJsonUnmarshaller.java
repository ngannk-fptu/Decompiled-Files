/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.SimpleTypeJsonUnmarshallers;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class AliasListEntryJsonUnmarshaller
implements Unmarshaller<AliasListEntry, JsonUnmarshallerContext> {
    private static AliasListEntryJsonUnmarshaller instance;

    @Override
    public AliasListEntry unmarshall(JsonUnmarshallerContext context) throws Exception {
        AliasListEntry aliasListEntry = new AliasListEntry();
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
                if (context.testExpression("AliasName", targetDepth)) {
                    context.nextToken();
                    aliasListEntry.setAliasName(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("AliasArn", targetDepth)) {
                    context.nextToken();
                    aliasListEntry.setAliasArn(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("TargetKeyId", targetDepth)) {
                    context.nextToken();
                    aliasListEntry.setTargetKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("CreationDate", targetDepth)) {
                    context.nextToken();
                    aliasListEntry.setCreationDate(SimpleTypeJsonUnmarshallers.DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
                if (context.testExpression("LastUpdatedDate", targetDepth)) {
                    context.nextToken();
                    aliasListEntry.setLastUpdatedDate(SimpleTypeJsonUnmarshallers.DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return aliasListEntry;
    }

    public static AliasListEntryJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new AliasListEntryJsonUnmarshaller();
        }
        return instance;
    }
}

