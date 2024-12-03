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
import com.amazonaws.services.kms.model.GenerateRandomRequest;

@SdkInternalApi
public class GenerateRandomRequestMarshaller {
    private static final MarshallingInfo<Integer> NUMBEROFBYTES_BINDING = MarshallingInfo.builder(MarshallingType.INTEGER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("NumberOfBytes").build();
    private static final MarshallingInfo<String> CUSTOMKEYSTOREID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreId").build();
    private static final GenerateRandomRequestMarshaller instance = new GenerateRandomRequestMarshaller();

    public static GenerateRandomRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(GenerateRandomRequest generateRandomRequest, ProtocolMarshaller protocolMarshaller) {
        if (generateRandomRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(generateRandomRequest.getNumberOfBytes(), NUMBEROFBYTES_BINDING);
            protocolMarshaller.marshall(generateRandomRequest.getCustomKeyStoreId(), CUSTOMKEYSTOREID_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

