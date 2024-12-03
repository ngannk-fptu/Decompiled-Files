/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.Request;
import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.protocol.OperationInfo;
import com.amazonaws.protocol.Protocol;
import com.amazonaws.protocol.ProtocolRequestMarshaller;
import com.amazonaws.protocol.json.SdkJsonProtocolFactory;
import com.amazonaws.services.kms.model.GetKeyRotationStatusRequest;
import com.amazonaws.services.kms.model.transform.GetKeyRotationStatusRequestMarshaller;
import com.amazonaws.transform.Marshaller;

@SdkInternalApi
public class GetKeyRotationStatusRequestProtocolMarshaller
implements Marshaller<Request<GetKeyRotationStatusRequest>, GetKeyRotationStatusRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/").httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.GetKeyRotationStatus").serviceName("AWSKMS").build();
    private final SdkJsonProtocolFactory protocolFactory;

    public GetKeyRotationStatusRequestProtocolMarshaller(SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Request<GetKeyRotationStatusRequest> marshall(GetKeyRotationStatusRequest getKeyRotationStatusRequest) {
        if (getKeyRotationStatusRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            ProtocolRequestMarshaller<GetKeyRotationStatusRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING, getKeyRotationStatusRequest);
            protocolMarshaller.startMarshalling();
            GetKeyRotationStatusRequestMarshaller.getInstance().marshall(getKeyRotationStatusRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

