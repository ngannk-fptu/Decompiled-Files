/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.ReplicateKeyResult;
import com.amazonaws.services.kms.model.Tag;
import com.amazonaws.services.kms.model.transform.KeyMetadataJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.TagJsonUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.ListUnmarshaller;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class ReplicateKeyResultJsonUnmarshaller
implements Unmarshaller<ReplicateKeyResult, JsonUnmarshallerContext> {
    private static ReplicateKeyResultJsonUnmarshaller instance;

    @Override
    public ReplicateKeyResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        ReplicateKeyResult replicateKeyResult = new ReplicateKeyResult();
        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;
        JsonToken token = context.getCurrentToken();
        if (token == null) {
            token = context.nextToken();
        }
        if (token == JsonToken.VALUE_NULL) {
            return replicateKeyResult;
        }
        while (token != null) {
            if (token == JsonToken.FIELD_NAME || token == JsonToken.START_OBJECT) {
                if (context.testExpression("ReplicaKeyMetadata", targetDepth)) {
                    context.nextToken();
                    replicateKeyResult.setReplicaKeyMetadata(KeyMetadataJsonUnmarshaller.getInstance().unmarshall(context));
                }
                if (context.testExpression("ReplicaPolicy", targetDepth)) {
                    context.nextToken();
                    replicateKeyResult.setReplicaPolicy(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("ReplicaTags", targetDepth)) {
                    context.nextToken();
                    replicateKeyResult.setReplicaTags(new ListUnmarshaller<Tag>(TagJsonUnmarshaller.getInstance()).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return replicateKeyResult;
    }

    public static ReplicateKeyResultJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new ReplicateKeyResultJsonUnmarshaller();
        }
        return instance;
    }
}

