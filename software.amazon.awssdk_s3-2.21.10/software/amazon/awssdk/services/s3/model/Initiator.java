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

public final class Initiator
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Initiator> {
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ID").getter(Initiator.getter(Initiator::id)).setter(Initiator.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ID").unmarshallLocationName("ID").build()}).build();
    private static final SdkField<String> DISPLAY_NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("DisplayName").getter(Initiator.getter(Initiator::displayName)).setter(Initiator.setter(Builder::displayName)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DisplayName").unmarshallLocationName("DisplayName").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ID_FIELD, DISPLAY_NAME_FIELD));
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String displayName;

    private Initiator(BuilderImpl builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
    }

    public final String id() {
        return this.id;
    }

    public final String displayName() {
        return this.displayName;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.id());
        hashCode = 31 * hashCode + Objects.hashCode(this.displayName());
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
        if (!(obj instanceof Initiator)) {
            return false;
        }
        Initiator other = (Initiator)obj;
        return Objects.equals(this.id(), other.id()) && Objects.equals(this.displayName(), other.displayName());
    }

    public final String toString() {
        return ToString.builder((String)"Initiator").add("ID", (Object)this.id()).add("DisplayName", (Object)this.displayName()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ID": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "DisplayName": {
                return Optional.ofNullable(clazz.cast(this.displayName()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Initiator, T> g) {
        return obj -> g.apply((Initiator)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String id;
        private String displayName;

        private BuilderImpl() {
        }

        private BuilderImpl(Initiator model) {
            this.id(model.id);
            this.displayName(model.displayName);
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

        public Initiator build() {
            return new Initiator(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Initiator> {
        public Builder id(String var1);

        public Builder displayName(String var1);
    }
}

