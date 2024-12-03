/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
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
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.Protocol;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Redirect
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Redirect> {
    private static final SdkField<String> HOST_NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("HostName").getter(Redirect.getter(Redirect::hostName)).setter(Redirect.setter(Builder::hostName)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("HostName").unmarshallLocationName("HostName").build()}).build();
    private static final SdkField<String> HTTP_REDIRECT_CODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("HttpRedirectCode").getter(Redirect.getter(Redirect::httpRedirectCode)).setter(Redirect.setter(Builder::httpRedirectCode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("HttpRedirectCode").unmarshallLocationName("HttpRedirectCode").build()}).build();
    private static final SdkField<String> PROTOCOL_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Protocol").getter(Redirect.getter(Redirect::protocolAsString)).setter(Redirect.setter(Builder::protocol)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Protocol").unmarshallLocationName("Protocol").build()}).build();
    private static final SdkField<String> REPLACE_KEY_PREFIX_WITH_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ReplaceKeyPrefixWith").getter(Redirect.getter(Redirect::replaceKeyPrefixWith)).setter(Redirect.setter(Builder::replaceKeyPrefixWith)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ReplaceKeyPrefixWith").unmarshallLocationName("ReplaceKeyPrefixWith").build()}).build();
    private static final SdkField<String> REPLACE_KEY_WITH_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ReplaceKeyWith").getter(Redirect.getter(Redirect::replaceKeyWith)).setter(Redirect.setter(Builder::replaceKeyWith)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ReplaceKeyWith").unmarshallLocationName("ReplaceKeyWith").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(HOST_NAME_FIELD, HTTP_REDIRECT_CODE_FIELD, PROTOCOL_FIELD, REPLACE_KEY_PREFIX_WITH_FIELD, REPLACE_KEY_WITH_FIELD));
    private static final long serialVersionUID = 1L;
    private final String hostName;
    private final String httpRedirectCode;
    private final String protocol;
    private final String replaceKeyPrefixWith;
    private final String replaceKeyWith;

    private Redirect(BuilderImpl builder) {
        this.hostName = builder.hostName;
        this.httpRedirectCode = builder.httpRedirectCode;
        this.protocol = builder.protocol;
        this.replaceKeyPrefixWith = builder.replaceKeyPrefixWith;
        this.replaceKeyWith = builder.replaceKeyWith;
    }

    public final String hostName() {
        return this.hostName;
    }

    public final String httpRedirectCode() {
        return this.httpRedirectCode;
    }

    public final Protocol protocol() {
        return Protocol.fromValue(this.protocol);
    }

    public final String protocolAsString() {
        return this.protocol;
    }

    public final String replaceKeyPrefixWith() {
        return this.replaceKeyPrefixWith;
    }

    public final String replaceKeyWith() {
        return this.replaceKeyWith;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.httpRedirectCode());
        hashCode = 31 * hashCode + Objects.hashCode(this.protocolAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.replaceKeyPrefixWith());
        hashCode = 31 * hashCode + Objects.hashCode(this.replaceKeyWith());
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
        if (!(obj instanceof Redirect)) {
            return false;
        }
        Redirect other = (Redirect)obj;
        return Objects.equals(this.hostName(), other.hostName()) && Objects.equals(this.httpRedirectCode(), other.httpRedirectCode()) && Objects.equals(this.protocolAsString(), other.protocolAsString()) && Objects.equals(this.replaceKeyPrefixWith(), other.replaceKeyPrefixWith()) && Objects.equals(this.replaceKeyWith(), other.replaceKeyWith());
    }

    public final String toString() {
        return ToString.builder((String)"Redirect").add("HostName", (Object)this.hostName()).add("HttpRedirectCode", (Object)this.httpRedirectCode()).add("Protocol", (Object)this.protocolAsString()).add("ReplaceKeyPrefixWith", (Object)this.replaceKeyPrefixWith()).add("ReplaceKeyWith", (Object)this.replaceKeyWith()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "HostName": {
                return Optional.ofNullable(clazz.cast(this.hostName()));
            }
            case "HttpRedirectCode": {
                return Optional.ofNullable(clazz.cast(this.httpRedirectCode()));
            }
            case "Protocol": {
                return Optional.ofNullable(clazz.cast(this.protocolAsString()));
            }
            case "ReplaceKeyPrefixWith": {
                return Optional.ofNullable(clazz.cast(this.replaceKeyPrefixWith()));
            }
            case "ReplaceKeyWith": {
                return Optional.ofNullable(clazz.cast(this.replaceKeyWith()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Redirect, T> g) {
        return obj -> g.apply((Redirect)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String hostName;
        private String httpRedirectCode;
        private String protocol;
        private String replaceKeyPrefixWith;
        private String replaceKeyWith;

        private BuilderImpl() {
        }

        private BuilderImpl(Redirect model) {
            this.hostName(model.hostName);
            this.httpRedirectCode(model.httpRedirectCode);
            this.protocol(model.protocol);
            this.replaceKeyPrefixWith(model.replaceKeyPrefixWith);
            this.replaceKeyWith(model.replaceKeyWith);
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

        public final String getHttpRedirectCode() {
            return this.httpRedirectCode;
        }

        public final void setHttpRedirectCode(String httpRedirectCode) {
            this.httpRedirectCode = httpRedirectCode;
        }

        @Override
        public final Builder httpRedirectCode(String httpRedirectCode) {
            this.httpRedirectCode = httpRedirectCode;
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

        public final String getReplaceKeyPrefixWith() {
            return this.replaceKeyPrefixWith;
        }

        public final void setReplaceKeyPrefixWith(String replaceKeyPrefixWith) {
            this.replaceKeyPrefixWith = replaceKeyPrefixWith;
        }

        @Override
        public final Builder replaceKeyPrefixWith(String replaceKeyPrefixWith) {
            this.replaceKeyPrefixWith = replaceKeyPrefixWith;
            return this;
        }

        public final String getReplaceKeyWith() {
            return this.replaceKeyWith;
        }

        public final void setReplaceKeyWith(String replaceKeyWith) {
            this.replaceKeyWith = replaceKeyWith;
        }

        @Override
        public final Builder replaceKeyWith(String replaceKeyWith) {
            this.replaceKeyWith = replaceKeyWith;
            return this;
        }

        public Redirect build() {
            return new Redirect(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Redirect> {
        public Builder hostName(String var1);

        public Builder httpRedirectCode(String var1);

        public Builder protocol(String var1);

        public Builder protocol(Protocol var1);

        public Builder replaceKeyPrefixWith(String var1);

        public Builder replaceKeyWith(String var1);
    }
}

