/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.MarshallLocation;
import com.amazonaws.protocol.MarshallingInfo;
import com.amazonaws.protocol.MarshallingType;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.KeyMetadata;
import java.util.Date;
import java.util.List;

@SdkInternalApi
public class KeyMetadataMarshaller {
    private static final MarshallingInfo<String> AWSACCOUNTID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("AWSAccountId").build();
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> ARN_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Arn").build();
    private static final MarshallingInfo<Date> CREATIONDATE_BINDING = MarshallingInfo.builder(MarshallingType.DATE).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CreationDate").timestampFormat("unixTimestamp").build();
    private static final MarshallingInfo<Boolean> ENABLED_BINDING = MarshallingInfo.builder(MarshallingType.BOOLEAN).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Enabled").build();
    private static final MarshallingInfo<String> DESCRIPTION_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Description").build();
    private static final MarshallingInfo<String> KEYUSAGE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyUsage").build();
    private static final MarshallingInfo<String> KEYSTATE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyState").build();
    private static final MarshallingInfo<Date> DELETIONDATE_BINDING = MarshallingInfo.builder(MarshallingType.DATE).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("DeletionDate").timestampFormat("unixTimestamp").build();
    private static final MarshallingInfo<Date> VALIDTO_BINDING = MarshallingInfo.builder(MarshallingType.DATE).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ValidTo").timestampFormat("unixTimestamp").build();
    private static final MarshallingInfo<String> ORIGIN_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Origin").build();
    private static final MarshallingInfo<String> CUSTOMKEYSTOREID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreId").build();
    private static final MarshallingInfo<String> CLOUDHSMCLUSTERID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CloudHsmClusterId").build();
    private static final MarshallingInfo<String> EXPIRATIONMODEL_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ExpirationModel").build();
    private static final MarshallingInfo<String> KEYMANAGER_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyManager").build();
    private static final MarshallingInfo<String> CUSTOMERMASTERKEYSPEC_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomerMasterKeySpec").build();
    private static final MarshallingInfo<String> KEYSPEC_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeySpec").build();
    private static final MarshallingInfo<List> ENCRYPTIONALGORITHMS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("EncryptionAlgorithms").build();
    private static final MarshallingInfo<List> SIGNINGALGORITHMS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("SigningAlgorithms").build();
    private static final MarshallingInfo<Boolean> MULTIREGION_BINDING = MarshallingInfo.builder(MarshallingType.BOOLEAN).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("MultiRegion").build();
    private static final MarshallingInfo<StructuredPojo> MULTIREGIONCONFIGURATION_BINDING = MarshallingInfo.builder(MarshallingType.STRUCTURED).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("MultiRegionConfiguration").build();
    private static final MarshallingInfo<Integer> PENDINGDELETIONWINDOWINDAYS_BINDING = MarshallingInfo.builder(MarshallingType.INTEGER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("PendingDeletionWindowInDays").build();
    private static final MarshallingInfo<List> MACALGORITHMS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("MacAlgorithms").build();
    private static final KeyMetadataMarshaller instance = new KeyMetadataMarshaller();

    public static KeyMetadataMarshaller getInstance() {
        return instance;
    }

    public void marshall(KeyMetadata keyMetadata, ProtocolMarshaller protocolMarshaller) {
        if (keyMetadata == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(keyMetadata.getAWSAccountId(), AWSACCOUNTID_BINDING);
            protocolMarshaller.marshall(keyMetadata.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(keyMetadata.getArn(), ARN_BINDING);
            protocolMarshaller.marshall(keyMetadata.getCreationDate(), CREATIONDATE_BINDING);
            protocolMarshaller.marshall(keyMetadata.getEnabled(), ENABLED_BINDING);
            protocolMarshaller.marshall(keyMetadata.getDescription(), DESCRIPTION_BINDING);
            protocolMarshaller.marshall(keyMetadata.getKeyUsage(), KEYUSAGE_BINDING);
            protocolMarshaller.marshall(keyMetadata.getKeyState(), KEYSTATE_BINDING);
            protocolMarshaller.marshall(keyMetadata.getDeletionDate(), DELETIONDATE_BINDING);
            protocolMarshaller.marshall(keyMetadata.getValidTo(), VALIDTO_BINDING);
            protocolMarshaller.marshall(keyMetadata.getOrigin(), ORIGIN_BINDING);
            protocolMarshaller.marshall(keyMetadata.getCustomKeyStoreId(), CUSTOMKEYSTOREID_BINDING);
            protocolMarshaller.marshall(keyMetadata.getCloudHsmClusterId(), CLOUDHSMCLUSTERID_BINDING);
            protocolMarshaller.marshall(keyMetadata.getExpirationModel(), EXPIRATIONMODEL_BINDING);
            protocolMarshaller.marshall(keyMetadata.getKeyManager(), KEYMANAGER_BINDING);
            protocolMarshaller.marshall(keyMetadata.getCustomerMasterKeySpec(), CUSTOMERMASTERKEYSPEC_BINDING);
            protocolMarshaller.marshall(keyMetadata.getKeySpec(), KEYSPEC_BINDING);
            protocolMarshaller.marshall(keyMetadata.getEncryptionAlgorithms(), ENCRYPTIONALGORITHMS_BINDING);
            protocolMarshaller.marshall(keyMetadata.getSigningAlgorithms(), SIGNINGALGORITHMS_BINDING);
            protocolMarshaller.marshall(keyMetadata.getMultiRegion(), MULTIREGION_BINDING);
            protocolMarshaller.marshall(keyMetadata.getMultiRegionConfiguration(), MULTIREGIONCONFIGURATION_BINDING);
            protocolMarshaller.marshall(keyMetadata.getPendingDeletionWindowInDays(), PENDINGDELETIONWINDOWINDAYS_BINDING);
            protocolMarshaller.marshall(keyMetadata.getMacAlgorithms(), MACALGORITHMS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

