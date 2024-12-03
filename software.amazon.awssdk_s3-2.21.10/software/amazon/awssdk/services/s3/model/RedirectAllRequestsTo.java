/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.Protocol;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class RedirectAllRequestsTo
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, RedirectAllRequestsTo> {
    private static final SdkField<String> HOST_NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("HostName").getter(RedirectAllRequestsTo.getter(RedirectAllRequestsTo::hostName)).setter(RedirectAllRequestsTo.setter(Builder::hostName)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("HostName").unmarshallLocationName("HostName").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> PROTOCOL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Protocol").getter(RedirectAllRequestsTo.getter(RedirectAllRequestsTo::protocolAsString)).setter(RedirectAllRequestsTo.setter(Builder::protocol)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Protocol").unmarshallLocationName("Protocol").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(HOST_NAME_FIELD, PROTOCOL_FIELD));
    private static final long serialVersionUID = 1L;
    private final String hostName;
    private final String protocol;

    private RedirectAllRequestsTo(BuilderImpl builder) {
        this.hostName = builder.hostName;
        this.protocol = builder.protocol;
    }

    public final String hostName() {
        return this.hostName;
    }

    public final Protocol protocol() {
        return Protocol.fromValue(this.protocol);
    }

    public final String protocolAsString() {
        return this.protocol;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.hostName());
        hashCode = 31 * hashCode + Objects.hashCode(this.protocolAsString());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RedirectAllRequestsTo)) {
            return false;
        }
        RedirectAllRequestsTo other = (RedirectAllRequestsTo)obj;
        return Objects.equals(this.hostName(), other.hostName()) && Objects.equals(this.protocolAsString(), other.protocolAsString());
    }

    public final String toString() {
        return ToString.builder((String)"RedirectAllRequestsTo").add("HostName", (Object)this.hostName()).add("Protocol", (Object)this.protocolAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "HostName": {
                return Optional.ofNullable(clazz.cast(this.hostName()));
            }
            case "Protocol": {
                return Optional.ofNullable(clazz.cast(this.protocolAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RedirectAllRequestsTo, T> g) {
        return obj -> g.apply((RedirectAllRequestsTo)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String hostName;
        private String protocol;

        private BuilderImpl() {
        }

        private BuilderImpl(RedirectAllRequestsTo model) {
            this.hostName(model.hostName);
            this.protocol(model.protocol);
        }

        public final String getHostName() {
            return this.hostName;
        }

        public final void setHostName(String hostName) {
            this.hostName = hostName;
        }

        @Override
        public final Builder hostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        public final String getProtocol() {
            return this.protocol;
        }

        public final void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        @Override
        public final Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        @Override
        public final Builder protocol(Protocol protocol) {
            this.protocol(protocol == null ? null : protocol.toString());
            return this;
        }

        public RedirectAllRequestsTo build() {
            return new RedirectAllRequestsTo(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, RedirectAllRequestsTo> {
        public Builder hostName(String var1);

        public Builder protocol(String var1);

        public Builder protocol(Protocol var1);
    }
}

