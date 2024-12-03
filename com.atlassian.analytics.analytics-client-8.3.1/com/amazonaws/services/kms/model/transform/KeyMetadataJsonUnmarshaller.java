/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.services.kms.model.KeyMetadata;
import com.amazonaws.services.kms.model.transform.MultiRegionConfigurationJsonUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.ListUnmarshaller;
import com.amazonaws.transform.SimpleTypeJsonUnmarshallers;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonToken;

public class KeyMetadataJsonUnmarshaller
implements Unmarshaller<KeyMetadata, JsonUnmarshallerContext> {
    private static KeyMetadataJsonUnmarshaller instance;

    @Override
    public KeyMetadata unmarshall(JsonUnmarshallerContext context) throws Exception {
        KeyMetadata keyMetadata = new KeyMetadata();
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
                if (context.testExpression("AWSAccountId", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setAWSAccountId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeyId", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setKeyId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("Arn", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setArn(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("CreationDate", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setCreationDate(SimpleTypeJsonUnmarshallers.DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
                if (context.testExpression("Enabled", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setEnabled(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
                if (context.testExpression("Description", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setDescription(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeyUsage", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setKeyUsage(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeyState", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setKeyState(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("DeletionDate", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setDeletionDate(SimpleTypeJsonUnmarshallers.DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
                if (context.testExpression("ValidTo", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setValidTo(SimpleTypeJsonUnmarshallers.DateJsonUnmarshallerFactory.getInstance("unixTimestamp").unmarshall(context));
                }
                if (context.testExpression("Origin", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setOrigin(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("CustomKeyStoreId", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setCustomKeyStoreId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("CloudHsmClusterId", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setCloudHsmClusterId(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("ExpirationModel", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setExpirationModel(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeyManager", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setKeyManager(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("CustomerMasterKeySpec", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setCustomerMasterKeySpec(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("KeySpec", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setKeySpec(context.getUnmarshaller(String.class).unmarshall(context));
                }
                if (context.testExpression("EncryptionAlgorithms", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setEncryptionAlgorithms(new ListUnmarshaller<String>(context.getUnmarshaller(String.class)).unmarshall(context));
                }
                if (context.testExpression("SigningAlgorithms", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setSigningAlgorithms(new ListUnmarshaller<String>(context.getUnmarshaller(String.class)).unmarshall(context));
                }
                if (context.testExpression("MultiRegion", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setMultiRegion(context.getUnmarshaller(Boolean.class).unmarshall(context));
                }
                if (context.testExpression("MultiRegionConfiguration", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setMultiRegionConfiguration(MultiRegionConfigurationJsonUnmarshaller.getInstance().unmarshall(context));
                }
                if (context.testExpression("PendingDeletionWindowInDays", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setPendingDeletionWindowInDays(context.getUnmarshaller(Integer.class).unmarshall(context));
                }
                if (context.testExpression("MacAlgorithms", targetDepth)) {
                    context.nextToken();
                    keyMetadata.setMacAlgorithms(new ListUnmarshaller<String>(context.getUnmarshaller(String.class)).unmarshall(context));
                }
            } else if (!(token != JsonToken.END_ARRAY && token != JsonToken.END_OBJECT || context.getLastParsedParentElement() != null && !context.getLastParsedParentElement().equals(currentParentElement) || context.getCurrentDepth() > originalDepth)) break;
            token = context.nextToken();
        }
        return keyMetadata;
    }

    public static KeyMetadataJsonUnmarshaller getInstance() {
        if (instance == null) {
            instance = new KeyMetadataJsonUnmarshaller();
        }
        return instance;
    }
}

