/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.presigner;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public abstract class PresignedRequest {
    private final URL url;
    private final Instant expiration;
    private final boolean isBrowserExecutable;
    private final Map<String, List<String>> signedHeaders;
    private final SdkBytes signedPayload;
    private final SdkHttpRequest httpRequest;

    protected PresignedRequest(DefaultBuilder<?> builder) {
        this.expiration = Validate.notNull(((DefaultBuilder)builder).expiration, "expiration", new Object[0]);
        this.isBrowserExecutable = Validate.notNull(((DefaultBuilder)builder).isBrowserExecutable, "isBrowserExecutable", new Object[0]);
        this.signedHeaders = Validate.notEmpty(((DefaultBuilder)builder).signedHeaders, "signedHeaders", new Object[0]);
        this.signedPayload = ((DefaultBuilder)builder).signedPayload;
        this.httpRequest = Validate.notNull(((DefaultBuilder)builder).httpRequest, "httpRequest", new Object[0]);
        this.url = FunctionalUtils.invokeSafely(this.httpRequest.getUri()::toURL);
    }

    public URL url() {
        return this.url;
    }

    public Instant expiration() {
        return this.expiration;
    }

    public boolean isBrowserExecutable() {
        return this.isBrowserExecutable;
    }

    public Map<String, List<String>> signedHeaders() {
        return this.signedHeaders;
    }

    public Optional<SdkBytes> signedPayload() {
        return Optional.ofNullable(this.signedPayload);
    }

    public SdkHttpRequest httpRequest() {
        return this.httpRequest;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PresignedRequest that = (PresignedRequest)o;
        if (this.isBrowserExecutable != that.isBrowserExecutable) {
            return false;
        }
        if (!this.expiration.equals(that.expiration)) {
            return false;
        }
        if (!this.signedHeaders.equals(that.signedHeaders)) {
            return false;
        }
        if (this.signedPayload != null ? !this.signedPayload.equals(that.signedPayload) : that.signedPayload != null) {
            return false;
        }
        return this.httpRequest.equals(that.httpRequest);
    }

    public int hashCode() {
        int result = this.expiration.hashCode();
        result = 31 * result + (this.isBrowserExecutable ? 1 : 0);
        result = 31 * result + this.signedHeaders.hashCode();
        result = 31 * result + (this.signedPayload != null ? this.signedPayload.hashCode() : 0);
        result = 31 * result + this.httpRequest.hashCode();
        return result;
    }

    @SdkProtectedApi
    protected static abstract class DefaultBuilder<B extends DefaultBuilder<B>>
    implements Builder {
        private Instant expiration;
        private Boolean isBrowserExecutable;
        private Map<String, List<String>> signedHeaders;
        private SdkBytes signedPayload;
        private SdkHttpRequest httpRequest;

        protected DefaultBuilder() {
        }

        protected DefaultBuilder(PresignedRequest request) {
            this.expiration = request.expiration;
            this.isBrowserExecutable = request.isBrowserExecutable;
            this.signedHeaders = request.signedHeaders;
            this.signedPayload = request.signedPayload;
            this.httpRequest = request.httpRequest;
        }

        public B expiration(Instant expiration) {
            this.expiration = expiration;
            return this.thisBuilder();
        }

        public B isBrowserExecutable(Boolean isBrowserExecutable) {
            this.isBrowserExecutable = isBrowserExecutable;
            return this.thisBuilder();
        }

        public B signedHeaders(Map<String, List<String>> signedHeaders) {
            this.signedHeaders = signedHeaders;
            return this.thisBuilder();
        }

        public B signedPayload(SdkBytes signedPayload) {
            this.signedPayload = signedPayload;
            return this.thisBuilder();
        }

        public B httpRequest(SdkHttpRequest httpRequest) {
            this.httpRequest = httpRequest;
            return this.thisBuilder();
        }

        private B thisBuilder() {
            return (B)this;
        }
    }

    @SdkPublicApi
    public static interface Builder {
        public Builder expiration(Instant var1);

        public Builder isBrowserExecutable(Boolean var1);

        public Builder signedHeaders(Map<String, List<String>> var1);

        public Builder signedPayload(SdkBytes var1);

        public Builder httpRequest(SdkHttpRequest var1);

        public PresignedRequest build();
    }
}

