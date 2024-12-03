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
import com.amazonaws.services.kms.model.DecryptRequest;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

@SdkInternalApi
public class DecryptRequestMarshaller {
    private static final MarshallingInfo<ByteBuffer> CIPHERTEXTBLOB_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CiphertextBlob").build();
    private static final MarshallingInfo<Map> ENCRYPTIONCONTEXT_BINDING = MarshallingInfo.builder(MarshallingType.MAP).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("EncryptionContext").build();
    private static final MarshallingInfo<List> GRANTTOKENS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantTokens").build();
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> ENCRYPTIONALGORITHM_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("EncryptionAlgorithm").build();
    private static final DecryptRequestMarshaller instance = new DecryptRequestMarshaller();

    public static DecryptRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(DecryptRequest decryptRequest, ProtocolMarshaller protocolMarshaller) {
        if (decryptRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(decryptRequest.getCiphertextBlob(), CIPHERTEXTBLOB_BINDING);
            protocolMarshaller.marshall(decryptRequest.getEncryptionContext(), ENCRYPTIONCONTEXT_BINDING);
            protocolMarshaller.marshall(decryptRequest.getGrantTokens(), GRANTTOKENS_BINDING);
            protocolMarshaller.marshall(decryptRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(decryptRequest.getEncryptionAlgorithm(), ENCRYPTIONALGORITHM_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

