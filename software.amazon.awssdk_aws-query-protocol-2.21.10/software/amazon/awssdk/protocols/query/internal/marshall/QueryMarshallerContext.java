/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 */
package software.amazon.awssdk.protocols.query.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshallerRegistry;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryProtocolMarshaller;

@SdkInternalApi
public final class QueryMarshallerContext {
    private final QueryProtocolMarshaller protocolHandler;
    private final QueryMarshallerRegistry marshallerRegistry;
    private final SdkHttpFullRequest.Builder request;

    private QueryMarshallerContext(Builder builder) {
        this.protocolHandler = builder.protocolHandler;
        this.marshallerRegistry = builder.marshallerRegistry;
        this.request = builder.request;
    }

    public QueryProtocolMarshaller protocolHandler() {
        return this.protocolHandler;
    }

    public QueryMarshallerRegistry marshallerRegistry() {
        return this.marshallerRegistry;
    }

    public SdkHttpFullRequest.Builder request() {
        return this.request;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private QueryProtocolMarshaller protocolHandler;
        private QueryMarshallerRegistry marshallerRegistry;
        private SdkHttpFullRequest.Builder request;

        private Builder() {
        }

        public Builder protocolHandler(QueryProtocolMarshaller protocolHandler) {
            this.protocolHandler = protocolHandler;
            return this;
        }

        public Builder marshallerRegistry(QueryMarshallerRegistry marshallerRegistry) {
            this.marshallerRegistry = marshallerRegistry;
            return this;
        }

        public Builder request(SdkHttpFullRequest.Builder request) {
            this.request = request;
            return this;
        }

        public QueryMarshallerContext build() {
            return new QueryMarshallerContext(this);
        }
    }
}

