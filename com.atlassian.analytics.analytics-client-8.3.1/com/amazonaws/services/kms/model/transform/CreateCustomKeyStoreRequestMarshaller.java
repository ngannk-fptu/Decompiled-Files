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
import com.amazonaws.services.kms.model.CreateCustomKeyStoreRequest;

@SdkInternalApi
public class CreateCustomKeyStoreRequestMarshaller {
    private static final MarshallingInfo<String> CUSTOMKEYSTORENAME_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreName").build();
    private static final MarshallingInfo<String> CLOUDHSMCLUSTERID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CloudHsmClusterId").build();
    private static final MarshallingInfo<String> TRUSTANCHORCERTIFICATE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("TrustAnchorCertificate").build();
    private static final MarshallingInfo<String> KEYSTOREPASSWORD_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyStorePassword").build();
    private static final CreateCustomKeyStoreRequestMarshaller instance = new CreateCustomKeyStoreRequestMarshaller();

    public static CreateCustomKeyStoreRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(CreateCustomKeyStoreRequest createCustomKeyStoreRequest, ProtocolMarshaller protocolMarshaller) {
        if (createCustomKeyStoreRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(createCustomKeyStoreRequest.getCustomKeyStoreName(), CUSTOMKEYSTORENAME_BINDING);
            protocolMarshaller.marshall(createCustomKeyStoreRequest.getCloudHsmClusterId(), CLOUDHSMCLUSTERID_BINDING);
            protocolMarshaller.marshall(createCustomKeyStoreRequest.getTrustAnchorCertificate(), TRUSTANCHORCERTIFICATE_BINDING);
            protocolMarshaller.marshall(createCustomKeyStoreRequest.getKeyStorePassword(), KEYSTOREPASSWORD_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

