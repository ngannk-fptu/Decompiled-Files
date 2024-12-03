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
 *  software.amazon.awssdk.protocols.xml.AwsXmlProtocolFactory
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.transform;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.runtime.transform.Marshaller;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.protocols.xml.AwsXmlProtocolFactory;
import software.amazon.awssdk.services.s3.model.PutBucketTaggingRequest;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class PutBucketTaggingRequestMarshaller
implements Marshaller<PutBucketTaggingRequest> {
    private static final OperationInfo SDK_OPERATION_BINDING = OperationInfo.builder().requestUri("?tagging").httpMethod(SdkHttpMethod.PUT).hasExplicitPayloadMember(true).hasPayloadMembers(true).putAdditionalMetadata(AwsXmlProtocolFactory.ROOT_MARSHALL_LOCATION_ATTRIBUTE, null).putAdditionalMetadata(AwsXmlProtocolFactory.XML_NAMESPACE_ATTRIBUTE, (Object)"http://s3.amazonaws.com/doc/2006-03-01/").build();
    private final AwsXmlProtocolFactory protocolFactory;

    public PutBucketTaggingRequestMarshaller(AwsXmlProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    public SdkHttpFullRequest marshall(PutBucketTaggingRequest putBucketTaggingRequest) {
        Validate.paramNotNull((Object)((Object)putBucketTaggingRequest), (String)"putBucketTaggingRequest");
        try {
            ProtocolMarshaller protocolMarshaller = this.protocolFactory.createProtocolMarshaller(SDK_OPERATION_BINDING);
            return (SdkHttpFullRequest)protocolMarshaller.marshall((SdkPojo)putBucketTaggingRequest);
        }
        catch (Exception e) {
            throw SdkClientException.builder().message("Unable to marshall request to JSON: " + e.getMessage()).cause((Throwable)e).build();
        }
    }
}

