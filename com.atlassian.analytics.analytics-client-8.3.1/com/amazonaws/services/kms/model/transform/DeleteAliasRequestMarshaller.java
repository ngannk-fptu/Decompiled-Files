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
import com.amazonaws.services.kms.model.DeleteAliasRequest;

@SdkInternalApi
public class DeleteAliasRequestMarshaller {
    private static final MarshallingInfo<String> ALIASNAME_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("AliasName").build();
    private static final DeleteAliasRequestMarshaller instance = new DeleteAliasRequestMarshaller();

    public static DeleteAliasRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(DeleteAliasRequest deleteAliasRequest, ProtocolMarshaller protocolMarshaller) {
        if (deleteAliasRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(deleteAliasRequest.getAliasName(), ALIASNAME_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

