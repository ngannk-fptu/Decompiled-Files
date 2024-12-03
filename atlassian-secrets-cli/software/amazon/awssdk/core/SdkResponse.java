/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.http.SdkHttpResponse;

@Immutable
@SdkPublicApi
public abstract class SdkResponse
implements SdkPojo {
    private final SdkHttpResponse sdkHttpResponse;

    protected SdkResponse(Builder builder) {
        this.sdkHttpResponse = builder.sdkHttpResponse();
    }

    public SdkHttpResponse sdkHttpResponse() {
        return this.sdkHttpResponse;
    }

    public <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        return Optional.empty();
    }

    public abstract Builder toBuilder();

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SdkResponse that = (SdkResponse)o;
        return Objects.equals(this.sdkHttpResponse, that.sdkHttpResponse);
    }

    public int hashCode() {
        return Objects.hashCode(this.sdkHttpResponse);
    }

    protected static abstract class BuilderImpl
    implements Builder {
        private SdkHttpResponse sdkHttpResponse;

        protected BuilderImpl() {
        }

        protected BuilderImpl(SdkResponse response) {
            this.sdkHttpResponse = response.sdkHttpResponse();
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
    }

    public static interface Builder {
        public Builder sdkHttpResponse(SdkHttpResponse var1);

        public SdkHttpResponse sdkHttpResponse();

        public SdkResponse build();
    }
}

