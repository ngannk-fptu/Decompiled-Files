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
import com.amazonaws.services.kms.model.GrantConstraints;
import java.util.Map;

@SdkInternalApi
public class GrantConstraintsMarshaller {
    private static final MarshallingInfo<Map> ENCRYPTIONCONTEXTSUBSET_BINDING = MarshallingInfo.builder(MarshallingType.MAP).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("EncryptionContextSubset").build();
    private static final MarshallingInfo<Map> ENCRYPTIONCONTEXTEQUALS_BINDING = MarshallingInfo.builder(MarshallingType.MAP).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("EncryptionContextEquals").build();
    private static final GrantConstraintsMarshaller instance = new GrantConstraintsMarshaller();

    public static GrantConstraintsMarshaller getInstance() {
        return instance;
    }

    public void marshall(GrantConstraints grantConstraints, ProtocolMarshaller protocolMarshaller) {
        if (grantConstraints == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(grantConstraints.getEncryptionContextSubset(), ENCRYPTIONCONTEXTSUBSET_BINDING);
            protocolMarshaller.marshall(grantConstraints.getEncryptionContextEquals(), ENCRYPTIONCONTEXTEQUALS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

