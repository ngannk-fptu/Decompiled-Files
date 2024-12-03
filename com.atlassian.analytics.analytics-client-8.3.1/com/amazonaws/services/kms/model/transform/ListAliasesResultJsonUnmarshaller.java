/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.ListAliasesResult;
import com.amazonaws.services.kms.model.transform.AliasListEntryJsonUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.ListUnmarshaller;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class ListAliasesResultJsonUnmarshaller
implements Unmarshaller<ListAliasesResult, JsonUnmarshallerContext> {
    private static ListAliasesResultJsonUnmarshaller instance;

    @Override
    public ListAliasesResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        ListAliasesResult listAliasesResult = new ListAliasesResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return listAliasesResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("Aliases", targetDepth)) {
                    context.nextToken();
                    listAliasesResult.setAliases(new ListUnmarshaller<AliasListEntry>(AliasListEntryJsonUnmarshaller.getInstance()).unmarshall(context));
                }
                if (context.testExpression("NextMarker", targetDepth)) {
                    context.nextToken();
                    listAliasesResult.setNextMarker(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Truncated", targetDepth)) {
                    context.nextToken();
                    listAliasesResult.setTruncated(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return listAliasesResult;
    }

    public static ListAliasesResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new ListAliasesResultJsonUnmarshaller();
        }
        return instance;
    }
}

