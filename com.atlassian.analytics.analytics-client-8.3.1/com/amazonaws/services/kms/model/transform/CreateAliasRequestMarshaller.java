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
import com.amazonaws.services.kms.model.CreateAliasRequest;

@SdkInternalApi
public class CreateAliasRequestMarshaller {
    private static final MarshallingInfo<String> ALIASNAME_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("AliasName").build();
    private static final MarshallingInfo<String> TARGETKEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("TargetKeyId").build();
    private static final CreateAliasRequestMarshaller instance = new CreateAliasRequestMarshaller();

    public static CreateAliasRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(CreateAliasRequest createAliasRequest, ProtocolMarshaller protocolMarshaller) {
        if (createAliasRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(createAliasRequest.getAliasName(), ALIASNAME_BINDING);
            protocolMarshaller.marshall(createAliasRequest.getTargetKeyId(), TARGETKEYID_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

