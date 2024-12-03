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
import com.amazonaws.services.kms.model.DisableKeyRotationRequest;
import com.amazonaws.services.kms.model.transform.DisableKeyRotationRequestMarshaller;
import com.amazonaws.transform.Marshaller;

@SdkInternalApi
public class DisableKeyRotationRequestProtocolMarshaller
implements Marshaller<Request<DisableKeyRotationRequest>, DisableKeyRotationRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/").httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.DisableKeyRotation").serviceName("AWSKMS").build();
    private final SdkJsonProtocolFactory protocolFactory;

    public DisableKeyRotationRequestProtocolMarshaller(SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Request<DisableKeyRotationRequest> marshall(DisableKeyRotationRequest disableKeyRotationRequest) {
        if (disableKeyRotationRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            ProtocolRequestMarshaller<DisableKeyRotationRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING, disableKeyRotationRequest);
            protocolMarshaller.startMarshalling();
            DisableKeyRotationRequestMarshaller.getInstance().marshall(disableKeyRotationRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

