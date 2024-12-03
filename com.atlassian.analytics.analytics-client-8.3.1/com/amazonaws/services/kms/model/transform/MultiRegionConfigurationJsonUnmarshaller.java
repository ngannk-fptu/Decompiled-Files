/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.MultiRegionConfiguration;
import com.amazonaws.services.kms.model.MultiRegionKey;
import com.amazonaws.services.kms.model.transform.MultiRegionKeyJsonUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.ListUnmarshaller;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class MultiRegionConfigurationJsonUnmarshaller
implements Unmarshaller<MultiRegionConfiguration, JsonUnmarshallerContext> {
    private static MultiRegionConfigurationJsonUnmarshaller instance;

    @Override
    public MultiRegionConfiguration unmarshall(JsonUnmarshallerContext context) throws Exception {
        MultiRegionConfiguration multiRegionConfiguration = new MultiRegionConfiguration();
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
                if (context.testExpression("MultiRegionKeyType", targetDepth)) {
                    context.nextToken();
                    multiRegionConfiguration.setMultiRegionKeyType(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("PrimaryKey", targetDepth)) {
                    context.nextToken();
                    multiRegionConfiguration.setPrimaryKey(MultiRegionKeyJsonUnmarshaller.getInstance().unmarshall(context));
                }
                if (context.testExpression("ReplicaKeys", targetDepth)) {
                    context.nextToken();
                    multiRegionConfiguration.setReplicaKeys(new ListUnmarshaller<MultiRegionKey>(MultiRegionKeyJsonUnmarshaller.getInstance()).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return multiRegionConfiguration;
    }

    public static MultiRegionConfigurationJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new MultiRegionConfigurationJsonUnmarshaller();
        }
        return instance;
    }
}

