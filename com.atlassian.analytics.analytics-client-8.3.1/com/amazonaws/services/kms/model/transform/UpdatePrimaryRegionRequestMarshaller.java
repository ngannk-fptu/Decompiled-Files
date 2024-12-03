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
import com.amazonaws.services.kms.model.UpdatePrimaryRegionRequest;

@SdkInternalApi
public class UpdatePrimaryRegionRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> PRIMARYREGION_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("PrimaryRegion").build();
    private static final UpdatePrimaryRegionRequestMarshaller instance = new UpdatePrimaryRegionRequestMarshaller();

    public static UpdatePrimaryRegionRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(UpdatePrimaryRegionRequest updatePrimaryRegionRequest, ProtocolMarshaller protocolMarshaller) {
        if (updatePrimaryRegionRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(updatePrimaryRegionRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(updatePrimaryRegionRequest.getPrimaryRegion(), PRIMARYREGION_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

