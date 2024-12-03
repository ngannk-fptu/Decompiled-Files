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
import com.amazonaws.services.kms.model.ListKeyPoliciesRequest;

@SdkInternalApi
public class ListKeyPoliciesRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<Integer> LIMIT_BINDING = MarshallingInfo.builder(MarshallingType.INTEGER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Limit").build();
    private static final MarshallingInfo<String> MARKER_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Marker").build();
    private static final ListKeyPoliciesRequestMarshaller instance = new ListKeyPoliciesRequestMarshaller();

    public static ListKeyPoliciesRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(ListKeyPoliciesRequest listKeyPoliciesRequest, ProtocolMarshaller protocolMarshaller) {
        if (listKeyPoliciesRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(listKeyPoliciesRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(listKeyPoliciesRequest.getLimit(), LIMIT_BINDING);
            protocolMarshaller.marshall(listKeyPoliciesRequest.getMarker(), MARKER_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

