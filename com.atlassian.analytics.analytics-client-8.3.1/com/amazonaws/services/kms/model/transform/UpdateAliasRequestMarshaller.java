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
import com.amazonaws.services.kms.model.UpdateAliasRequest;

@SdkInternalApi
public class UpdateAliasRequestMarshaller {
    private static final MarshallingInfo<String> ALIASNAME_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("AliasName").build();
    private static final MarshallingInfo<String> TARGETKEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("TargetKeyId").build();
    private static final UpdateAliasRequestMarshaller instance = new UpdateAliasRequestMarshaller();

    public static UpdateAliasRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(UpdateAliasRequest updateAliasRequest, ProtocolMarshaller protocolMarshaller) {
        if (updateAliasRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(updateAliasRequest.getAliasName(), ALIASNAME_BINDING);
            protocolMarshaller.marshall(updateAliasRequest.getTargetKeyId(), TARGETKEYID_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

