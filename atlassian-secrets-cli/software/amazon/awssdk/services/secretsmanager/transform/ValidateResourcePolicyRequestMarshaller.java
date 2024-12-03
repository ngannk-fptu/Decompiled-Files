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
import software.amazon.awssdk.services.secretsmanager.model.ValidateResourcePolicyRequest;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class ValidateResourcePolicyRequestMarshaller
implements Marshaller<ValidateResourcePolicyRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().requestUri("/").httpMethod(SdkHttpMethod.POST).hasExplicitPayloadMember(false).hasImplicitPayloadMembers(true).hasPayloadMembers(true).operationIdentifier("secretsmanager.ValidateResourcePolicy").build();
    private final BaseAwsJsonProtocolFactory protocolFactory;

    public ValidateResourcePolicyRequestMarshaller(BaseAwsJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    @Override
    public SdkHttpFullRequest marshall(ValidateResourcePolicyRequest validateResourcePolicyRequest) {
        Validate.paramNotNull(validateResourcePolicyRequest, "validateResourcePolicyRequest");
        try {
            ProtocolMarshaller<SdkHttpFullRequest> protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING);
            return protocolMarshaller.marshall(validateResourcePolicyRequest);
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to marshall request to JSON: " + e.getMessage()).cause(e).build();
        }
    }
}

