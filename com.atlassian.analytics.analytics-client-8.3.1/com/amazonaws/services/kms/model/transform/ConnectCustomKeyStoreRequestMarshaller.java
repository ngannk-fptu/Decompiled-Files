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
import com.amazonaws.services.kms.model.ConnectCustomKeyStoreRequest;

@SdkInternalApi
public class ConnectCustomKeyStoreRequestMarshaller {
    private static final MarshallingInfo<String> CUSTOMKEYSTOREID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreId").build();
    private static final ConnectCustomKeyStoreRequestMarshaller instance = new ConnectCustomKeyStoreRequestMarshaller();

    public static ConnectCustomKeyStoreRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(ConnectCustomKeyStoreRequest connectCustomKeyStoreRequest, ProtocolMarshaller protocolMarshaller) {
        if (connectCustomKeyStoreRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(connectCustomKeyStoreRequest.getCustomKeyStoreId(), CUSTOMKEYSTOREID_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

