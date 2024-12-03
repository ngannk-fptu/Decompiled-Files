/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.awscore.AwsResponse
 *  software.amazon.awssdk.core.Response
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlElement
 */
package software.amazon.awssdk.protocols.xml;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.xml.AwsXmlProtocolFactory;
import software.amazon.awssdk.protocols.xml.XmlOperationMetadata;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.AwsXmlPredicatedResponseHandler;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.DecorateErrorFromResponseBodyUnmarshaller;

@SdkProtectedApi
public final class AwsS3ProtocolFactory
extends AwsXmlProtocolFactory {
    private AwsS3ProtocolFactory(Builder builder) {
        super(builder);
    }

    @Override
    Optional<XmlElement> getErrorRoot(XmlElement document) {
        return Optional.of(document);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T extends AwsResponse> HttpResponseHandler<Response<T>> createCombinedResponseHandler(Supplier<SdkPojo> pojoSupplier, XmlOperationMetadata staxOperationMetadata) {
        return this.createErrorCouldBeInBodyResponseHandler(pojoSupplier, staxOperationMetadata);
    }

    private <T extends AwsResponse> HttpResponseHandler<Response<T>> createErrorCouldBeInBodyResponseHandler(Supplier<SdkPojo> pojoSupplier, XmlOperationMetadata staxOperationMetadata) {
        return new AwsXmlPredicatedResponseHandler(r -> (SdkPojo)pojoSupplier.get(), this.createResponseTransformer(pojoSupplier), this.createErrorTransformer(), DecorateErrorFromResponseBodyUnmarshaller.of(this::getErrorRoot), staxOperationMetadata.isHasStreamingSuccessResponse());
    }

    public static final class Builder
    extends AwsXmlProtocolFactory.Builder<Builder> {
        private Builder() {
        }

        @Override
        public AwsS3ProtocolFactory build() {
            return new AwsS3ProtocolFactory(this);
        }
    }
}

