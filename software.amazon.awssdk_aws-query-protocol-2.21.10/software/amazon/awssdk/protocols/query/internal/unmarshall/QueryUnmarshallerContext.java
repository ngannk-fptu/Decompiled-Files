/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 */
package software.amazon.awssdk.protocols.query.internal.unmarshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryProtocolUnmarshaller;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshaller;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshallerRegistry;

@SdkInternalApi
public final class QueryUnmarshallerContext {
    private final QueryUnmarshallerRegistry registry;
    private final QueryProtocolUnmarshaller protocolUnmarshaller;

    private QueryUnmarshallerContext(Builder builder) {
        this.registry = builder.registry;
        this.protocolUnmarshaller = builder.protocolUnmarshaller;
    }

    public QueryProtocolUnmarshaller protocolUnmarshaller() {
        return this.protocolUnmarshaller;
    }

    public <T> QueryUnmarshaller<Object> getUnmarshaller(MarshallLocation marshallLocation, MarshallingType<T> marshallingType) {
        return this.registry.getUnmarshaller(marshallLocation, marshallingType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private QueryUnmarshallerRegistry registry;
        private QueryProtocolUnmarshaller protocolUnmarshaller;

        private Builder() {
        }

        public Builder registry(QueryUnmarshallerRegistry registry) {
            this.registry = registry;
            return this;
        }

        public Builder protocolUnmarshaller(QueryProtocolUnmarshaller protocolUnmarshaller) {
            this.protocolUnmarshaller = protocolUnmarshaller;
            return this;
        }

        public QueryUnmarshallerContext build() {
            return new QueryUnmarshallerContext(this);
        }
    }
}

