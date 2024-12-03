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
import com.amazonaws.services.kms.model.DescribeCustomKeyStoresRequest;

@SdkInternalApi
public class DescribeCustomKeyStoresRequestMarshaller {
    private static final MarshallingInfo<String> CUSTOMKEYSTOREID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreId").build();
    private static final MarshallingInfo<String> CUSTOMKEYSTORENAME_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreName").build();
    private static final MarshallingInfo<Integer> LIMIT_BINDING = MarshallingInfo.builder(MarshallingType.INTEGER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Limit").build();
    private static final MarshallingInfo<String> MARKER_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Marker").build();
    private static final DescribeCustomKeyStoresRequestMarshaller instance = new DescribeCustomKeyStoresRequestMarshaller();

    public static DescribeCustomKeyStoresRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(DescribeCustomKeyStoresRequest describeCustomKeyStoresRequest, ProtocolMarshaller protocolMarshaller) {
        if (describeCustomKeyStoresRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(describeCustomKeyStoresRequest.getCustomKeyStoreId(), CUSTOMKEYSTOREID_BINDING);
            protocolMarshaller.marshall(describeCustomKeyStoresRequest.getCustomKeyStoreName(), CUSTOMKEYSTORENAME_BINDING);
            protocolMarshaller.marshall(describeCustomKeyStoresRequest.getLimit(), LIMIT_BINDING);
            protocolMarshaller.marshall(describeCustomKeyStoresRequest.getMarker(), MARKER_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

