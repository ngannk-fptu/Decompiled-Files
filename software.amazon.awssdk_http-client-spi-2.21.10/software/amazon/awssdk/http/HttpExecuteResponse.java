/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.http;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkPublicApi
public class HttpExecuteResponse {
    private final SdkHttpResponse response;
    private final Optional<AbortableInputStream> responseBody;

    private HttpExecuteResponse(BuilderImpl builder) {
        this.response = builder.response;
        this.responseBody = builder.responseBody;
    }

    public SdkHttpResponse httpResponse() {
        return this.response;
    }

    public Optional<AbortableInputStream> responseBody() {
        return this.responseBody;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    private static class BuilderImpl
    implements Builder {
        private SdkHttpResponse response;
        private Optional<AbortableInputStream> responseBody = Optional.empty();

        private BuilderImpl() {
        }

        @Override
        public Builder response(SdkHttpResponse response) {
            this.response = response;
            return this;
        }

        @Override
        public Builder responseBody(AbortableInputStream responseBody) {
            this.responseBody = Optional.ofNullable(responseBody);
            return this;
        }

        @Override
        public HttpExecuteResponse build() {
            return new HttpExecuteResponse(this);
        }
    }

    public static interface Builder {
        public Builder response(SdkHttpResponse var1);

        public Builder responseBody(AbortableInputStream var1);

        public HttpExecuteResponse build();
    }
}

