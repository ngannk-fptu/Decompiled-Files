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
import com.amazonaws.services.kms.model.ScheduleKeyDeletionRequest;
import com.amazonaws.services.kms.model.transform.ScheduleKeyDeletionRequestMarshaller;
import com.amazonaws.transform.Marshaller;

@SdkInternalApi
public class ScheduleKeyDeletionRequestProtocolMarshaller
implements Marshaller<Request<ScheduleKeyDeletionRequest>, ScheduleKeyDeletionRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().protocol(Protocol.AWS_JSON).requestUri("/").httpMethodName(HttpMethodName.POST).hasExplicitPayloadMember(false).hasPayloadMembers(true).operationIdentifier("TrentService.ScheduleKeyDeletion").serviceName("AWSKMS").build();
    private final SdkJsonProtocolFactory protocolFactory;

    public ScheduleKeyDeletionRequestProtocolMarshaller(SdkJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Request<ScheduleKeyDeletionRequest> marshall(ScheduleKeyDeletionRequest scheduleKeyDeletionRequest) {
        if (scheduleKeyDeletionRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            ProtocolRequestMarshaller<ScheduleKeyDeletionRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING, scheduleKeyDeletionRequest);
            protocolMarshaller.startMarshalling();
            ScheduleKeyDeletionRequestMarshaller.getInstance().marshall(scheduleKeyDeletionRequest, protocolMarshaller);
            return protocolMarshaller.finishMarshalling();
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

