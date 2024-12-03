/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.transform;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.runtime.transform.Marshaller;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.protocols.json.BaseAwsJsonProtocolFactory;
import software.amazon.awssdk.services.secretsmanager.model.StopReplicationToReplicaRequest;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class StopReplicationToReplicaRequestMarshaller
implements Marshaller<StopReplicationToReplicaRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().requestUri("/").httpMethod(SdkHttpMethod.POST).hasExplicitPayloadMember(false).hasImplicitPayloadMembers(true).hasPayloadMembers(true).operationIdentifier("secretsmanager.StopReplicationToReplica").build();
    private final BaseAwsJsonProtocolFactory protocolFactory;

    public StopReplicationToReplicaRequestMarshaller(BaseAwsJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public SdkHttpFullRequest marshall(StopReplicationToReplicaRequest stopReplicationToReplicaRequest) {
        Validate.paramNotNull(stopReplicationToReplicaRequest, "stopReplicationToReplicaRequest");
        try {
            ProtocolMarshaller<SdkHttpFullRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING);
            return protocolMarshaller.marshall(stopReplicationToReplicaRequest);
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to marshall request to JSON: " + e.getMessage()).cause(e).build();
        }
    }
}

