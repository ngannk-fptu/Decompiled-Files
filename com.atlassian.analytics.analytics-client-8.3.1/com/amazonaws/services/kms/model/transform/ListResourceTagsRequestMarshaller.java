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
import com.amazonaws.services.kms.model.ListResourceTagsRequest;

@SdkInternalApi
public class ListResourceTagsRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<Integer> LIMIT_BINDING = MarshallingInfo.builder(MarshallingType.INTEGER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Limit").build();
    private static final MarshallingInfo<String> MARKER_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Marker").build();
    private static final ListResourceTagsRequestMarshaller instance = new ListResourceTagsRequestMarshaller();

    public static ListResourceTagsRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(ListResourceTagsRequest listResourceTagsRequest, ProtocolMarshaller protocolMarshaller) {
        if (listResourceTagsRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(listResourceTagsRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(listResourceTagsRequest.getLimit(), LIMIT_BINDING);
            protocolMarshaller.marshall(listResourceTagsRequest.getMarker(), MARKER_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

