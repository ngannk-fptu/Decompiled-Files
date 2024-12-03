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
import com.amazonaws.services.kms.model.CreateKeyRequest;
import java.util.List;

@SdkInternalApi
public class CreateKeyRequestMarshaller {
    private static final MarshallingInfo<String> POLICY_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Policy").build();
    private static final MarshallingInfo<String> DESCRIPTION_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Description").build();
    private static final MarshallingInfo<String> KEYUSAGE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyUsage").build();
    private static final MarshallingInfo<String> CUSTOMERMASTERKEYSPEC_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomerMasterKeySpec").build();
    private static final MarshallingInfo<String> KEYSPEC_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeySpec").build();
    private static final MarshallingInfo<String> ORIGIN_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Origin").build();
    private static final MarshallingInfo<String> CUSTOMKEYSTOREID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreId").build();
    private static final MarshallingInfo<Boolean> BYPASSPOLICYLOCKOUTSAFETYCHECK_BINDING = MarshallingInfo.builder(MarshallingType.BOOLEAN).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("BypassPolicyLockoutSafetyCheck").build();
    private static final MarshallingInfo<List> TAGS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Tags").build();
    private static final MarshallingInfo<Boolean> MULTIREGION_BINDING = MarshallingInfo.builder(MarshallingType.BOOLEAN).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("MultiRegion").build();
    private static final CreateKeyRequestMarshaller instance = new CreateKeyRequestMarshaller();

    public static CreateKeyRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(CreateKeyRequest createKeyRequest, ProtocolMarshaller protocolMarshaller) {
        if (createKeyRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(createKeyRequest.getPolicy(), POLICY_BINDING);
            protocolMarshaller.marshall(createKeyRequest.getDescription(), DESCRIPTION_BINDING);
            protocolMarshaller.marshall(createKeyRequest.getKeyUsage(), KEYUSAGE_BINDING);
            protocolMarshaller.marshall(createKeyRequest.getCustomerMasterKeySpec(), CUSTOMERMASTERKEYSPEC_BINDING);
            protocolMarshaller.marshall(createKeyRequest.getKeySpec(), KEYSPEC_BINDING);
            protocolMarshaller.marshall(createKeyRequest.getOrigin(), ORIGIN_BINDING);
            protocolMarshaller.marshall(createKeyRequest.getCustomKeyStoreId(), CUSTOMKEYSTOREID_BINDING);
            protocolMarshaller.marshall(createKeyRequest.getBypassPolicyLockoutSafetyCheck(), BYPASSPOLICYLOCKOUTSAFETYCHECK_BINDING);
            protocolMarshaller.marshall(createKeyRequest.getTags(), TAGS_BINDING);
            protocolMarshaller.marshall(createKeyRequest.getMultiRegion(), MULTIREGION_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

