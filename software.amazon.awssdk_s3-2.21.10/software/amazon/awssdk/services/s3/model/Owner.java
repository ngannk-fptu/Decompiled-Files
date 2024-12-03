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
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Owner
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Owner> {
    private static final SdkField<String> DISPLAY_NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("DisplayName").getter(Owner.getter(Owner::displayName)).setter(Owner.setter(Builder::displayName)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DisplayName").unmarshallLocationName("DisplayName").build()}).build();
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ID").getter(Owner.getter(Owner::id)).setter(Owner.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ID").unmarshallLocationName("ID").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DISPLAY_NAME_FIELD, ID_FIELD));
    private static final long serialVersionUID = 1L;
    private final String displayName;
    private final String id;

    private Owner(BuilderImpl builder) {
        this.displayName = builder.displayName;
        this.id = builder.id;
    }

    public final String displayName() {
        return this.displayName;
    }

    public final String id() {
        return this.id;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.id());
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
        if (!(obj instanceof Owner)) {
            return false;
        }
        Owner other = (Owner)obj;
        return Objects.equals(this.displayName(), other.displayName()) && Objects.equals(this.id(), other.id());
    }

    public final String toString() {
        return ToString.builder((String)"Owner").add("DisplayName", (Object)this.displayName()).add("ID", (Object)this.id()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "DisplayName": {
                return Optional.ofNullable(clazz.cast(this.displayName()));
            }
            case "ID": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Owner, T> g) {
        return obj -> g.apply((Owner)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String displayName;
        private String id;

        private BuilderImpl() {
        }

        private BuilderImpl(Owner model) {
            this.displayName(model.displayName);
            this.id(model.id);
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

        public Owner build() {
            return new Owner(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Owner> {
        public Builder displayName(String var1);

        public Builder id(String var1);
    }
}

