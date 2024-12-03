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
import com.amazonaws.services.kms.model.GetParametersForImportRequest;

@SdkInternalApi
public class GetParametersForImportRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> WRAPPINGALGORITHM_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("WrappingAlgorithm").build();
    private static final MarshallingInfo<String> WRAPPINGKEYSPEC_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("WrappingKeySpec").build();
    private static final GetParametersForImportRequestMarshaller instance = new GetParametersForImportRequestMarshaller();

    public static GetParametersForImportRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(GetParametersForImportRequest getParametersForImportRequest, ProtocolMarshaller protocolMarshaller) {
        if (getParametersForImportRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(getParametersForImportRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(getParametersForImportRequest.getWrappingAlgorithm(), WRAPPINGALGORITHM_BINDING);
            protocolMarshaller.marshall(getParametersForImportRequest.getWrappingKeySpec(), WRAPPINGKEYSPEC_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

