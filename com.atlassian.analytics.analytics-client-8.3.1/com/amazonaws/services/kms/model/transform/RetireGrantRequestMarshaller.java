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
import com.amazonaws.services.kms.model.RetireGrantRequest;

@SdkInternalApi
public class RetireGrantRequestMarshaller {
    private static final MarshallingInfo<String> GRANTTOKEN_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantToken").build();
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> GRANTID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantId").build();
    private static final RetireGrantRequestMarshaller instance = new RetireGrantRequestMarshaller();

    public static RetireGrantRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(RetireGrantRequest retireGrantRequest, ProtocolMarshaller protocolMarshaller) {
        if (retireGrantRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(retireGrantRequest.getGrantToken(), GRANTTOKEN_BINDING);
            protocolMarshaller.marshall(retireGrantRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(retireGrantRequest.getGrantId(), GRANTID_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

