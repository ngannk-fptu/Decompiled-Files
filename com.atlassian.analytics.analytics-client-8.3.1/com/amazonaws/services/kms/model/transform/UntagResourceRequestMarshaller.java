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
import com.amazonaws.services.kms.model.UntagResourceRequest;
import java.util.List;

@SdkInternalApi
public class UntagResourceRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<List> TAGKEYS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("TagKeys").build();
    private static final UntagResourceRequestMarshaller instance = new UntagResourceRequestMarshaller();

    public static UntagResourceRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(UntagResourceRequest untagResourceRequest, ProtocolMarshaller protocolMarshaller) {
        if (untagResourceRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(untagResourceRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(untagResourceRequest.getTagKeys(), TAGKEYS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

