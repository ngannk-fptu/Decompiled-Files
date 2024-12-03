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
import com.amazonaws.services.kms.model.DisconnectCustomKeyStoreRequest;

@SdkInternalApi
public class DisconnectCustomKeyStoreRequestMarshaller {
    private static final MarshallingInfo<String> CUSTOMKEYSTOREID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreId").build();
    private static final DisconnectCustomKeyStoreRequestMarshaller instance = new DisconnectCustomKeyStoreRequestMarshaller();

    public static DisconnectCustomKeyStoreRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(DisconnectCustomKeyStoreRequest disconnectCustomKeyStoreRequest, ProtocolMarshaller protocolMarshaller) {
        if (disconnectCustomKeyStoreRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(disconnectCustomKeyStoreRequest.getCustomKeyStoreId(), CUSTOMKEYSTOREID_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

