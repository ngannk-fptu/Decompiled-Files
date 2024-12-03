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
import com.amazonaws.services.kms.model.GenerateDataKeyPairRequest;
import com.amazonaws.services.kms.model.transform.GenerateDataKeyPairRequestMarshaller;
import com.amazonaws.transform.Marshaller;

@SdkInternalApi
public class GenerateDataKeyPairRequestProtocolMarshaller
implements Marshaller<Request<GenerateDataKeyPairRequest>, GenerateDataKeyPairRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/").httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.GenerateDataKeyPair").serviceName("AWSKMS").build();
    private final SdkJsonProtocolFactory protocolFactory;

    public GenerateDataKeyPairRequestProtocolMarshaller(SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Request<GenerateDataKeyPairRequest> marshall(GenerateDataKeyPairRequest generateDataKeyPairRequest) {
        if (generateDataKeyPairRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            ProtocolRequestMarshaller<GenerateDataKeyPairRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING, generateDataKeyPairRequest);
            protocolMarshaller.startMarshalling();
            GenerateDataKeyPairRequestMarshaller.getInstance().marshall(generateDataKeyPairRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

