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
import com.amazonaws.services.kms.model.GetKeyRotationStatusRequest;

@SdkInternalApi
public class GetKeyRotationStatusRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final GetKeyRotationStatusRequestMarshaller instance = new GetKeyRotationStatusRequestMarshaller();

    public static GetKeyRotationStatusRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(GetKeyRotationStatusRequest getKeyRotationStatusRequest, ProtocolMarshaller protocolMarshaller) {
        if (getKeyRotationStatusRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(getKeyRotationStatusRequest.getKeyId(), KEYID_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

