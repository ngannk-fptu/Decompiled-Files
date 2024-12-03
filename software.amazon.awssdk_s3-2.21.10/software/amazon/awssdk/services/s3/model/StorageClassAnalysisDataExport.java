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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.AnalyticsExportDestination;
import software.amazon.awssdk.services.s3.model.StorageClassAnalysisSchemaVersion;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class StorageClassAnalysisDataExport
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, StorageClassAnalysisDataExport> {
    private static final SdkField<String> OUTPUT_SCHEMA_VERSION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("OutputSchemaVersion").getter(StorageClassAnalysisDataExport.getter(StorageClassAnalysisDataExport::outputSchemaVersionAsString)).setter(StorageClassAnalysisDataExport.setter(Builder::outputSchemaVersion)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("OutputSchemaVersion").unmarshallLocationName("OutputSchemaVersion").build(), RequiredTrait.create()}).build();
    private static final SdkField<AnalyticsExportDestination> DESTINATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Destination").getter(StorageClassAnalysisDataExport.getter(StorageClassAnalysisDataExport::destination)).setter(StorageClassAnalysisDataExport.setter(Builder::destination)).constructor(AnalyticsExportDestination::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Destination").unmarshallLocationName("Destination").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(OUTPUT_SCHEMA_VERSION_FIELD, DESTINATION_FIELD));
    private static final long serialVersionUID = 1L;
    private final String outputSchemaVersion;
    private final AnalyticsExportDestination destination;

    private StorageClassAnalysisDataExport(BuilderImpl builder) {
        this.outputSchemaVersion = builder.outputSchemaVersion;
        this.destination = builder.destination;
    }

    public final StorageClassAnalysisSchemaVersion outputSchemaVersion() {
        return StorageClassAnalysisSchemaVersion.fromValue(this.outputSchemaVersion);
    }

    public final String outputSchemaVersionAsString() {
        return this.outputSchemaVersion;
    }

    public final AnalyticsExportDestination destination() {
        return this.destination;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.outputSchemaVersionAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.destination());
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
        if (!(obj instanceof StorageClassAnalysisDataExport)) {
            return false;
        }
        StorageClassAnalysisDataExport other = (StorageClassAnalysisDataExport)obj;
        return Objects.equals(this.outputSchemaVersionAsString(), other.outputSchemaVersionAsString()) && Objects.equals(this.destination(), other.destination());
    }

    public final String toString() {
        return ToString.builder((String)"StorageClassAnalysisDataExport").add("OutputSchemaVersion", (Object)this.outputSchemaVersionAsString()).add("Destination", (Object)this.destination()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "OutputSchemaVersion": {
                return Optional.ofNullable(clazz.cast(this.outputSchemaVersionAsString()));
            }
            case "Destination": {
                return Optional.ofNullable(clazz.cast(this.destination()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<StorageClassAnalysisDataExport, T> g) {
        return obj -> g.apply((StorageClassAnalysisDataExport)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String outputSchemaVersion;
        private AnalyticsExportDestination destination;

        private BuilderImpl() {
        }

        private BuilderImpl(StorageClassAnalysisDataExport model) {
            this.outputSchemaVersion(model.outputSchemaVersion);
            this.destination(model.destination);
        }

        public final String getOutputSchemaVersion() {
            return this.outputSchemaVersion;
        }

        public final void setOutputSchemaVersion(String outputSchemaVersion) {
            this.outputSchemaVersion = outputSchemaVersion;
        }

        @Override
        public final Builder outputSchemaVersion(String outputSchemaVersion) {
            this.outputSchemaVersion = outputSchemaVersion;
            return this;
        }

        @Override
        public final Builder outputSchemaVersion(StorageClassAnalysisSchemaVersion outputSchemaVersion) {
            this.outputSchemaVersion(outputSchemaVersion == null ? null : outputSchemaVersion.toString());
            return this;
        }

        public final AnalyticsExportDestination.Builder getDestination() {
            return this.destination != null ? this.destination.toBuilder() : null;
        }

        public final void setDestination(AnalyticsExportDestination.BuilderImpl destination) {
            this.destination = destination != null ? destination.build() : null;
        }

        @Override
        public final Builder destination(AnalyticsExportDestination destination) {
            this.destination = destination;
            return this;
        }

        public StorageClassAnalysisDataExport build() {
            return new StorageClassAnalysisDataExport(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, StorageClassAnalysisDataExport> {
        public Builder outputSchemaVersion(String var1);

        public Builder outputSchemaVersion(StorageClassAnalysisSchemaVersion var1);

        public Builder destination(AnalyticsExportDestination var1);

        default public Builder destination(Consumer<AnalyticsExportDestination.Builder> destination) {
            return this.destination((AnalyticsExportDestination)((AnalyticsExportDestination.Builder)AnalyticsExportDestination.builder().applyMutation(destination)).build());
        }
    }
}

