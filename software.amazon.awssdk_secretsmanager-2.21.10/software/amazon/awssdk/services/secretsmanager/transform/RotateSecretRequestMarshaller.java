/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.runtime.transform.Marshaller
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpMethod
 *  software.amazon.awssdk.protocols.core.OperationInfo
 *  software.amazon.awssdk.protocols.core.ProtocolMarshaller
 *  software.amazon.awssdk.protocols.json.BaseAwsJsonProtocolFactory
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.secretsmanager.transform;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.runtime.transform.Marshaller;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.protocols.json.BaseAwsJsonProtocolFactory;
import software.amazon.awssdk.services.secretsmanager.model.RotateSecretRequest;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class RotateSecretRequestMarshaller
implements Marshaller<RotateSecretRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().requestUri("/").httpMethod(SdkHttpMethod.POST).hasExplicitPayloadMember(false).hasImplicitPayloadMembers(true).hasPayloadMembers(true).operationIdentifier("secretsmanager.RotateSecret").build();
    private final BaseAwsJsonProtocolFactory protocolFactory;

    public RotateSecretRequestMarshaller(BaseAwsJsonProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    public SdkHttpFullRequest marshall(RotateSecretRequest rotateSecretRequest) {
        Validate.paramNotNull((Object)((Object)rotateSecretRequest), (String)"rotateSecretRequest");
        try {
            ProtocolMarshaller protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING);
            return (SdkHttpFullRequest)protocolMarshaller.marshall((SdkPojo)rotateSecretRequest);
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to marshall request to JSON: " + e.getMessage()).cause((Throwable)e).build();
        }
    }
}

