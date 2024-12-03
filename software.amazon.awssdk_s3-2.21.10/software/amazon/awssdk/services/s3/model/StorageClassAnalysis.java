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
import software.amazon.awssdk.services.s3.model.StorageClassAnalysisDataExport;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class StorageClassAnalysis
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, StorageClassAnalysis> {
    private static final SdkField<StorageClassAnalysisDataExport> DATA_EXPORT_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("DataExport").getter(StorageClassAnalysis.getter(StorageClassAnalysis::dataExport)).setter(StorageClassAnalysis.setter(Builder::dataExport)).constructor(StorageClassAnalysisDataExport::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DataExport").unmarshallLocationName("DataExport").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DATA_EXPORT_FIELD));
    private static final long serialVersionUID = 1L;
    private final StorageClassAnalysisDataExport dataExport;

    private StorageClassAnalysis(BuilderImpl builder) {
        this.dataExport = builder.dataExport;
    }

    public final StorageClassAnalysisDataExport dataExport() {
        return this.dataExport;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.dataExport());
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
        if (!(obj instanceof StorageClassAnalysis)) {
            return false;
        }
        StorageClassAnalysis other = (StorageClassAnalysis)obj;
        return Objects.equals(this.dataExport(), other.dataExport());
    }

    public final String toString() {
        return ToString.builder((String)"StorageClassAnalysis").add("DataExport", (Object)this.dataExport()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "DataExport": {
                return Optional.ofNullable(clazz.cast(this.dataExport()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<StorageClassAnalysis, T> g) {
        return obj -> g.apply((StorageClassAnalysis)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private StorageClassAnalysisDataExport dataExport;

        private BuilderImpl() {
        }

        private BuilderImpl(StorageClassAnalysis model) {
            this.dataExport(model.dataExport);
        }

        public final StorageClassAnalysisDataExport.Builder getDataExport() {
            return this.dataExport != null ? this.dataExport.toBuilder() : null;
        }

        public final void setDataExport(StorageClassAnalysisDataExport.BuilderImpl dataExport) {
            this.dataExport = dataExport != null ? dataExport.build() : null;
        }

        @Override
        public final Builder dataExport(StorageClassAnalysisDataExport dataExport) {
            this.dataExport = dataExport;
            return this;
        }

        public StorageClassAnalysis build() {
            return new StorageClassAnalysis(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, StorageClassAnalysis> {
        public Builder dataExport(StorageClassAnalysisDataExport var1);

        default public Builder dataExport(Consumer<StorageClassAnalysisDataExport.Builder> dataExport) {
            return this.dataExport((StorageClassAnalysisDataExport)((StorageClassAnalysisDataExport.Builder)StorageClassAnalysisDataExport.builder().applyMutation(dataExport)).build());
        }
    }
}

