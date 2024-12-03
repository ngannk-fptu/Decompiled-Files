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
import com.amazonaws.services.kms.model.ListRetirableGrantsRequest;

@SdkInternalApi
public class ListRetirableGrantsRequestMarshaller {
    private static final MarshallingInfo<Integer> LIMIT_BINDING = MarshallingInfo.builder(MarshallingType.INTEGER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Limit").build();
    private static final MarshallingInfo<String> MARKER_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Marker").build();
    private static final MarshallingInfo<String> RETIRINGPRINCIPAL_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("RetiringPrincipal").build();
    private static final ListRetirableGrantsRequestMarshaller instance = new ListRetirableGrantsRequestMarshaller();

    public static ListRetirableGrantsRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(ListRetirableGrantsRequest listRetirableGrantsRequest, ProtocolMarshaller protocolMarshaller) {
        if (listRetirableGrantsRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(listRetirableGrantsRequest.getLimit(), LIMIT_BINDING);
            protocolMarshaller.marshall(listRetirableGrantsRequest.getMarker(), MARKER_BINDING);
            protocolMarshaller.marshall(listRetirableGrantsRequest.getRetiringPrincipal(), RETIRINGPRINCIPAL_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

