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
import com.amazonaws.services.kms.model.GetKeyPolicyRequest;
import com.amazonaws.services.kms.model.transform.GetKeyPolicyRequestMarshaller;
import com.amazonaws.transform.Marshaller;

@SdkInternalApi
public class GetKeyPolicyRequestProtocolMarshaller
implements Marshaller<Request<GetKeyPolicyRequest>, GetKeyPolicyRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/").httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.GetKeyPolicy").serviceName("AWSKMS").build();
    private final SdkJsonProtocolFactory protocolFactory;

    public GetKeyPolicyRequestProtocolMarshaller(SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Request<GetKeyPolicyRequest> marshall(GetKeyPolicyRequest getKeyPolicyRequest) {
        if (getKeyPolicyRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            ProtocolRequestMarshaller<GetKeyPolicyRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING, getKeyPolicyRequest);
            protocolMarshaller.startMarshalling();
            GetKeyPolicyRequestMarshaller.getInstance().marshall(getKeyPolicyRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

