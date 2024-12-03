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
import com.amazonaws.services.kms.model.MultiRegionKey;

@SdkInternalApi
public class MultiRegionKeyMarshaller {
    private static final MarshallingInfo<String> ARN_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Arn").build();
    private static final MarshallingInfo<String> REGION_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Region").build();
    private static final MultiRegionKeyMarshaller instance = new MultiRegionKeyMarshaller();

    public static MultiRegionKeyMarshaller getInstance() {
        return instance;
    }

    public void marshall(MultiRegionKey multiRegionKey, ProtocolMarshaller protocolMarshaller) {
        if (multiRegionKey == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(multiRegionKey.getArn(), ARN_BINDING);
            protocolMarshaller.marshall(multiRegionKey.getRegion(), REGION_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

