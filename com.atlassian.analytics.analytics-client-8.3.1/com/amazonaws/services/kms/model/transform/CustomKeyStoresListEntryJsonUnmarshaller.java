/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.CustomKeyStoresListEntry;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.SimpleTypeJsonUnmarshallers;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class CustomKeyStoresListEntryJsonUnmarshaller
implements Unmarshaller<CustomKeyStoresListEntry, JsonUnmarshallerContext> {
    private static CustomKeyStoresListEntryJsonUnmarshaller instance;

    @Override
    public CustomKeyStoresListEntry unmarshall(JsonUnmarshallerContext context) throws Exception {
        CustomKeyStoresListEntry customKeyStoresListEntry = new CustomKeyStoresListEntry();
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
                if (context.testExpression("CustomKeyStoreId", targetDepth)) {
                    context.nextToken();
                    customKeyStoresListEntry.setCustomKeyStoreId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("CustomKeyStoreName", targetDepth)) {
                    context.nextToken();
                    customKeyStoresListEntry.setCustomKeyStoreName(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("CloudHsmClusterId", targetDepth)) {
                    context.nextToken();
                    customKeyStoresListEntry.setCloudHsmClusterId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("TrustAnchorCertificate", targetDepth)) {
                    context.nextToken();
                    customKeyStoresListEntry.setTrustAnchorCertificate(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("ConnectionState", targetDepth)) {
                    context.nextToken();
                    customKeyStoresListEntry.setConnectionState(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("ConnectionErrorCode", targetDepth)) {
                    context.nextToken();
                    customKeyStoresListEntry.setConnectionErrorCode(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("CreationDate", targetDepth)) {
                    context.nextToken();
                    customKeyStoresListEntry.setCreationDate(SimpleTypeJsonUnmarshallers.DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return customKeyStoresListEntry;
    }

    public static CustomKeyStoresListEntryJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new CustomKeyStoresListEntryJsonUnmarshaller();
        }
        return instance;
    }
}

