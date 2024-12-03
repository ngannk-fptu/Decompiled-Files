/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlProtocolUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlUnmarshallerRegistry;

@SdkInternalApi
public final class XmlUnmarshallerContext {
    private final SdkHttpFullResponse response;
    private final XmlUnmarshallerRegistry registry;
    private final XmlProtocolUnmarshaller protocolUnmarshaller;

    private XmlUnmarshallerContext(Builder builder) {
        this.response = builder.response;
        this.registry = builder.registry;
        this.protocolUnmarshaller = builder.protocolUnmarshaller;
    }

    public SdkHttpFullResponse response() {
        return this.response;
    }

    public XmlProtocolUnmarshaller protocolUnmarshaller() {
        return this.protocolUnmarshaller;
    }

    public <T> XmlUnmarshaller<Object> getUnmarshaller(MarshallLocation marshallLocation, MarshallingType<T> marshallingType) {
        return this.registry.getUnmarshaller(marshallLocation, marshallingType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SdkHttpFullResponse response;
        private XmlUnmarshallerRegistry registry;
        private XmlProtocolUnmarshaller protocolUnmarshaller;

        private Builder() {
        }

        public Builder response(SdkHttpFullResponse response) {
            this.response = response;
            return this;
        }

        public Builder registry(XmlUnmarshallerRegistry registry) {
            this.registry = registry;
            return this;
        }

        public Builder protocolUnmarshaller(XmlProtocolUnmarshaller protocolUnmarshaller) {
            this.protocolUnmarshaller = protocolUnmarshaller;
            return this;
        }

        public XmlUnmarshallerContext build() {
            return new XmlUnmarshallerContext(this);
        }
    }
}

