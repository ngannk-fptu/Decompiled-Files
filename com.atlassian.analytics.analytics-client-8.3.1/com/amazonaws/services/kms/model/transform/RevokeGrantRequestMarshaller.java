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
import com.amazonaws.services.kms.model.RevokeGrantRequest;

@SdkInternalApi
public class RevokeGrantRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> GRANTID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantId").build();
    private static final RevokeGrantRequestMarshaller instance = new RevokeGrantRequestMarshaller();

    public static RevokeGrantRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(RevokeGrantRequest revokeGrantRequest, ProtocolMarshaller protocolMarshaller) {
        if (revokeGrantRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(revokeGrantRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(revokeGrantRequest.getGrantId(), GRANTID_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

