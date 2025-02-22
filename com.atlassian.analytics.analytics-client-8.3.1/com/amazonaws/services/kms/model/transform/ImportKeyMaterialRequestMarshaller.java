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
import com.amazonaws.services.kms.model.ImportKeyMaterialRequest;
import java.nio.ByteBuffer;
import java.util.Date;

@SdkInternalApi
public class ImportKeyMaterialRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<ByteBuffer> IMPORTTOKEN_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ImportToken").build();
    private static final MarshallingInfo<ByteBuffer> ENCRYPTEDKEYMATERIAL_BINDING = MarshallingInfo.builder(MarshallingType.BYTE_BUFFER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("EncryptedKeyMaterial").build();
    private static final MarshallingInfo<Date> VALIDTO_BINDING = MarshallingInfo.builder(MarshallingType.DATE).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ValidTo").timestampFormat("unixTimestamp").build();
    private static final MarshallingInfo<String> EXPIRATIONMODEL_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ExpirationModel").build();
    private static final ImportKeyMaterialRequestMarshaller instance = new ImportKeyMaterialRequestMarshaller();

    public static ImportKeyMaterialRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(ImportKeyMaterialRequest importKeyMaterialRequest, ProtocolMarshaller protocolMarshaller) {
        if (importKeyMaterialRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(importKeyMaterialRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(importKeyMaterialRequest.getImportToken(), IMPORTTOKEN_BINDING);
            protocolMarshaller.marshall(importKeyMaterialRequest.getEncryptedKeyMaterial(), ENCRYPTEDKEYMATERIAL_BINDING);
            protocolMarshaller.marshall(importKeyMaterialRequest.getValidTo(), VALIDTO_BINDING);
            protocolMarshaller.marshall(importKeyMaterialRequest.getExpirationModel(), EXPIRATIONMODEL_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

