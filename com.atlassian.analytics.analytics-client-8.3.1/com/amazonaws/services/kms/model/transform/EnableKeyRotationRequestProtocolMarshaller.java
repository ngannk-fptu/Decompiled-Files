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
import com.amazonaws.services.kms.model.EnableKeyRotationRequest;
import com.amazonaws.services.kms.model.transform.EnableKeyRotationRequestMarshaller;
import com.amazonaws.transform.Marshaller;

@SdkInternalApi
public class EnableKeyRotationRequestProtocolMarshaller
implements Marshaller<Request<EnableKeyRotationRequest>, EnableKeyRotationRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/").httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.EnableKeyRotation").serviceName("AWSKMS").build();
    private final SdkJsonProtocolFactory protocolFactory;

    public EnableKeyRotationRequestProtocolMarshaller(SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Request<EnableKeyRotationRequest> marshall(EnableKeyRotationRequest enableKeyRotationRequest) {
        if (enableKeyRotationRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            ProtocolRequestMarshaller<EnableKeyRotationRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING, enableKeyRotationRequest);
            protocolMarshaller.startMarshalling();
            EnableKeyRotationRequestMarshaller.getInstance().marshall(enableKeyRotationRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

