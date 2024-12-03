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
import com.amazonaws.services.kms.model.ReplicateKeyRequest;
import java.util.List;

@SdkInternalApi
public class ReplicateKeyRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> REPLICAREGION_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ReplicaRegion").build();
    private static final MarshallingInfo<String> POLICY_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Policy").build();
    private static final MarshallingInfo<Boolean> BYPASSPOLICYLOCKOUTSAFETYCHECK_BINDING = MarshallingInfo.builder(MarshallingType.BOOLEAN).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("BypassPolicyLockoutSafetyCheck").build();
    private static final MarshallingInfo<String> DESCRIPTION_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Description").build();
    private static final MarshallingInfo<List> TAGS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Tags").build();
    private static final ReplicateKeyRequestMarshaller instance = new ReplicateKeyRequestMarshaller();

    public static ReplicateKeyRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(ReplicateKeyRequest replicateKeyRequest, ProtocolMarshaller protocolMarshaller) {
        if (replicateKeyRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(replicateKeyRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(replicateKeyRequest.getReplicaRegion(), REPLICAREGION_BINDING);
            protocolMarshaller.marshall(replicateKeyRequest.getPolicy(), POLICY_BINDING);
            protocolMarshaller.marshall(replicateKeyRequest.getBypassPolicyLockoutSafetyCheck(), BYPASSPOLICYLOCKOUTSAFETYCHECK_BINDING);
            protocolMarshaller.marshall(replicateKeyRequest.getDescription(), DESCRIPTION_BINDING);
            protocolMarshaller.marshall(replicateKeyRequest.getTags(), TAGS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

