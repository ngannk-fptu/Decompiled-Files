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
import com.amazonaws.services.kms.model.Tag;

@SdkInternalApi
public class TagMarshaller {
    private static final MarshallingInfo<String> TAGKEY_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("TagKey").build();
    private static final MarshallingInfo<String> TAGVALUE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("TagValue").build();
    private static final TagMarshaller instance = new TagMarshaller();

    public static TagMarshaller getInstance() {
        return instance;
    }

    public void marshall(Tag tag, ProtocolMarshaller protocolMarshaller) {
        if (tag == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(tag.getTagKey(), TAGKEY_BINDING);
            protocolMarshaller.marshall(tag.getTagValue(), TAGVALUE_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

