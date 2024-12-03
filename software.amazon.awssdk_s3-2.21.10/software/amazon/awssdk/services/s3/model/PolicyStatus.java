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

public final class PolicyStatus
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, PolicyStatus> {
    private static final SdkField<Boolean> IS_PUBLIC_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsPublic").getter(PolicyStatus.getter(PolicyStatus::isPublic)).setter(PolicyStatus.setter(Builder::isPublic)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsPublic").unmarshallLocationName("IsPublic").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(IS_PUBLIC_FIELD));
    private static final long serialVersionUID = 1L;
    private final Boolean isPublic;

    private PolicyStatus(BuilderImpl builder) {
        this.isPublic = builder.isPublic;
    }

    public final Boolean isPublic() {
        return this.isPublic;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.isPublic());
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
        if (!(obj instanceof PolicyStatus)) {
            return false;
        }
        PolicyStatus other = (PolicyStatus)obj;
        return Objects.equals(this.isPublic(), other.isPublic());
    }

    public final String toString() {
        return ToString.builder((String)"PolicyStatus").add("IsPublic", (Object)this.isPublic()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "IsPublic": {
                return Optional.ofNullable(clazz.cast(this.isPublic()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<PolicyStatus, T> g) {
        return obj -> g.apply((PolicyStatus)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Boolean isPublic;

        private BuilderImpl() {
        }

        private BuilderImpl(PolicyStatus model) {
            this.isPublic(model.isPublic);
        }

        public final Boolean getIsPublic() {
            return this.isPublic;
        }

        public final void setIsPublic(Boolean isPublic) {
            this.isPublic = isPublic;
        }

        @Override
        public final Builder isPublic(Boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public PolicyStatus build() {
            return new PolicyStatus(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, PolicyStatus> {
        public Builder isPublic(Boolean var1);
    }
}

