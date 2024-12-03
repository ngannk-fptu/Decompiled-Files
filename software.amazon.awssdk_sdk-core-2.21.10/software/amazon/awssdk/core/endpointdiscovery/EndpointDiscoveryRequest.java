/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.core.endpointdiscovery;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkProtectedApi
public final class EndpointDiscoveryRequest
implements ToCopyableBuilder<Builder, EndpointDiscoveryRequest> {
    private final RequestOverrideConfiguration requestOverrideConfiguration;
    private final String operationName;
    private final Map<String, String> identifiers;
    private final String cacheKey;
    private final boolean required;
    private final URI defaultEndpoint;

    private EndpointDiscoveryRequest(BuilderImpl builder) {
        this.requestOverrideConfiguration = builder.requestOverrideConfiguration;
        this.operationName = builder.operationName;
        this.identifiers = builder.identifiers;
        this.cacheKey = builder.cacheKey;
        this.required = builder.required;
        this.defaultEndpoint = builder.defaultEndpoint;
    }

    public Optional<RequestOverrideConfiguration> overrideConfiguration() {
        return Optional.ofNullable(this.requestOverrideConfiguration);
    }

    public Optional<String> operationName() {
        return Optional.ofNullable(this.operationName);
    }

    public Optional<Map<String, String>> identifiers() {
        return Optional.ofNullable(this.identifiers);
    }

    public Optional<String> cacheKey() {
        return Optional.ofNullable(this.cacheKey);
    }

    public boolean required() {
        return this.required;
    }

    public URI defaultEndpoint() {
        return this.defaultEndpoint;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static class BuilderImpl
    implements Builder {
        private RequestOverrideConfiguration requestOverrideConfiguration;
        private String operationName;
        private Map<String, String> identifiers;
        private String cacheKey;
        private boolean required = false;
        private URI defaultEndpoint;

        private BuilderImpl() {
        }

        private BuilderImpl(EndpointDiscoveryRequest request) {
            this.requestOverrideConfiguration = request.requestOverrideConfiguration;
            this.operationName = request.operationName;
            this.identifiers = request.identifiers;
            this.cacheKey = request.cacheKey;
            this.required = request.required;
            this.defaultEndpoint = request.defaultEndpoint;
        }

        @Override
        public Builder overrideConfiguration(RequestOverrideConfiguration overrideConfiguration) {
            this.requestOverrideConfiguration = overrideConfiguration;
            return this;
        }

        @Override
        public Builder operationName(String operationName) {
            this.operationName = operationName;
            return this;
        }

        @Override
        public Builder identifiers(Map<String, String> identifiers) {
            this.identifiers = identifiers;
            return this;
        }

        @Override
        public Builder cacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
            return this;
        }

        @Override
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        @Override
        public Builder defaultEndpoint(URI defaultEndpoint) {
            this.defaultEndpoint = defaultEndpoint;
            return this;
        }

        public EndpointDiscoveryRequest build() {
            return new EndpointDiscoveryRequest(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, EndpointDiscoveryRequest> {
        public Builder overrideConfiguration(RequestOverrideConfiguration var1);

        public Builder operationName(String var1);

        public Builder identifiers(Map<String, String> var1);

        public Builder cacheKey(String var1);

        public Builder required(boolean var1);

        public Builder defaultEndpoint(URI var1);
    }
}

