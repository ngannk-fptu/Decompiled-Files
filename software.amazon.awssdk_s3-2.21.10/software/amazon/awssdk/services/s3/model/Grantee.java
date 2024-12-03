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
 *  software.amazon.awssdk.core.traits.XmlAttributeTrait
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
import software.amazon.awssdk.core.traits.XmlAttributeTrait;
import software.amazon.awssdk.services.s3.model.Type;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Grantee
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Grantee> {
    private static final SdkField<String> DISPLAY_NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("DisplayName").getter(Grantee.getter(Grantee::displayName)).setter(Grantee.setter(Builder::displayName)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DisplayName").unmarshallLocationName("DisplayName").build()}).build();
    private static final SdkField<String> EMAIL_ADDRESS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("EmailAddress").getter(Grantee.getter(Grantee::emailAddress)).setter(Grantee.setter(Builder::emailAddress)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("EmailAddress").unmarshallLocationName("EmailAddress").build()}).build();
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ID").getter(Grantee.getter(Grantee::id)).setter(Grantee.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ID").unmarshallLocationName("ID").build()}).build();
    private static final SdkField<String> TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Type").getter(Grantee.getter(Grantee::typeAsString)).setter(Grantee.setter(Builder::type)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("xsi:type").unmarshallLocationName("xsi:type").build(), XmlAttributeTrait.create(), RequiredTrait.create()}).build();
    private static final SdkField<String> URI_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("URI").getter(Grantee.getter(Grantee::uri)).setter(Grantee.setter(Builder::uri)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("URI").unmarshallLocationName("URI").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DISPLAY_NAME_FIELD, EMAIL_ADDRESS_FIELD, ID_FIELD, TYPE_FIELD, URI_FIELD));
    private static final long serialVersionUID = 1L;
    private final String displayName;
    private final String emailAddress;
    private final String id;
    private final String type;
    private final String uri;

    private Grantee(BuilderImpl builder) {
        this.displayName = builder.displayName;
        this.emailAddress = builder.emailAddress;
        this.id = builder.id;
        this.type = builder.type;
        this.uri = builder.uri;
    }

    public final String displayName() {
        return this.displayName;
    }

    public final String emailAddress() {
        return this.emailAddress;
    }

    public final String id() {
        return this.id;
    }

    public final Type type() {
        return Type.fromValue(this.type);
    }

    public final String typeAsString() {
        return this.type;
    }

    public final String uri() {
        return this.uri;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.displayName());
        hashCode = 31 * hashCode + Objects.hashCode(this.emailAddress());
        hashCode = 31 * hashCode + Objects.hashCode(this.id());
        hashCode = 31 * hashCode + Objects.hashCode(this.typeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.uri());
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
        if (!(obj instanceof Grantee)) {
            return false;
        }
        Grantee other = (Grantee)obj;
        return Objects.equals(this.displayName(), other.displayName()) && Objects.equals(this.emailAddress(), other.emailAddress()) && Objects.equals(this.id(), other.id()) && Objects.equals(this.typeAsString(), other.typeAsString()) && Objects.equals(this.uri(), other.uri());
    }

    public final String toString() {
        return ToString.builder((String)"Grantee").add("DisplayName", (Object)this.displayName()).add("EmailAddress", (Object)this.emailAddress()).add("ID", (Object)this.id()).add("Type", (Object)this.typeAsString()).add("URI", (Object)this.uri()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "DisplayName": {
                return Optional.ofNullable(clazz.cast(this.displayName()));
            }
            case "EmailAddress": {
                return Optional.ofNullable(clazz.cast(this.emailAddress()));
            }
            case "ID": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "Type": {
                return Optional.ofNullable(clazz.cast(this.typeAsString()));
            }
            case "URI": {
                return Optional.ofNullable(clazz.cast(this.uri()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Grantee, T> g) {
        return obj -> g.apply((Grantee)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String displayName;
        private String emailAddress;
        private String id;
        private String type;
        private String uri;

        private BuilderImpl() {
        }

        private BuilderImpl(Grantee model) {
            this.displayName(model.displayName);
            this.emailAddress(model.emailAddress);
            this.id(model.id);
            this.type(model.type);
            this.uri(model.uri);
        }

        public final String getDisplayName() {
            return this.displayName;
        }

        public final void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public final Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public final String getEmailAddress() {
            return this.emailAddress;
        }

        public final void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        @Override
        public final Builder emailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public final String getId() {
            return this.id;
        }

        public final void setId(String id) {
            this.id = id;
        }

        @Override
        public final Builder id(String id) {
            this.id = id;
            return this;
        }

        public final String getType() {
            return this.type;
        }

        public final void setType(String type) {
            this.type = type;
        }

        @Override
        public final Builder type(String type) {
            this.type = type;
            return this;
        }

        @Override
        public final Builder type(Type type) {
            this.type(type == null ? null : type.toString());
            return this;
        }

        public final String getUri() {
            return this.uri;
        }

        public final void setUri(String uri) {
            this.uri = uri;
        }

        @Override
        public final Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Grantee build() {
            return new Grantee(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Grantee> {
        public Builder displayName(String var1);

        public Builder emailAddress(String var1);

        public Builder id(String var1);

        public Builder type(String var1);

        public Builder type(Type var1);

        public Builder uri(String var1);
    }
}

