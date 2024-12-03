/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.GrantListEntry;
import com.amazonaws.services.kms.model.transform.GrantConstraintsJsonUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.ListUnmarshaller;
import com.amazonaws.transform.SimpleTypeJsonUnmarshallers;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class GrantListEntryJsonUnmarshaller
implements Unmarshaller<GrantListEntry, JsonUnmarshallerContext> {
    private static GrantListEntryJsonUnmarshaller instance;

    @Override
    public GrantListEntry unmarshall(JsonUnmarshallerContext context) throws Exception {
        GrantListEntry grantListEntry = new GrantListEntry();
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
                    grantListEntry.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("GrantId", targetDepth)) {
                    context.nextToken();
                    grantListEntry.setGrantId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Name", targetDepth)) {
                    context.nextToken();
                    grantListEntry.setName(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("CreationDate", targetDepth)) {
                    context.nextToken();
                    grantListEntry.setCreationDate(SimpleTypeJsonUnmarshallers.DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
                if (context.testExpression("GranteePrincipal", targetDepth)) {
                    context.nextToken();
                    grantListEntry.setGranteePrincipal(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("RetiringPrincipal", targetDepth)) {
                    context.nextToken();
                    grantListEntry.setRetiringPrincipal(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("IssuingAccount", targetDepth)) {
                    context.nextToken();
                    grantListEntry.setIssuingAccount(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Operations", targetDepth)) {
                    context.nextToken();
                    grantListEntry.setOperations(new ListUnmarshaller<String>(context.getUnmarshaller(String.class)).unmarshall(context));
                }
                if (context.testExpression("Constraints", targetDepth)) {
                    context.nextToken();
                    grantListEntry.setConstraints(GrantConstraintsJsonUnmarshaller.getInstance().unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return grantListEntry;
    }

    public static GrantListEntryJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new GrantListEntryJsonUnmarshaller();
        }
        return instance;
    }
}

