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
import com.amazonaws.services.kms.model.GetParametersForImportRequest;
import com.amazonaws.services.kms.model.transform.GetParametersForImportRequestMarshaller;
import com.amazonaws.transform.Marshaller;

@SdkInternalApi
public class GetParametersForImportRequestProtocolMarshaller
implements Marshaller<Request<GetParametersForImportRequest>, GetParametersForImportRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/").httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.GetParametersForImport").serviceName("AWSKMS").build();
    private final SdkJsonProtocolFactory protocolFactory;

    public GetParametersForImportRequestProtocolMarshaller(SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Request<GetParametersForImportRequest> marshall(GetParametersForImportRequest getParametersForImportRequest) {
        if (getParametersForImportRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            ProtocolRequestMarshaller<GetParametersForImportRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING, getParametersForImportRequest);
            protocolMarshaller.startMarshalling();
            GetParametersForImportRequestMarshaller.getInstance().marshall(getParametersForImportRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

