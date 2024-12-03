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
import com.amazonaws.services.kms.model.ReEncryptRequest;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

@SdkInternalApi
public class ReEncryptRequestMarshaller {
    private static final MarshallingInfo<ByteBuffer> CIPHERTEXTBLOB_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CiphertextBlob").build();
    private static final MarshallingInfo<Map> SOURCEENCRYPTIONCONTEXT_BINDING = MarshallingInfo.builder(MarshallingType.MAP).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("SourceEncryptionContext").build();
    private static final MarshallingInfo<String> SOURCEKEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("SourceKeyId").build();
    private static final MarshallingInfo<String> DESTINATIONKEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("DestinationKeyId").build();
    private static final MarshallingInfo<Map> DESTINATIONENCRYPTIONCONTEXT_BINDING = MarshallingInfo.builder(MarshallingType.MAP).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("DestinationEncryptionContext").build();
    private static final MarshallingInfo<String> SOURCEENCRYPTIONALGORITHM_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("SourceEncryptionAlgorithm").build();
    private static final MarshallingInfo<String> DESTINATIONENCRYPTIONALGORITHM_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("DestinationEncryptionAlgorithm").build();
    private static final MarshallingInfo<List> GRANTTOKENS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantTokens").build();
    private static final ReEncryptRequestMarshaller instance = new ReEncryptRequestMarshaller();

    public static ReEncryptRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(ReEncryptRequest reEncryptRequest, ProtocolMarshaller protocolMarshaller) {
        if (reEncryptRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(reEncryptRequest.getCiphertextBlob(), CIPHERTEXTBLOB_BINDING);
            protocolMarshaller.marshall(reEncryptRequest.getSourceEncryptionContext(), SOURCEENCRYPTIONCONTEXT_BINDING);
            protocolMarshaller.marshall(reEncryptRequest.getSourceKeyId(), SOURCEKEYID_BINDING);
            protocolMarshaller.marshall(reEncryptRequest.getDestinationKeyId(), DESTINATIONKEYID_BINDING);
            protocolMarshaller.marshall(reEncryptRequest.getDestinationEncryptionContext(), DESTINATIONENCRYPTIONCONTEXT_BINDING);
            protocolMarshaller.marshall(reEncryptRequest.getSourceEncryptionAlgorithm(), SOURCEENCRYPTIONALGORITHM_BINDING);
            protocolMarshaller.marshall(reEncryptRequest.getDestinationEncryptionAlgorithm(), DESTINATIONENCRYPTIONALGORITHM_BINDING);
            protocolMarshaller.marshall(reEncryptRequest.getGrantTokens(), GRANTTOKENS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

