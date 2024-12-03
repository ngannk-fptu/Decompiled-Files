/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.awscore.exception;

import java.io.Serializable;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.ToString;

@SdkPublicApi
public class AwsErrorDetails
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String errorMessage;
    private final String errorCode;
    private final String serviceName;
    private final SdkHttpResponse sdkHttpResponse;
    private final SdkBytes rawResponse;

    protected AwsErrorDetails(Builder b) {
        this.errorMessage = b.errorMessage();
        this.errorCode = b.errorCode();
        this.serviceName = b.serviceName();
        this.sdkHttpResponse = b.sdkHttpResponse();
        this.rawResponse = b.rawResponse();
    }

    public String serviceName() {
        return this.serviceName;
    }

    public String errorMessage() {
        return this.errorMessage;
    }

    public String errorCode() {
        return this.errorCode;
    }

    public SdkBytes rawResponse() {
        return this.rawResponse;
    }

    public SdkHttpResponse sdkHttpResponse() {
        return this.sdkHttpResponse;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AwsErrorDetails that = (AwsErrorDetails)o;
        return Objects.equals(this.errorMessage, that.errorMessage) && Objects.equals(this.errorCode, that.errorCode) && Objects.equals(this.serviceName, that.serviceName) && Objects.equals(this.sdkHttpResponse, that.sdkHttpResponse) && Objects.equals(this.rawResponse, that.rawResponse);
    }

    public int hashCode() {
        int result = Objects.hashCode(this.errorMessage);
        result = 31 * result + Objects.hashCode(this.errorCode);
        result = 31 * result + Objects.hashCode(this.serviceName);
        result = 31 * result + Objects.hashCode(this.sdkHttpResponse);
        result = 31 * result + Objects.hashCode(this.rawResponse);
        return result;
    }

    public String toString() {
        return ToString.builder((String)"AwsErrorDetails").add("errorMessage", (Object)this.errorMessage).add("errorCode", (Object)this.errorCode).add("serviceName", (Object)this.serviceName).build();
    }

    protected static final class BuilderImpl
    implements Builder {
        private String errorMessage;
        private String errorCode;
        private String serviceName;
        private SdkHttpResponse sdkHttpResponse;
        private SdkBytes rawResponse;

        private BuilderImpl() {
        }

        private BuilderImpl(AwsErrorDetails awsErrorDetails) {
            this.errorMessage = awsErrorDetails.errorMessage();
            this.errorCode = awsErrorDetails.errorCode();
            this.serviceName = awsErrorDetails.serviceName();
            this.sdkHttpResponse = awsErrorDetails.sdkHttpResponse();
            this.rawResponse = awsErrorDetails.rawResponse();
        }

        @Override
        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        @Override
        public String errorMessage() {
            return this.errorMessage;
        }

        @Override
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        @Override
        public String errorCode() {
            return this.errorCode;
        }

        @Override
        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        @Override
        public String serviceName() {
            return this.serviceName;
        }

        @Override
        public Builder sdkHttpResponse(SdkHttpResponse sdkHttpResponse) {
            this.sdkHttpResponse = sdkHttpResponse;
            return this;
        }

        @Override
        public SdkHttpResponse sdkHttpResponse() {
            return this.sdkHttpResponse;
        }

        @Override
        public Builder rawResponse(SdkBytes rawResponse) {
            this.rawResponse = rawResponse;
            return this;
        }

        @Override
        public SdkBytes rawResponse() {
            return this.rawResponse;
        }

        @Override
        public AwsErrorDetails build() {
            return new AwsErrorDetails(this);
        }
    }

    public static interface Builder {
        public Builder errorMessage(String var1);

        public String errorMessage();

        public Builder errorCode(String var1);

        public String errorCode();

        public Builder serviceName(String var1);

        public String serviceName();

        public Builder sdkHttpResponse(SdkHttpResponse var1);

        public SdkHttpResponse sdkHttpResponse();

        public Builder rawResponse(SdkBytes var1);

        public SdkBytes rawResponse();

        public AwsErrorDetails build();
    }
}

