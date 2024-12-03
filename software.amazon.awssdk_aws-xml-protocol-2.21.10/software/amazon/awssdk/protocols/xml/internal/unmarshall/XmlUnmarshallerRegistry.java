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
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.protocols.core.AbstractMarshallingRegistry;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlUnmarshaller;

@SdkInternalApi
public final class XmlUnmarshallerRegistry
extends AbstractMarshallingRegistry {
    private XmlUnmarshallerRegistry(Builder builder) {
        super((AbstractMarshallingRegistry.Builder)builder);
    }

    public <T> XmlUnmarshaller<Object> getUnmarshaller(MarshallLocation marshallLocation, MarshallingType<T> marshallingType) {
        return (XmlUnmarshaller)this.get(marshallLocation, marshallingType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
    extends AbstractMarshallingRegistry.Builder {
        private Builder() {
        }

        public <T> Builder payloadUnmarshaller(MarshallingType<T> marshallingType, XmlUnmarshaller<T> marshaller) {
            this.register(MarshallLocation.PAYLOAD, marshallingType, marshaller);
            return this;
        }

        public <T> Builder headerUnmarshaller(MarshallingType<T> marshallingType, XmlUnmarshaller<T> marshaller) {
            this.register(MarshallLocation.HEADER, marshallingType, marshaller);
            return this;
        }

        public <T> Builder statusCodeUnmarshaller(MarshallingType<T> marshallingType, XmlUnmarshaller<T> marshaller) {
            this.register(MarshallLocation.STATUS_CODE, marshallingType, marshaller);
            return this;
        }

        public XmlUnmarshallerRegistry build() {
            return new XmlUnmarshallerRegistry(this);
        }
    }
}

