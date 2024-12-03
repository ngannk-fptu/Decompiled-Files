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
import com.amazonaws.services.kms.model.DeleteImportedKeyMaterialRequest;

@SdkInternalApi
public class DeleteImportedKeyMaterialRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final DeleteImportedKeyMaterialRequestMarshaller instance = new DeleteImportedKeyMaterialRequestMarshaller();

    public static DeleteImportedKeyMaterialRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(DeleteImportedKeyMaterialRequest deleteImportedKeyMaterialRequest, ProtocolMarshaller protocolMarshaller) {
        if (deleteImportedKeyMaterialRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(deleteImportedKeyMaterialRequest.getKeyId(), KEYID_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

