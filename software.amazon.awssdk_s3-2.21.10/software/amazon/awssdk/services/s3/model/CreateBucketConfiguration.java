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
import software.amazon.awssdk.services.s3.model.BucketLocationConstraint;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CreateBucketConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, CreateBucketConfiguration> {
    private static final SdkField<String> LOCATION_CONSTRAINT_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("LocationConstraint").getter(CreateBucketConfiguration.getter(CreateBucketConfiguration::locationConstraintAsString)).setter(CreateBucketConfiguration.setter(Builder::locationConstraint)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("LocationConstraint").unmarshallLocationName("LocationConstraint").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(LOCATION_CONSTRAINT_FIELD));
    private static final long serialVersionUID = 1L;
    private final String locationConstraint;

    private CreateBucketConfiguration(BuilderImpl builder) {
        this.locationConstraint = builder.locationConstraint;
    }

    public final BucketLocationConstraint locationConstraint() {
        return BucketLocationConstraint.fromValue(this.locationConstraint);
    }

    public final String locationConstraintAsString() {
        return this.locationConstraint;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.locationConstraintAsString());
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
        if (!(obj instanceof CreateBucketConfiguration)) {
            return false;
        }
        CreateBucketConfiguration other = (CreateBucketConfiguration)obj;
        return Objects.equals(this.locationConstraintAsString(), other.locationConstraintAsString());
    }

    public final String toString() {
        return ToString.builder((String)"CreateBucketConfiguration").add("LocationConstraint", (Object)this.locationConstraintAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "LocationConstraint": {
                return Optional.ofNullable(clazz.cast(this.locationConstraintAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CreateBucketConfiguration, T> g) {
        return obj -> g.apply((CreateBucketConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String locationConstraint;

        private BuilderImpl() {
        }

        private BuilderImpl(CreateBucketConfiguration model) {
            this.locationConstraint(model.locationConstraint);
        }

        public final String getLocationConstraint() {
            return this.locationConstraint;
        }

        public final void setLocationConstraint(String locationConstraint) {
            this.locationConstraint = locationConstraint;
        }

        @Override
        public final Builder locationConstraint(String locationConstraint) {
            this.locationConstraint = locationConstraint;
            return this;
        }

        @Override
        public final Builder locationConstraint(BucketLocationConstraint locationConstraint) {
            this.locationConstraint(locationConstraint == null ? null : locationConstraint.toString());
            return this;
        }

        public CreateBucketConfiguration build() {
            return new CreateBucketConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, CreateBucketConfiguration> {
        public Builder locationConstraint(String var1);

        public Builder locationConstraint(BucketLocationConstraint var1);
    }
}

