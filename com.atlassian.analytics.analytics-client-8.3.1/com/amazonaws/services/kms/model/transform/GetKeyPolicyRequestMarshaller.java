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
import com.amazonaws.services.kms.model.GetKeyPolicyRequest;

@SdkInternalApi
public class GetKeyPolicyRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> POLICYNAME_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("PolicyName").build();
    private static final GetKeyPolicyRequestMarshaller instance = new GetKeyPolicyRequestMarshaller();

    public static GetKeyPolicyRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(GetKeyPolicyRequest getKeyPolicyRequest, ProtocolMarshaller protocolMarshaller) {
        if (getKeyPolicyRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(getKeyPolicyRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(getKeyPolicyRequest.getPolicyName(), POLICYNAME_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

