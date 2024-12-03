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
package software.amazon.awssdk.protocols.xml.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.protocols.core.AbstractMarshallingRegistry;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshaller;

@SdkInternalApi
public final class XmlMarshallerRegistry
extends AbstractMarshallingRegistry {
    private XmlMarshallerRegistry(Builder builder) {
        super((AbstractMarshallingRegistry.Builder)builder);
    }

    public <T> XmlMarshaller<T> getMarshaller(MarshallLocation marshallLocation, T val) {
        return (XmlMarshaller)this.get(marshallLocation, this.toMarshallingType(val));
    }

    public <T> XmlMarshaller<Object> getMarshaller(MarshallLocation marshallLocation, MarshallingType<T> marshallingType, Object val) {
        return (XmlMarshaller)this.get(marshallLocation, val == null ? MarshallingType.NULL : marshallingType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
    extends AbstractMarshallingRegistry.Builder {
        private Builder() {
        }

        public <T> Builder payloadMarshaller(MarshallingType<T> marshallingType, XmlMarshaller<T> marshaller) {
            this.register(MarshallLocation.PAYLOAD, marshallingType, marshaller);
            return this;
        }

        public <T> Builder headerMarshaller(MarshallingType<T> marshallingType, XmlMarshaller<T> marshaller) {
            this.register(MarshallLocation.HEADER, marshallingType, marshaller);
            return this;
        }

        public <T> Builder queryParamMarshaller(MarshallingType<T> marshallingType, XmlMarshaller<T> marshaller) {
            this.register(MarshallLocation.QUERY_PARAM, marshallingType, marshaller);
            return this;
        }

        public <T> Builder pathParamMarshaller(MarshallingType<T> marshallingType, XmlMarshaller<T> marshaller) {
            this.register(MarshallLocation.PATH, marshallingType, marshaller);
            return this;
        }

        public <T> Builder greedyPathParamMarshaller(MarshallingType<T> marshallingType, XmlMarshaller<T> marshaller) {
            this.register(MarshallLocation.GREEDY_PATH, marshallingType, marshaller);
            return this;
        }

        public XmlMarshallerRegistry build() {
            return new XmlMarshallerRegistry(this);
        }
    }
}

