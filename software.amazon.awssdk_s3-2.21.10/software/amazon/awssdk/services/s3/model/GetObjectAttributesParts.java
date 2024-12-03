/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.ObjectPart;
import software.amazon.awssdk.services.s3.model.PartsListCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetObjectAttributesParts
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, GetObjectAttributesParts> {
    private static final SdkField<Integer> TOTAL_PARTS_COUNT_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("TotalPartsCount").getter(GetObjectAttributesParts.getter(GetObjectAttributesParts::totalPartsCount)).setter(GetObjectAttributesParts.setter(Builder::totalPartsCount)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PartsCount").unmarshallLocationName("PartsCount").build()}).build();
    private static final SdkField<Integer> PART_NUMBER_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("PartNumberMarker").getter(GetObjectAttributesParts.getter(GetObjectAttributesParts::partNumberMarker)).setter(GetObjectAttributesParts.setter(Builder::partNumberMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PartNumberMarker").unmarshallLocationName("PartNumberMarker").build()}).build();
    private static final SdkField<Integer> NEXT_PART_NUMBER_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("NextPartNumberMarker").getter(GetObjectAttributesParts.getter(GetObjectAttributesParts::nextPartNumberMarker)).setter(GetObjectAttributesParts.setter(Builder::nextPartNumberMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextPartNumberMarker").unmarshallLocationName("NextPartNumberMarker").build()}).build();
    private static final SdkField<Integer> MAX_PARTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxParts").getter(GetObjectAttributesParts.getter(GetObjectAttributesParts::maxParts)).setter(GetObjectAttributesParts.setter(Builder::maxParts)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MaxParts").unmarshallLocationName("MaxParts").build()}).build();
    private static final SdkField<Boolean> IS_TRUNCATED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsTruncated").getter(GetObjectAttributesParts.getter(GetObjectAttributesParts::isTruncated)).setter(GetObjectAttributesParts.setter(Builder::isTruncated)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsTruncated").unmarshallLocationName("IsTruncated").build()}).build();
    private static final SdkField<List<ObjectPart>> PARTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Parts").getter(GetObjectAttributesParts.getter(GetObjectAttributesParts::parts)).setter(GetObjectAttributesParts.setter(Builder::parts)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Part").unmarshallLocationName("Part").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(ObjectPart::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(TOTAL_PARTS_COUNT_FIELD, PART_NUMBER_MARKER_FIELD, NEXT_PART_NUMBER_MARKER_FIELD, MAX_PARTS_FIELD, IS_TRUNCATED_FIELD, PARTS_FIELD));
    private static final long serialVersionUID = 1L;
    private final Integer totalPartsCount;
    private final Integer partNumberMarker;
    private final Integer nextPartNumberMarker;
    private final Integer maxParts;
    private final Boolean isTruncated;
    private final List<ObjectPart> parts;

    private GetObjectAttributesParts(BuilderImpl builder) {
        this.totalPartsCount = builder.totalPartsCount;
        this.partNumberMarker = builder.partNumberMarker;
        this.nextPartNumberMarker = builder.nextPartNumberMarker;
        this.maxParts = builder.maxParts;
        this.isTruncated = builder.isTruncated;
        this.parts = builder.parts;
    }

    public final Integer totalPartsCount() {
        return this.totalPartsCount;
    }

    public final Integer partNumberMarker() {
        return this.partNumberMarker;
    }

    public final Integer nextPartNumberMarker() {
        return this.nextPartNumberMarker;
    }

    public final Integer maxParts() {
        return this.maxParts;
    }

    public final Boolean isTruncated() {
        return this.isTruncated;
    }

    public final boolean hasParts() {
        return this.parts != null && !(this.parts instanceof SdkAutoConstructList);
    }

    public final List<ObjectPart> parts() {
        return this.parts;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.totalPartsCount());
        hashCode = 31 * hashCode + Objects.hashCode(this.partNumberMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextPartNumberMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.maxParts());
        hashCode = 31 * hashCode + Objects.hashCode(this.isTruncated());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasParts() ? this.parts() : null);
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
        if (!(obj instanceof GetObjectAttributesParts)) {
            return false;
        }
        GetObjectAttributesParts other = (GetObjectAttributesParts)obj;
        return Objects.equals(this.totalPartsCount(), other.totalPartsCount()) && Objects.equals(this.partNumberMarker(), other.partNumberMarker()) && Objects.equals(this.nextPartNumberMarker(), other.nextPartNumberMarker()) && Objects.equals(this.maxParts(), other.maxParts()) && Objects.equals(this.isTruncated(), other.isTruncated()) && this.hasParts() == other.hasParts() && Objects.equals(this.parts(), other.parts());
    }

    public final String toString() {
        return ToString.builder((String)"GetObjectAttributesParts").add("TotalPartsCount", (Object)this.totalPartsCount()).add("PartNumberMarker", (Object)this.partNumberMarker()).add("NextPartNumberMarker", (Object)this.nextPartNumberMarker()).add("MaxParts", (Object)this.maxParts()).add("IsTruncated", (Object)this.isTruncated()).add("Parts", this.hasParts() ? this.parts() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "TotalPartsCount": {
                return Optional.ofNullable(clazz.cast(this.totalPartsCount()));
            }
            case "PartNumberMarker": {
                return Optional.ofNullable(clazz.cast(this.partNumberMarker()));
            }
            case "NextPartNumberMarker": {
                return Optional.ofNullable(clazz.cast(this.nextPartNumberMarker()));
            }
            case "MaxParts": {
                return Optional.ofNullable(clazz.cast(this.maxParts()));
            }
            case "IsTruncated": {
                return Optional.ofNullable(clazz.cast(this.isTruncated()));
            }
            case "Parts": {
                return Optional.ofNullable(clazz.cast(this.parts()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetObjectAttributesParts, T> g) {
        return obj -> g.apply((GetObjectAttributesParts)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Integer totalPartsCount;
        private Integer partNumberMarker;
        private Integer nextPartNumberMarker;
        private Integer maxParts;
        private Boolean isTruncated;
        private List<ObjectPart> parts = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(GetObjectAttributesParts model) {
            this.totalPartsCount(model.totalPartsCount);
            this.partNumberMarker(model.partNumberMarker);
            this.nextPartNumberMarker(model.nextPartNumberMarker);
            this.maxParts(model.maxParts);
            this.isTruncated(model.isTruncated);
            this.parts(model.parts);
        }

        public final Integer getTotalPartsCount() {
            return this.totalPartsCount;
        }

        public final void setTotalPartsCount(Integer totalPartsCount) {
            this.totalPartsCount = totalPartsCount;
        }

        @Override
        public final Builder totalPartsCount(Integer totalPartsCount) {
            this.totalPartsCount = totalPartsCount;
            return this;
        }

        public final Integer getPartNumberMarker() {
            return this.partNumberMarker;
        }

        public final void setPartNumberMarker(Integer partNumberMarker) {
            this.partNumberMarker = partNumberMarker;
        }

        @Override
        public final Builder partNumberMarker(Integer partNumberMarker) {
            this.partNumberMarker = partNumberMarker;
            return this;
        }

        public final Integer getNextPartNumberMarker() {
            return this.nextPartNumberMarker;
        }

        public final void setNextPartNumberMarker(Integer nextPartNumberMarker) {
            this.nextPartNumberMarker = nextPartNumberMarker;
        }

        @Override
        public final Builder nextPartNumberMarker(Integer nextPartNumberMarker) {
            this.nextPartNumberMarker = nextPartNumberMarker;
            return this;
        }

        public final Integer getMaxParts() {
            return this.maxParts;
        }

        public final void setMaxParts(Integer maxParts) {
            this.maxParts = maxParts;
        }

        @Override
        public final Builder maxParts(Integer maxParts) {
            this.maxParts = maxParts;
            return this;
        }

        public final Boolean getIsTruncated() {
            return this.isTruncated;
        }

        public final void setIsTruncated(Boolean isTruncated) {
            this.isTruncated = isTruncated;
        }

        @Override
        public final Builder isTruncated(Boolean isTruncated) {
            this.isTruncated = isTruncated;
            return this;
        }

        public final List<ObjectPart.Builder> getParts() {
            List<ObjectPart.Builder> result = PartsListCopier.copyToBuilder(this.parts);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setParts(Collection<ObjectPart.BuilderImpl> parts) {
            this.parts = PartsListCopier.copyFromBuilder(parts);
        }

        @Override
        public final Builder parts(Collection<ObjectPart> parts) {
            this.parts = PartsListCopier.copy(parts);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder parts(ObjectPart ... parts) {
            this.parts(Arrays.asList(parts));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder parts(Consumer<ObjectPart.Builder> ... parts) {
            this.parts(Stream.of(parts).map(c -> (ObjectPart)((ObjectPart.Builder)ObjectPart.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public GetObjectAttributesParts build() {
            return new GetObjectAttributesParts(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, GetObjectAttributesParts> {
        public Builder totalPartsCount(Integer var1);

        public Builder partNumberMarker(Integer var1);

        public Builder nextPartNumberMarker(Integer var1);

        public Builder maxParts(Integer var1);

        public Builder isTruncated(Boolean var1);

        public Builder parts(Collection<ObjectPart> var1);

        public Builder parts(ObjectPart ... var1);

        public Builder parts(Consumer<ObjectPart.Builder> ... var1);
    }
}

