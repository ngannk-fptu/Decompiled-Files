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
import com.amazonaws.services.kms.model.VerifyRequest;
import java.nio.ByteBuffer;
import java.util.List;

@SdkInternalApi
public class VerifyRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<ByteBuffer> MESSAGE_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Message").build();
    private static final MarshallingInfo<String> MESSAGETYPE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("MessageType").build();
    private static final MarshallingInfo<ByteBuffer> SIGNATURE_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Signature").build();
    private static final MarshallingInfo<String> SIGNINGALGORITHM_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("SigningAlgorithm").build();
    private static final MarshallingInfo<List> GRANTTOKENS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantTokens").build();
    private static final VerifyRequestMarshaller instance = new VerifyRequestMarshaller();

    public static VerifyRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(VerifyRequest verifyRequest, ProtocolMarshaller protocolMarshaller) {
        if (verifyRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(verifyRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(verifyRequest.getMessage(), MESSAGE_BINDING);
            protocolMarshaller.marshall(verifyRequest.getMessageType(), MESSAGETYPE_BINDING);
            protocolMarshaller.marshall(verifyRequest.getSignature(), SIGNATURE_BINDING);
            protocolMarshaller.marshall(verifyRequest.getSigningAlgorithm(), SIGNINGALGORITHM_BINDING);
            protocolMarshaller.marshall(verifyRequest.getGrantTokens(), GRANTTOKENS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

