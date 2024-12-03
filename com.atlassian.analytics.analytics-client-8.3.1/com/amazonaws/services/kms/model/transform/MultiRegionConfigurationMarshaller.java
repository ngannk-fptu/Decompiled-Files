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
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.MultiRegionConfiguration;
import java.util.List;

@SdkInternalApi
public class MultiRegionConfigurationMarshaller {
    private static final MarshallingInfo<String> MULTIREGIONKEYTYPE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("MultiRegionKeyType").build();
    private static final MarshallingInfo<StructuredPojo> PRIMARYKEY_BINDING = MarshallingInfo.builder(MarshallingType.STRUCTURED).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("PrimaryKey").build();
    private static final MarshallingInfo<List> REPLICAKEYS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ReplicaKeys").build();
    private static final MultiRegionConfigurationMarshaller instance = new MultiRegionConfigurationMarshaller();

    public static MultiRegionConfigurationMarshaller getInstance() {
        return instance;
    }

    public void marshall(MultiRegionConfiguration multiRegionConfiguration, ProtocolMarshaller protocolMarshaller) {
        if (multiRegionConfiguration == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(multiRegionConfiguration.getMultiRegionKeyType(), MULTIREGIONKEYTYPE_BINDING);
            protocolMarshaller.marshall(multiRegionConfiguration.getPrimaryKey(), PRIMARYKEY_BINDING);
            protocolMarshaller.marshall(multiRegionConfiguration.getReplicaKeys(), REPLICAKEYS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

