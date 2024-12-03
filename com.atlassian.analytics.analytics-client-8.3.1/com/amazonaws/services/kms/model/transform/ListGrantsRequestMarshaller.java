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
import com.amazonaws.services.kms.model.ListGrantsRequest;

@SdkInternalApi
public class ListGrantsRequestMarshaller {
    private static final MarshallingInfo<Integer> LIMIT_BINDING = MarshallingInfo.builder(MarshallingType.INTEGER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Limit").build();
    private static final MarshallingInfo<String> MARKER_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Marker").build();
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> GRANTID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantId").build();
    private static final MarshallingInfo<String> GRANTEEPRINCIPAL_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GranteePrincipal").build();
    private static final ListGrantsRequestMarshaller instance = new ListGrantsRequestMarshaller();

    public static ListGrantsRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(ListGrantsRequest listGrantsRequest, ProtocolMarshaller protocolMarshaller) {
        if (listGrantsRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(listGrantsRequest.getLimit(), LIMIT_BINDING);
            protocolMarshaller.marshall(listGrantsRequest.getMarker(), MARKER_BINDING);
            protocolMarshaller.marshall(listGrantsRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(listGrantsRequest.getGrantId(), GRANTID_BINDING);
            protocolMarshaller.marshall(listGrantsRequest.getGranteePrincipal(), GRANTEEPRINCIPAL_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

