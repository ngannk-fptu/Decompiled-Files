/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkPublicApi
public final class HttpExecuteRequest {
    private final SdkHttpRequest request;
    private final Optional<ContentStreamProvider> contentStreamProvider;
    private final MetricCollector metricCollector;

    private HttpExecuteRequest(BuilderImpl builder) {
        this.request = builder.request;
        this.contentStreamProvider = builder.contentStreamProvider;
        this.metricCollector = builder.metricCollector;
    }

    public SdkHttpRequest httpRequest() {
        return this.request;
    }

    public Optional<ContentStreamProvider> contentStreamProvider() {
        return this.contentStreamProvider;
    }

    public Optional<MetricCollector> metricCollector() {
        return Optional.ofNullable(this.metricCollector);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    private static class BuilderImpl
    implements Builder {
        private SdkHttpRequest request;
        private Optional<ContentStreamProvider> contentStreamProvider = Optional.empty();
        private MetricCollector metricCollector;

        private BuilderImpl() {
        }

        @Override
        public Builder request(SdkHttpRequest request) {
            this.request = request;
            return this;
        }

        @Override
        public Builder contentStreamProvider(ContentStreamProvider contentStreamProvider) {
            this.contentStreamProvider = Optional.ofNullable(contentStreamProvider);
            return this;
        }

        @Override
        public Builder metricCollector(MetricCollector metricCollector) {
            this.metricCollector = metricCollector;
            return this;
        }

        @Override
        public HttpExecuteRequest build() {
            return new HttpExecuteRequest(this);
        }
    }

    public static interface Builder {
        public Builder request(SdkHttpRequest var1);

        public Builder contentStreamProvider(ContentStreamProvider var1);

        public Builder metricCollector(MetricCollector var1);

        public HttpExecuteRequest build();
    }
}

