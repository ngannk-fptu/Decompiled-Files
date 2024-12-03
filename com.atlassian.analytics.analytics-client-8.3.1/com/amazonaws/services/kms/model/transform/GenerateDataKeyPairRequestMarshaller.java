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
import com.amazonaws.services.kms.model.GenerateDataKeyPairRequest;
import java.util.List;
import java.util.Map;

@SdkInternalApi
public class GenerateDataKeyPairRequestMarshaller {
    private static final MarshallingInfo<Map> ENCRYPTIONCONTEXT_BINDING = MarshallingInfo.builder(MarshallingType.MAP).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("EncryptionContext").build();
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> KEYPAIRSPEC_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyPairSpec").build();
    private static final MarshallingInfo<List> GRANTTOKENS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantTokens").build();
    private static final GenerateDataKeyPairRequestMarshaller instance = new GenerateDataKeyPairRequestMarshaller();

    public static GenerateDataKeyPairRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(GenerateDataKeyPairRequest generateDataKeyPairRequest, ProtocolMarshaller protocolMarshaller) {
        if (generateDataKeyPairRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(generateDataKeyPairRequest.getEncryptionContext(), ENCRYPTIONCONTEXT_BINDING);
            protocolMarshaller.marshall(generateDataKeyPairRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(generateDataKeyPairRequest.getKeyPairSpec(), KEYPAIRSPEC_BINDING);
            protocolMarshaller.marshall(generateDataKeyPairRequest.getGrantTokens(), GRANTTOKENS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

