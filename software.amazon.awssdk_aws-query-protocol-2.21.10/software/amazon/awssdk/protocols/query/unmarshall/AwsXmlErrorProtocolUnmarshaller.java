/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.protocols.core.ExceptionMetadata
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.protocols.query.unmarshall;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.core.ExceptionMetadata;
import software.amazon.awssdk.protocols.query.internal.unmarshall.AwsXmlErrorUnmarshaller;
import software.amazon.awssdk.protocols.query.unmarshall.XmlDomParser;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.query.unmarshall.XmlErrorUnmarshaller;
import software.amazon.awssdk.utils.Pair;

@SdkProtectedApi
public final class AwsXmlErrorProtocolUnmarshaller
implements HttpResponseHandler<AwsServiceException> {
    private final AwsXmlErrorUnmarshaller awsXmlErrorUnmarshaller;
    private final Function<XmlElement, Optional<XmlElement>> errorRootExtractor;

    private AwsXmlErrorProtocolUnmarshaller(Builder builder) {
        this.errorRootExtractor = builder.errorRootExtractor;
        this.awsXmlErrorUnmarshaller = AwsXmlErrorUnmarshaller.builder().defaultExceptionSupplier(builder.defaultExceptionSupplier).exceptions(builder.exceptions).errorUnmarshaller(builder.errorUnmarshaller).build();
    }

    public AwsServiceException handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) {
        Pair<XmlElement, SdkBytes> xmlAndBytes = this.parseXml(response);
        XmlElement document = (XmlElement)xmlAndBytes.left();
        Optional<XmlElement> errorRoot = this.errorRootExtractor.apply(document);
        return this.awsXmlErrorUnmarshaller.unmarshall(document, errorRoot, Optional.of(xmlAndBytes.right()), response, executionAttributes);
    }

    private Pair<XmlElement, SdkBytes> parseXml(SdkHttpFullResponse response) {
        SdkBytes bytes = this.getResponseBytes(response);
        try {
            return Pair.of((Object)XmlDomParser.parse(bytes.asInputStream()), (Object)bytes);
        }
        catch (Exception e) {
            return Pair.of((Object)XmlElement.empty(), (Object)bytes);
        }
    }

    private SdkBytes getResponseBytes(SdkHttpFullResponse response) {
        try {
            return response.content().map(SdkBytes::fromInputStream).orElseGet(this::emptyXmlBytes);
        }
        catch (Exception e) {
            return this.emptyXmlBytes();
        }
    }

    private SdkBytes emptyXmlBytes() {
        return SdkBytes.fromUtf8String((String)"<eof/>");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private List<ExceptionMetadata> exceptions;
        private Supplier<SdkPojo> defaultExceptionSupplier;
        private Function<XmlElement, Optional<XmlElement>> errorRootExtractor;
        private XmlErrorUnmarshaller errorUnmarshaller;

        private Builder() {
        }

        public Builder exceptions(List<ExceptionMetadata> exceptions) {
            this.exceptions = exceptions;
            return this;
        }

        public Builder defaultExceptionSupplier(Supplier<SdkPojo> defaultExceptionSupplier) {
            this.defaultExceptionSupplier = defaultExceptionSupplier;
            return this;
        }

        public Builder errorRootExtractor(Function<XmlElement, Optional<XmlElement>> errorRootExtractor) {
            this.errorRootExtractor = errorRootExtractor;
            return this;
        }

        public Builder errorUnmarshaller(XmlErrorUnmarshaller errorUnmarshaller) {
            this.errorUnmarshaller = errorUnmarshaller;
            return this;
        }

        public AwsXmlErrorProtocolUnmarshaller build() {
            return new AwsXmlErrorProtocolUnmarshaller(this);
        }
    }
}

