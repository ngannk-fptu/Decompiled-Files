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
import com.amazonaws.services.kms.model.DeleteImportedKeyMaterialRequest;
import com.amazonaws.services.kms.model.transform.DeleteImportedKeyMaterialRequestMarshaller;
import com.amazonaws.transform.Marshaller;

@SdkInternalApi
public class DeleteImportedKeyMaterialRequestProtocolMarshaller
implements Marshaller<Request<DeleteImportedKeyMaterialRequest>, DeleteImportedKeyMaterialRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/").httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.DeleteImportedKeyMaterial").serviceName("AWSKMS").build();
    private final SdkJsonProtocolFactory protocolFactory;

    public DeleteImportedKeyMaterialRequestProtocolMarshaller(SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Request<DeleteImportedKeyMaterialRequest> marshall(DeleteImportedKeyMaterialRequest deleteImportedKeyMaterialRequest) {
        if (deleteImportedKeyMaterialRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            ProtocolRequestMarshaller<DeleteImportedKeyMaterialRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING, deleteImportedKeyMaterialRequest);
            protocolMarshaller.startMarshalling();
            DeleteImportedKeyMaterialRequestMarshaller.getInstance().marshall(deleteImportedKeyMaterialRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

