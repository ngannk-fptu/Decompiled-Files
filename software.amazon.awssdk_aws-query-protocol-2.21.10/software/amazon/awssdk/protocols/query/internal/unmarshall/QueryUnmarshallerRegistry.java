/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.protocols.core.AbstractMarshallingRegistry
 *  software.amazon.awssdk.protocols.core.AbstractMarshallingRegistry$Builder
 */
package software.amazon.awssdk.protocols.query.internal.unmarshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.protocols.core.AbstractMarshallingRegistry;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryUnmarshaller;

@SdkInternalApi
public final class QueryUnmarshallerRegistry
extends AbstractMarshallingRegistry {
    private QueryUnmarshallerRegistry(Builder builder) {
        super((AbstractMarshallingRegistry.Builder)builder);
    }

    public <T> QueryUnmarshaller<Object> getUnmarshaller(MarshallLocation marshallLocation, MarshallingType<T> marshallingType) {
        return (QueryUnmarshaller)super.get(marshallLocation, marshallingType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
    extends AbstractMarshallingRegistry.Builder {
        private Builder() {
        }

        public <T> Builder unmarshaller(MarshallingType<T> marshallingType, QueryUnmarshaller<T> marshaller) {
            this.register(MarshallLocation.PAYLOAD, marshallingType, marshaller);
            return this;
        }

        public QueryUnmarshallerRegistry build() {
            return new QueryUnmarshallerRegistry(this);
        }
    }
}

