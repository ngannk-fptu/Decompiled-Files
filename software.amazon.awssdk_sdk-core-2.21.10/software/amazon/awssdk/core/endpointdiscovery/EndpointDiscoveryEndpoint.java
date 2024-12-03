/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.core.endpointdiscovery;

import java.net.URI;
import java.time.Instant;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkInternalApi
public final class EndpointDiscoveryEndpoint
implements ToCopyableBuilder<Builder, EndpointDiscoveryEndpoint> {
    private final URI endpoint;
    private final Instant expirationTime;

    private EndpointDiscoveryEndpoint(BuilderImpl builder) {
        this.endpoint = builder.endpoint;
        this.expirationTime = builder.expirationTime;
    }

    public URI endpoint() {
        return this.endpoint;
    }

    public Instant expirationTime() {
        return this.expirationTime;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public Builder toBuilder() {
        return EndpointDiscoveryEndpoint.builder().endpoint(this.endpoint).expirationTime(this.expirationTime);
    }

    private static final class BuilderImpl
    implements Builder {
        private URI endpoint;
        private Instant expirationTime;

        private BuilderImpl() {
        }

        @Override
        public Builder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public void setEndpoint(URI endpoint) {
            this.endpoint(endpoint);
        }

        @Override
        public Builder expirationTime(Instant expirationTime) {
            this.expirationTime = expirationTime;
            return this;
        }

        public void setExpirationTime(Instant expirationTime) {
            this.expirationTime(expirationTime);
        }

        @Override
        public EndpointDiscoveryEndpoint build() {
            return new EndpointDiscoveryEndpoint(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, EndpointDiscoveryEndpoint> {
        public Builder endpoint(URI var1);

        public Builder expirationTime(Instant var1);

        public EndpointDiscoveryEndpoint build();
    }
}

