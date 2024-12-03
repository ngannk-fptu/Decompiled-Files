/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 */
package software.amazon.awssdk.protocols.xml.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlGenerator;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshallerRegistry;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlProtocolMarshaller;

@SdkInternalApi
public final class XmlMarshallerContext {
    private final XmlGenerator xmlGenerator;
    private final XmlProtocolMarshaller protocolMarshaller;
    private final XmlMarshallerRegistry marshallerRegistry;
    private final SdkHttpFullRequest.Builder request;

    public XmlMarshallerContext(Builder builder) {
        this.xmlGenerator = builder.xmlGenerator;
        this.protocolMarshaller = builder.protocolMarshaller;
        this.marshallerRegistry = builder.marshallerRegistry;
        this.request = builder.request;
    }

    public XmlGenerator xmlGenerator() {
        return this.xmlGenerator;
    }

    public XmlProtocolMarshaller protocolMarshaller() {
        return this.protocolMarshaller;
    }

    public XmlMarshallerRegistry marshallerRegistry() {
        return this.marshallerRegistry;
    }

    public SdkHttpFullRequest.Builder request() {
        return this.request;
    }

    public void marshall(MarshallLocation marshallLocation, Object val) {
        this.marshallerRegistry.getMarshaller(marshallLocation, val).marshall(val, this, null, null);
    }

    public <T> void marshall(MarshallLocation marshallLocation, T val, String paramName, SdkField<T> sdkField) {
        this.marshallerRegistry.getMarshaller(marshallLocation, val).marshall(val, this, paramName, sdkField);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private XmlGenerator xmlGenerator;
        private XmlProtocolMarshaller protocolMarshaller;
        private XmlMarshallerRegistry marshallerRegistry;
        private SdkHttpFullRequest.Builder request;

        private Builder() {
        }

        public Builder xmlGenerator(XmlGenerator xmlGenerator) {
            this.xmlGenerator = xmlGenerator;
            return this;
        }

        public Builder protocolMarshaller(XmlProtocolMarshaller protocolMarshaller) {
            this.protocolMarshaller = protocolMarshaller;
            return this;
        }

        public Builder marshallerRegistry(XmlMarshallerRegistry marshallerRegistry) {
            this.marshallerRegistry = marshallerRegistry;
            return this;
        }

        public Builder request(SdkHttpFullRequest.Builder request) {
            this.request = request;
            return this;
        }

        public XmlMarshallerContext build() {
            return new XmlMarshallerContext(this);
        }
    }
}

