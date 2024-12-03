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
import com.amazonaws.services.kms.model.DescribeKeyRequest;
import java.util.List;

@SdkInternalApi
public class DescribeKeyRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<List> GRANTTOKENS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantTokens").build();
    private static final DescribeKeyRequestMarshaller instance = new DescribeKeyRequestMarshaller();

    public static DescribeKeyRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(DescribeKeyRequest describeKeyRequest, ProtocolMarshaller protocolMarshaller) {
        if (describeKeyRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(describeKeyRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(describeKeyRequest.getGrantTokens(), GRANTTOKENS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

