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
import com.amazonaws.services.kms.model.ConnectCustomKeyStoreRequest;
import com.amazonaws.services.kms.model.transform.ConnectCustomKeyStoreRequestMarshaller;
import com.amazonaws.transform.Marshaller;

@SdkInternalApi
public class ConnectCustomKeyStoreRequestProtocolMarshaller
implements Marshaller<Request<ConnectCustomKeyStoreRequest>, ConnectCustomKeyStoreRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/").httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.ConnectCustomKeyStore").serviceName("AWSKMS").build();
    private final SdkJsonProtocolFactory protocolFactory;

    public ConnectCustomKeyStoreRequestProtocolMarshaller(SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Request<ConnectCustomKeyStoreRequest> marshall(ConnectCustomKeyStoreRequest connectCustomKeyStoreRequest) {
        if (connectCustomKeyStoreRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            ProtocolRequestMarshaller<ConnectCustomKeyStoreRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING, connectCustomKeyStoreRequest);
            protocolMarshaller.startMarshalling();
            ConnectCustomKeyStoreRequestMarshaller.getInstance().marshall(connectCustomKeyStoreRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

