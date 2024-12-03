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
package software.amazon.awssdk.protocols.query.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.protocols.core.AbstractMarshallingRegistry;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshaller;

@SdkInternalApi
public final class QueryMarshallerRegistry
extends AbstractMarshallingRegistry {
    private QueryMarshallerRegistry(Builder builder) {
        super((AbstractMarshallingRegistry.Builder)builder);
    }

    public <T> QueryMarshaller<Object> getMarshaller(T val) {
        MarshallingType marshallingType = this.toMarshallingType(val);
        return (QueryMarshaller)this.get(MarshallLocation.PAYLOAD, marshallingType);
    }

    public <T> QueryMarshaller<Object> getMarshaller(MarshallingType<T> marshallingType, Object val) {
        return (QueryMarshaller)this.get(MarshallLocation.PAYLOAD, val == null ? MarshallingType.NULL : marshallingType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    extends AbstractMarshallingRegistry.Builder {
        private Builder() {
        }

        public <T> Builder marshaller(MarshallingType<T> marshallingType, QueryMarshaller<T> marshaller) {
            this.register(MarshallLocation.PAYLOAD, marshallingType, marshaller);
            return this;
        }

        public QueryMarshallerRegistry build() {
            return new QueryMarshallerRegistry(this);
        }
    }
}

