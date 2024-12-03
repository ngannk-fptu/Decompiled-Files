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
import com.amazonaws.services.kms.model.SignRequest;
import java.nio.ByteBuffer;
import java.util.List;

@SdkInternalApi
public class SignRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<ByteBuffer> MESSAGE_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Message").build();
    private static final MarshallingInfo<String> MESSAGETYPE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("MessageType").build();
    private static final MarshallingInfo<List> GRANTTOKENS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantTokens").build();
    private static final MarshallingInfo<String> SIGNINGALGORITHM_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("SigningAlgorithm").build();
    private static final SignRequestMarshaller instance = new SignRequestMarshaller();

    public static SignRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(SignRequest signRequest, ProtocolMarshaller protocolMarshaller) {
        if (signRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(signRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(signRequest.getMessage(), MESSAGE_BINDING);
            protocolMarshaller.marshall(signRequest.getMessageType(), MESSAGETYPE_BINDING);
            protocolMarshaller.marshall(signRequest.getGrantTokens(), GRANTTOKENS_BINDING);
            protocolMarshaller.marshall(signRequest.getSigningAlgorithm(), SIGNINGALGORITHM_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

