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
import com.amazonaws.services.kms.model.TagResourceRequest;
import java.util.List;

@SdkInternalApi
public class TagResourceRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<List> TAGS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Tags").build();
    private static final TagResourceRequestMarshaller instance = new TagResourceRequestMarshaller();

    public static TagResourceRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(TagResourceRequest tagResourceRequest, ProtocolMarshaller protocolMarshaller) {
        if (tagResourceRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(tagResourceRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(tagResourceRequest.getTags(), TAGS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

