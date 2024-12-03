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
import com.amazonaws.services.kms.model.GetPublicKeyRequest;
import java.util.List;

@SdkInternalApi
public class GetPublicKeyRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<List> GRANTTOKENS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantTokens").build();
    private static final GetPublicKeyRequestMarshaller instance = new GetPublicKeyRequestMarshaller();

    public static GetPublicKeyRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(GetPublicKeyRequest getPublicKeyRequest, ProtocolMarshaller protocolMarshaller) {
        if (getPublicKeyRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(getPublicKeyRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(getPublicKeyRequest.getGrantTokens(), GRANTTOKENS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

