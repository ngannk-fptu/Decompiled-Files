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
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CompletedPartListCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CompletedMultipartUpload
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, CompletedMultipartUpload> {
    private static final SdkField<List<CompletedPart>> PARTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Parts").getter(CompletedMultipartUpload.getter(CompletedMultipartUpload::parts)).setter(CompletedMultipartUpload.setter(Builder::parts)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Part").unmarshallLocationName("Part").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(CompletedPart::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(PARTS_FIELD));
    private static final long serialVersionUID = 1L;
    private final List<CompletedPart> parts;

    private CompletedMultipartUpload(BuilderImpl builder) {
        this.parts = builder.parts;
    }

    public final boolean hasParts() {
        return this.parts != null && !(this.parts instanceof SdkAutoConstructList);
    }

    public final List<CompletedPart> parts() {
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
        if (!(obj instanceof CompletedMultipartUpload)) {
            return false;
        }
        CompletedMultipartUpload other = (CompletedMultipartUpload)obj;
        return this.hasParts() == other.hasParts() && Objects.equals(this.parts(), other.parts());
    }

    public final String toString() {
        return ToString.builder((String)"CompletedMultipartUpload").add("Parts", this.hasParts() ? this.parts() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Parts": {
                return Optional.ofNullable(clazz.cast(this.parts()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CompletedMultipartUpload, T> g) {
        return obj -> g.apply((CompletedMultipartUpload)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private List<CompletedPart> parts = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(CompletedMultipartUpload model) {
            this.parts(model.parts);
        }

        public final List<CompletedPart.Builder> getParts() {
            List<CompletedPart.Builder> result = CompletedPartListCopier.copyToBuilder(this.parts);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setParts(Collection<CompletedPart.BuilderImpl> parts) {
            this.parts = CompletedPartListCopier.copyFromBuilder(parts);
        }

        @Override
        public final Builder parts(Collection<CompletedPart> parts) {
            this.parts = CompletedPartListCopier.copy(parts);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder parts(CompletedPart ... parts) {
            this.parts(Arrays.asList(parts));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder parts(Consumer<CompletedPart.Builder> ... parts) {
            this.parts(Stream.of(parts).map(c -> (CompletedPart)((CompletedPart.Builder)CompletedPart.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public CompletedMultipartUpload build() {
            return new CompletedMultipartUpload(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, CompletedMultipartUpload> {
        public Builder parts(Collection<CompletedPart> var1);

        public Builder parts(CompletedPart ... var1);

        public Builder parts(Consumer<CompletedPart.Builder> ... var1);
    }
}

