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
import java.time.Instant;
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

public final class Bucket
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Bucket> {
    private static final SdkField<String> NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Name").getter(Bucket.getter(Bucket::name)).setter(Bucket.setter(Builder::name)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").unmarshallLocationName("Name").build()}).build();
    private static final SdkField<Instant> CREATION_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("CreationDate").getter(Bucket.getter(Bucket::creationDate)).setter(Bucket.setter(Builder::creationDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CreationDate").unmarshallLocationName("CreationDate").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(NAME_FIELD, CREATION_DATE_FIELD));
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Instant creationDate;

    private Bucket(BuilderImpl builder) {
        this.name = builder.name;
        this.creationDate = builder.creationDate;
    }

    public final String name() {
        return this.name;
    }

    public final Instant creationDate() {
        return this.creationDate;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.name());
        hashCode = 31 * hashCode + Objects.hashCode(this.creationDate());
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
        if (!(obj instanceof Bucket)) {
            return false;
        }
        Bucket other = (Bucket)obj;
        return Objects.equals(this.name(), other.name()) && Objects.equals(this.creationDate(), other.creationDate());
    }

    public final String toString() {
        return ToString.builder((String)"Bucket").add("Name", (Object)this.name()).add("CreationDate", (Object)this.creationDate()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Name": {
                return Optional.ofNullable(clazz.cast(this.name()));
            }
            case "CreationDate": {
                return Optional.ofNullable(clazz.cast(this.creationDate()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Bucket, T> g) {
        return obj -> g.apply((Bucket)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String name;
        private Instant creationDate;

        private BuilderImpl() {
        }

        private BuilderImpl(Bucket model) {
            this.name(model.name);
            this.creationDate(model.creationDate);
        }

        public final String getName() {
            return this.name;
        }

        public final void setName(String name) {
            this.name = name;
        }

        @Override
        public final Builder name(String name) {
            this.name = name;
            return this;
        }

        public final Instant getCreationDate() {
            return this.creationDate;
        }

        public final void setCreationDate(Instant creationDate) {
            this.creationDate = creationDate;
        }

        @Override
        public final Builder creationDate(Instant creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Bucket build() {
            return new Bucket(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Bucket> {
        public Builder name(String var1);

        public Builder creationDate(Instant var1);
    }
}

