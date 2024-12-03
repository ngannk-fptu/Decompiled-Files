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
import software.amazon.awssdk.services.s3.model.TransitionStorageClass;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class NoncurrentVersionTransition
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, NoncurrentVersionTransition> {
    private static final SdkField<Integer> NONCURRENT_DAYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("NoncurrentDays").getter(NoncurrentVersionTransition.getter(NoncurrentVersionTransition::noncurrentDays)).setter(NoncurrentVersionTransition.setter(Builder::noncurrentDays)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NoncurrentDays").unmarshallLocationName("NoncurrentDays").build()}).build();
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(NoncurrentVersionTransition.getter(NoncurrentVersionTransition::storageClassAsString)).setter(NoncurrentVersionTransition.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("StorageClass").unmarshallLocationName("StorageClass").build()}).build();
    private static final SdkField<Integer> NEWER_NONCURRENT_VERSIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("NewerNoncurrentVersions").getter(NoncurrentVersionTransition.getter(NoncurrentVersionTransition::newerNoncurrentVersions)).setter(NoncurrentVersionTransition.setter(Builder::newerNoncurrentVersions)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NewerNoncurrentVersions").unmarshallLocationName("NewerNoncurrentVersions").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(NONCURRENT_DAYS_FIELD, STORAGE_CLASS_FIELD, NEWER_NONCURRENT_VERSIONS_FIELD));
    private static final long serialVersionUID = 1L;
    private final Integer noncurrentDays;
    private final String storageClass;
    private final Integer newerNoncurrentVersions;

    private NoncurrentVersionTransition(BuilderImpl builder) {
        this.noncurrentDays = builder.noncurrentDays;
        this.storageClass = builder.storageClass;
        this.newerNoncurrentVersions = builder.newerNoncurrentVersions;
    }

    public final Integer noncurrentDays() {
        return this.noncurrentDays;
    }

    public final TransitionStorageClass storageClass() {
        return TransitionStorageClass.fromValue(this.storageClass);
    }

    public final String storageClassAsString() {
        return this.storageClass;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAsString());
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
        if (!(obj instanceof NoncurrentVersionTransition)) {
            return false;
        }
        NoncurrentVersionTransition other = (NoncurrentVersionTransition)obj;
        return Objects.equals(this.noncurrentDays(), other.noncurrentDays()) && Objects.equals(this.storageClassAsString(), other.storageClassAsString()) && Objects.equals(this.newerNoncurrentVersions(), other.newerNoncurrentVersions());
    }

    public final String toString() {
        return ToString.builder((String)"NoncurrentVersionTransition").add("NoncurrentDays", (Object)this.noncurrentDays()).add("StorageClass", (Object)this.storageClassAsString()).add("NewerNoncurrentVersions", (Object)this.newerNoncurrentVersions()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "NoncurrentDays": {
                return Optional.ofNullable(clazz.cast(this.noncurrentDays()));
            }
            case "StorageClass": {
                return Optional.ofNullable(clazz.cast(this.storageClassAsString()));
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

    private static <T> Function<Object, T> getter(Function<NoncurrentVersionTransition, T> g) {
        return obj -> g.apply((NoncurrentVersionTransition)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Integer noncurrentDays;
        private String storageClass;
        private Integer newerNoncurrentVersions;

        private BuilderImpl() {
        }

        private BuilderImpl(NoncurrentVersionTransition model) {
            this.noncurrentDays(model.noncurrentDays);
            this.storageClass(model.storageClass);
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

        public final String getStorageClass() {
            return this.storageClass;
        }

        public final void setStorageClass(String storageClass) {
            this.storageClass = storageClass;
        }

        @Override
        public final Builder storageClass(String storageClass) {
            this.storageClass = storageClass;
            return this;
        }

        @Override
        public final Builder storageClass(TransitionStorageClass storageClass) {
            this.storageClass(storageClass == null ? null : storageClass.toString());
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

        public NoncurrentVersionTransition build() {
            return new NoncurrentVersionTransition(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, NoncurrentVersionTransition> {
        public Builder noncurrentDays(Integer var1);

        public Builder storageClass(String var1);

        public Builder storageClass(TransitionStorageClass var1);

        public Builder newerNoncurrentVersions(Integer var1);
    }
}

