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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.S3Location;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class OutputLocation
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, OutputLocation> {
    private static final SdkField<S3Location> S3_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("S3").getter(OutputLocation.getter(OutputLocation::s3)).setter(OutputLocation.setter(Builder::s3)).constructor(S3Location::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("S3").unmarshallLocationName("S3").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(S3_FIELD));
    private static final long serialVersionUID = 1L;
    private final S3Location s3;

    private OutputLocation(BuilderImpl builder) {
        this.s3 = builder.s3;
    }

    public final S3Location s3() {
        return this.s3;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.s3());
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
        if (!(obj instanceof OutputLocation)) {
            return false;
        }
        OutputLocation other = (OutputLocation)obj;
        return Objects.equals(this.s3(), other.s3());
    }

    public final String toString() {
        return ToString.builder((String)"OutputLocation").add("S3", (Object)this.s3()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "S3": {
                return Optional.ofNullable(clazz.cast(this.s3()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<OutputLocation, T> g) {
        return obj -> g.apply((OutputLocation)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private S3Location s3;

        private BuilderImpl() {
        }

        private BuilderImpl(OutputLocation model) {
            this.s3(model.s3);
        }

        public final S3Location.Builder getS3() {
            return this.s3 != null ? this.s3.toBuilder() : null;
        }

        public final void setS3(S3Location.BuilderImpl s3) {
            this.s3 = s3 != null ? s3.build() : null;
        }

        @Override
        public final Builder s3(S3Location s3) {
            this.s3 = s3;
            return this;
        }

        public OutputLocation build() {
            return new OutputLocation(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, OutputLocation> {
        public Builder s3(S3Location var1);

        default public Builder s3(Consumer<S3Location.Builder> s3) {
            return this.s3((S3Location)((S3Location.Builder)S3Location.builder().applyMutation(s3)).build());
        }
    }
}

