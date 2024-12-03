/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.protocols.core.ExceptionMetadata
 *  software.amazon.awssdk.protocols.query.internal.unmarshall.AwsXmlErrorUnmarshaller
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlErrorUnmarshaller
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.protocols.core.ExceptionMetadata;
import software.amazon.awssdk.protocols.query.internal.unmarshall.AwsXmlErrorUnmarshaller;
import software.amazon.awssdk.protocols.query.unmarshall.XmlErrorUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.AwsXmlUnmarshallingContext;

@SdkInternalApi
public final class AwsXmlErrorTransformer
implements Function<AwsXmlUnmarshallingContext, AwsServiceException> {
    private final AwsXmlErrorUnmarshaller awsXmlErrorUnmarshaller;

    private AwsXmlErrorTransformer(Builder builder) {
        this.awsXmlErrorUnmarshaller = AwsXmlErrorUnmarshaller.builder().defaultExceptionSupplier(builder.defaultExceptionSupplier).exceptions(builder.exceptions).errorUnmarshaller(builder.errorUnmarshaller).build();
    }

    @Override
    public AwsServiceException apply(AwsXmlUnmarshallingContext context) {
        return this.awsXmlErrorUnmarshaller.unmarshall(context.parsedRootXml(), Optional.ofNullable(context.parsedErrorXml()), Optional.empty(), context.sdkHttpFullResponse(), context.executionAttributes());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private List<ExceptionMetadata> exceptions;
        private Supplier<SdkPojo> defaultExceptionSupplier;
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

        public Builder errorUnmarshaller(XmlErrorUnmarshaller errorUnmarshaller) {
            this.errorUnmarshaller = errorUnmarshaller;
            return this;
        }

        public AwsXmlErrorTransformer build() {
            return new AwsXmlErrorTransformer(this);
        }
    }
}

