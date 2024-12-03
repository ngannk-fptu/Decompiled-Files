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
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class IndexDocument
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, IndexDocument> {
    private static final SdkField<String> SUFFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Suffix").getter(IndexDocument.getter(IndexDocument::suffix)).setter(IndexDocument.setter(Builder::suffix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Suffix").unmarshallLocationName("Suffix").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SUFFIX_FIELD));
    private static final long serialVersionUID = 1L;
    private final String suffix;

    private IndexDocument(BuilderImpl builder) {
        this.suffix = builder.suffix;
    }

    public final String suffix() {
        return this.suffix;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.suffix());
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
        if (!(obj instanceof IndexDocument)) {
            return false;
        }
        IndexDocument other = (IndexDocument)obj;
        return Objects.equals(this.suffix(), other.suffix());
    }

    public final String toString() {
        return ToString.builder((String)"IndexDocument").add("Suffix", (Object)this.suffix()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Suffix": {
                return Optional.ofNullable(clazz.cast(this.suffix()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<IndexDocument, T> g) {
        return obj -> g.apply((IndexDocument)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String suffix;

        private BuilderImpl() {
        }

        private BuilderImpl(IndexDocument model) {
            this.suffix(model.suffix);
        }

        public final String getSuffix() {
            return this.suffix;
        }

        public final void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public final Builder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public IndexDocument build() {
            return new IndexDocument(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, IndexDocument> {
        public Builder suffix(String var1);
    }
}

