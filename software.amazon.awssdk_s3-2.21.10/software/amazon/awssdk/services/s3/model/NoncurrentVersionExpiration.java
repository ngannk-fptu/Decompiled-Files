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

public final class NoncurrentVersionExpiration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, NoncurrentVersionExpiration> {
    private static final SdkField<Integer> NONCURRENT_DAYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("NoncurrentDays").getter(NoncurrentVersionExpiration.getter(NoncurrentVersionExpiration::noncurrentDays)).setter(NoncurrentVersionExpiration.setter(Builder::noncurrentDays)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NoncurrentDays").unmarshallLocationName("NoncurrentDays").build()}).build();
    private static final SdkField<Integer> NEWER_NONCURRENT_VERSIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("NewerNoncurrentVersions").getter(NoncurrentVersionExpiration.getter(NoncurrentVersionExpiration::newerNoncurrentVersions)).setter(NoncurrentVersionExpiration.setter(Builder::newerNoncurrentVersions)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NewerNoncurrentVersions").unmarshallLocationName("NewerNoncurrentVersions").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(NONCURRENT_DAYS_FIELD, NEWER_NONCURRENT_VERSIONS_FIELD));
    private static final long serialVersionUID = 1L;
    private final Integer noncurrentDays;
    private final Integer newerNoncurrentVersions;

    private NoncurrentVersionExpiration(BuilderImpl builder) {
        this.noncurrentDays = builder.noncurrentDays;
        this.newerNoncurrentVersions = builder.newerNoncurrentVersions;
    }

    public final Integer noncurrentDays() {
        return this.noncurrentDays;
    }

    public final Integer newerNoncurrentVersions() {
        return this.newerNoncurrentVersions;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.noncurrentDays());
        hashCode = 31 * hashCode + Objects.hashCode(this.newerNoncurrentVersions());
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
        if (!(obj instanceof NoncurrentVersionExpiration)) {
            return false;
        }
        NoncurrentVersionExpiration other = (NoncurrentVersionExpiration)obj;
        return Objects.equals(this.noncurrentDays(), other.noncurrentDays()) && Objects.equals(this.newerNoncurrentVersions(), other.newerNoncurrentVersions());
    }

    public final String toString() {
        return ToString.builder((String)"NoncurrentVersionExpiration").add("NoncurrentDays", (Object)this.noncurrentDays()).add("NewerNoncurrentVersions", (Object)this.newerNoncurrentVersions()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "NoncurrentDays": {
                return Optional.ofNullable(clazz.cast(this.noncurrentDays()));
            }
            case "NewerNoncurrentVersions": {
                return Optional.ofNullable(clazz.cast(this.newerNoncurrentVersions()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<NoncurrentVersionExpiration, T> g) {
        return obj -> g.apply((NoncurrentVersionExpiration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Integer noncurrentDays;
        private Integer newerNoncurrentVersions;

        private BuilderImpl() {
        }

        private BuilderImpl(NoncurrentVersionExpiration model) {
            this.noncurrentDays(model.noncurrentDays);
            this.newerNoncurrentVersions(model.newerNoncurrentVersions);
        }

        public final Integer getNoncurrentDays() {
            return this.noncurrentDays;
        }

        public final void setNoncurrentDays(Integer noncurrentDays) {
            this.noncurrentDays = noncurrentDays;
        }

        @Override
        public final Builder noncurrentDays(Integer noncurrentDays) {
            this.noncurrentDays = noncurrentDays;
            return this;
        }

        public final Integer getNewerNoncurrentVersions() {
            return this.newerNoncurrentVersions;
        }

        public final void setNewerNoncurrentVersions(Integer newerNoncurrentVersions) {
            this.newerNoncurrentVersions = newerNoncurrentVersions;
        }

        @Override
        public final Builder newerNoncurrentVersions(Integer newerNoncurrentVersions) {
            this.newerNoncurrentVersions = newerNoncurrentVersions;
            return this;
        }

        public NoncurrentVersionExpiration build() {
            return new NoncurrentVersionExpiration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, NoncurrentVersionExpiration> {
        public Builder noncurrentDays(Integer var1);

        public Builder newerNoncurrentVersions(Integer var1);
    }
}

