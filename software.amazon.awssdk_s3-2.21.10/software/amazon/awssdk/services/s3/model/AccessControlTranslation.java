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
import software.amazon.awssdk.services.s3.model.OwnerOverride;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class AccessControlTranslation
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, AccessControlTranslation> {
    private static final SdkField<String> OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Owner").getter(AccessControlTranslation.getter(AccessControlTranslation::ownerAsString)).setter(AccessControlTranslation.setter(Builder::owner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Owner").unmarshallLocationName("Owner").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(OWNER_FIELD));
    private static final long serialVersionUID = 1L;
    private final String owner;

    private AccessControlTranslation(BuilderImpl builder) {
        this.owner = builder.owner;
    }

    public final OwnerOverride owner() {
        return OwnerOverride.fromValue(this.owner);
    }

    public final String ownerAsString() {
        return this.owner;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.ownerAsString());
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
        if (!(obj instanceof AccessControlTranslation)) {
            return false;
        }
        AccessControlTranslation other = (AccessControlTranslation)obj;
        return Objects.equals(this.ownerAsString(), other.ownerAsString());
    }

    public final String toString() {
        return ToString.builder((String)"AccessControlTranslation").add("Owner", (Object)this.ownerAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Owner": {
                return Optional.ofNullable(clazz.cast(this.ownerAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<AccessControlTranslation, T> g) {
        return obj -> g.apply((AccessControlTranslation)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String owner;

        private BuilderImpl() {
        }

        private BuilderImpl(AccessControlTranslation model) {
            this.owner(model.owner);
        }

        public final String getOwner() {
            return this.owner;
        }

        public final void setOwner(String owner) {
            this.owner = owner;
        }

        @Override
        public final Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        @Override
        public final Builder owner(OwnerOverride owner) {
            this.owner(owner == null ? null : owner.toString());
            return this;
        }

        public AccessControlTranslation build() {
            return new AccessControlTranslation(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, AccessControlTranslation> {
        public Builder owner(String var1);

        public Builder owner(OwnerOverride var1);
    }
}

