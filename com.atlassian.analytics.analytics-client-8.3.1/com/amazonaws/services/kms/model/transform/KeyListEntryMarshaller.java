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
import com.amazonaws.services.kms.model.KeyListEntry;

@SdkInternalApi
public class KeyListEntryMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> KEYARN_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyArn").build();
    private static final KeyListEntryMarshaller instance = new KeyListEntryMarshaller();

    public static KeyListEntryMarshaller getInstance() {
        return instance;
    }

    public void marshall(KeyListEntry keyListEntry, ProtocolMarshaller protocolMarshaller) {
        if (keyListEntry == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(keyListEntry.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(keyListEntry.getKeyArn(), KEYARN_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

