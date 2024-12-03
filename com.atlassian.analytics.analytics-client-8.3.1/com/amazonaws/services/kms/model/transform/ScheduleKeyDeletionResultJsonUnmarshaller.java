/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.ScheduleKeyDeletionResult;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.SimpleTypeJsonUnmarshallers;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class ScheduleKeyDeletionResultJsonUnmarshaller
implements Unmarshaller<ScheduleKeyDeletionResult, JsonUnmarshallerContext> {
    private static ScheduleKeyDeletionResultJsonUnmarshaller instance;

    @Override
    public ScheduleKeyDeletionResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        ScheduleKeyDeletionResult scheduleKeyDeletionResult = new ScheduleKeyDeletionResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return scheduleKeyDeletionResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    scheduleKeyDeletionResult.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("DeletionDate", targetDepth)) {
                    context.nextToken();
                    scheduleKeyDeletionResult.setDeletionDate(SimpleTypeJsonUnmarshallers.DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
                if (context.testExpression("KeyState", targetDepth)) {
                    context.nextToken();
                    scheduleKeyDeletionResult.setKeyState(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("PendingWindowInDays", targetDepth)) {
                    context.nextToken();
                    scheduleKeyDeletionResult.setPendingWindowInDays(context.getUnmarshaller(Integer.class).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return scheduleKeyDeletionResult;
    }

    public static ScheduleKeyDeletionResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new ScheduleKeyDeletionResultJsonUnmarshaller();
        }
        return instance;
    }
}

