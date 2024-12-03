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
import com.amazonaws.services.kms.model.GenerateMacRequest;
import java.nio.ByteBuffer;
import java.util.List;

@SdkInternalApi
public class GenerateMacRequestMarshaller {
    private static final MarshallingInfo<ByteBuffer> MESSAGE_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Message").build();
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> MACALGORITHM_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("MacAlgorithm").build();
    private static final MarshallingInfo<List> GRANTTOKENS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantTokens").build();
    private static final GenerateMacRequestMarshaller instance = new GenerateMacRequestMarshaller();

    public static GenerateMacRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(GenerateMacRequest generateMacRequest, ProtocolMarshaller protocolMarshaller) {
        if (generateMacRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(generateMacRequest.getMessage(), MESSAGE_BINDING);
            protocolMarshaller.marshall(generateMacRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(generateMacRequest.getMacAlgorithm(), MACALGORITHM_BINDING);
            protocolMarshaller.marshall(generateMacRequest.getGrantTokens(), GRANTTOKENS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

